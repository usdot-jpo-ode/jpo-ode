package us.dot.its.jpo.ode.bsm.bsmDepositorToOde;

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
	// String odeIp = "2001:4802:7801:102:be76:4eff:fe20:eb5"; // ode IPV6
	// String odeIp = "162.242.218.130"; // ode instance IPv4
	private static int selfPort = 12321;

	public static void main(String[] args) {
		System.out.println("args length: " + args.length);
		if (args.length < 3) {
			System.out.println("Usage Error. Expected: ObuBsmEmulator <OdeIP> <OdePort> <SelfPort>");
			return;
		}
		odeIp = args[0];
		odePort = Integer.parseInt(args[1]);
		selfPort = Integer.parseInt(args[2]);
		sendBsm();
	}

	public static void sendBsm() {
		String uperBsmHex = "401480CA4000000000000000000000000000000000000000000000000000000000000000F800D9EFFFB7FFF00000000000000000000000000000000000000000000000000000001FE07000000000000000000000000000000000001FF0";

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
			uperBsmByte = Hex.decodeHex(uperBsmHex.toCharArray());
		} catch (DecoderException e) {
			System.out.println("OBU - Error decoding hex string into bytes");
			e.printStackTrace();
		}

		DatagramPacket reqPacket = new DatagramPacket(uperBsmByte, uperBsmByte.length,
				new InetSocketAddress(odeIp, odePort));
		boolean stopped = false;
		while (!stopped) {
			System.out.println("OBU - Printing uperBsm in hex: \n" + uperBsmHex);
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
