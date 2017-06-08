package us.dot.its.jpo.ode.udp.emulators.bsm;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735MessageFrame;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssAsn1Coder;

/*
 * Simple test app that acts as an OBU for sending uper encoded bsms to ODE
 */
public class BsmDepositorToOde {
	final static int BUFFER_SIZE = 1000;
	private static String odeIp = "127.0.0.1";
	private static int odePort = 46800;
	private static int selfPort = 12321;

	public static void main(String[] args) throws DecoderException {
		if(args.length < 3){
			System.out.println("Usage Error. Expected: BsmDepositorToOde <OdeIP> <OdePort> <SelfPort>");
            System.out.println("Using defaults: <"
                    + odeIp
                    + "> <"
                    + odePort
                    + "> <"
                    + selfPort
                    + ">");
		} else {
    		odeIp = args[0];
    		odePort = Integer.parseInt(args[1]);
    		selfPort = Integer.parseInt(args[2]);
		}
		sendBsm();
		//testDecode();
	}

	@SuppressWarnings("static-access")
	public static void sendBsm() {
		//<rsu header> + <msg frame> + <bsm>
		String bsmPayloadHex = "030000ac000c001700000000000000200026ad01f13c00b1001480ad5dbbf140001450277c5e941cc867000d1cfffffffff0007080fdfa1fa1007fff8000000000020214c1c0ffc6bffc2f963c8a0ffaa401cef82612e0ffecc0142fca963a1002cbff6efaeb34a1001dbff4afb8d5fa0ffe2bffc2fc4ec020ff7ec00e0fb50b420ffe1bffe4fe347560ffc4c0048fa587f20ffb4c00dcfbdc7ee0ffda3feccfbe11220ffdf3fe9cfde5dc60ffc13ff90fd0777a0fffe3ff38fdea7fe0ffe73ff84fb8f51afffe00000000";

		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(selfPort);
			System.out.println("OBU - Started socket with port " + selfPort);
		} catch (SocketException e) {
			System.out.println("OBU - Error creating socket with port " + selfPort);
			e.printStackTrace();
		}

		byte[] uperBsmByte = null;
		try {
			uperBsmByte = Hex.decodeHex(bsmPayloadHex.toCharArray());
		} catch (DecoderException e) {
			System.out.println("OBU - Error decoding hex string into bytes");
			e.printStackTrace();
		}

		DatagramPacket reqPacket = new DatagramPacket(uperBsmByte, uperBsmByte.length,
				new InetSocketAddress(odeIp, odePort));
		boolean stopped = false;
		while (!stopped) {
			System.out.println("OBU - Printing uperBsm in hex: \n" + bsmPayloadHex);
			System.out.println("\nOBU - Sending uperBsm to ODE - Ip: " + odeIp + " Port: " + odePort);
			try {
				socket.send(reqPacket);
			} catch (IOException e) {
				stopped = true;
				System.out.println("OBU - Error Sending uperBsm to ODE");
				e.printStackTrace();
			}
			try {
				Thread.currentThread().sleep(5000);
			} catch (InterruptedException e) {
				System.out.println("OBU - Thread sleep error");
				e.printStackTrace();
			}
		}

		if (socket != null) {
			socket.close();
			System.out.println("OBU - Closed socket with port " + selfPort);
		}
	}

	/*
	 * Test to see if the bsms received from OBU can be decoded.
	 */
	public static void testDecode() throws DecoderException{
		//<rsu header> + <msg frame> + <bsm>
		String bsmPayloadHex = "030000ac000c001700000000000000200026ad01f13c00b1001480ad5dbbf140001450277c5e941cc867000d1cfffffffff0007080fdfa1fa1007fff8000000000020214c1c0ffc6bffc2f963c8a0ffaa401cef82612e0ffecc0142fca963a1002cbff6efaeb34a1001dbff4afb8d5fa0ffe2bffc2fc4ec020ff7ec00e0fb50b420ffe1bffe4fe347560ffc4c0048fa587f20ffb4c00dcfbdc7ee0ffda3feccfbe11220ffdf3fe9cfde5dc60ffc13ff90fd0777a0fffe3ff38fdea7fe0ffe73ff84fb8f51afffe00000000";

		byte[] payload = Hex.decodeHex(bsmPayloadHex.toCharArray());
		OssAsn1Coder asn1Coder = new OssAsn1Coder();
		Asn1Object decoded = asn1Coder.decodeUPERBsmBytes(payload);
		
		if (decoded instanceof J2735MessageFrame) {
			System.out.println("Received J2735BsmMessageFrame");
		} else if (decoded instanceof J2735Bsm) {
			System.out.println("Received J2735Bsm");
		} else {
			System.out.println("Unknown message type received " + decoded.getClass().getName());
		}
	}
}
