package us.dot.its.jpo.ode.udp.emulators.bsm;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

/*
 * Simple test app that acts as an OBU for sending uper encoded bsms to ODE
 */
public class BsmDepositorToOde {
	final static int BUFFER_SIZE = 1000;
	private static String odeIp = "127.0.0.1";
	private static int odePort = 46800;
	private static int selfPort = 12321;

	public static void main(String[] args) {
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
	}

	@SuppressWarnings("static-access")
	public static void sendBsm() {
		String uperBsmMsgFrameHex = "030000ac000c001700000000000000200026ad01f13c00b1001480ad44b9bf800018b5277c5ea49cc866fb8d317ffffffff000ae5afdfa1fa1007fff8000000000020214c1c0fff64000cffa20ce100004005f0188ae60fffabff75030ac6a0ffd1c003eff2f77e1001b3ff63005449a1000ebff06fef5e4e1002fbff8afff8c520ffa840094fedbfe210012c003b02e0f1a0ffbf4007701e41ce0ffe64014d03675720fff54002503c8d820ffdf3ff7d004caae0ffd140029006e6ce0ffbabff94fe33fb6fffe00000000";
		
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
			uperBsmByte = Hex.decodeHex(uperBsmMsgFrameHex.toCharArray());
		} catch (DecoderException e) {
			System.out.println("OBU - Error decoding hex string into bytes");
			e.printStackTrace();
		}

		DatagramPacket reqPacket = new DatagramPacket(uperBsmByte, uperBsmByte.length,
				new InetSocketAddress(odeIp, odePort));
		boolean stopped = false;
		while (!stopped) {
			System.out.println("OBU - Printing uperBsm in hex: \n" + uperBsmMsgFrameHex);
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

}
