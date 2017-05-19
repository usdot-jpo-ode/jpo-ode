import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class VsdDepositorToOde {

	public static void main(String[] args) {
		depositTest();
	}

	public static void depositTest() {
		final int BUFFER_SIZE = 1000;
		int port = 12321;
		String odeIp = "2001:4802:7801:102:be76:4eff:fe20:eb5"; // ode instance
		int odePort = 46753;
		String encodedRequestHex = "80000000000020202033477d77398108";
		String encodedVsdHex = "01000000000040404040a008004f85bfbeee252f000f21c53e0981e0aca0813c4d20bffe01a1f9f45f46a9ffffe002081d8040027c2dfdf771297800790e29de3dc86731e8c1a26905fff00d0fcfa2fa354fffff001040ec020013e16fefbb894bc003c8714ef1ef6f398f462313492fff80687e7d17d1aa7ffff80082076010009f0b7f7ddc4a5e001e438a778f5f41cc7a2fd89a517ffc0343f3e8be8d53ffffc004103b008004f85bfbeee252f000f21c53bc7aaece63d17e84d28bffe01a1f9f45f46a9ffffe002081d8040027c2dfdf771297800790e29de3d30a731e8be626925fff00d0fcfa2fa354fffff001040ec020013e16fefbb894bc003a8714ef1e858398f45f213492fff80687e7d17d1aa7ffff80082076010009f0b7f7ddc4a5e001d438a778f38b9cc7a2f589a497ffc0343f3e8be8d53ffffc004103b008004f85bfbeee252f000ea1c53bc797d0e63d17784d24bffe01a1f9f45f46a9ffffe002081d862640";

		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(port);
			System.out.println("OBU - Started socket with port " + port);
		} catch (SocketException e) {
			System.out.println("OBU - Error creating socket with port " + port);
			e.printStackTrace();
		}

		byte[] encodedRequestByte = null;
		byte[] encodedVsdByte = null;
		try {
			encodedRequestByte = Hex.decodeHex(encodedRequestHex.toCharArray());
			encodedVsdByte = Hex.decodeHex(encodedVsdHex.toCharArray());
		} catch (DecoderException e) {
			System.out.println("OBU - Error decoding hex string into bytes");
			e.printStackTrace();
		}

		DatagramPacket reqPacket = new DatagramPacket(encodedRequestByte, encodedRequestByte.length,
				new InetSocketAddress(odeIp, odePort));
		System.out.println("OBU - Printing Service Request in hex: \n" + encodedRequestHex);
		System.out.println("\nOBU - Sending Service Request to ODE - Ip: " + odeIp + " Port: " + odePort);
		try {
			socket.send(reqPacket);
		} catch (IOException e) {
			System.out.println("OBU - Error Sending Service Request to ODE");
			e.printStackTrace();
		}

		byte[] buffer = new byte[BUFFER_SIZE];
		System.out.println("\nOBU - Waiting for Service Response from ODE...");
		DatagramPacket resPacket = new DatagramPacket(buffer, buffer.length);
		try {
			socket.receive(resPacket);
			byte[] actualPacket = Arrays.copyOf(resPacket.getData(), resPacket.getLength());
			System.out.println("OBU - Received Service Response from ODE");
			System.out.println("OBU - Printing Service Response in hex: \n" + Hex.encodeHexString(actualPacket));
		} catch (IOException e) {
			System.out.println("\nOBU - Error Receiving Service Response from ODE");
			e.printStackTrace();
		}

		DatagramPacket vsdPacket = new DatagramPacket(encodedVsdByte, encodedVsdByte.length,
				new InetSocketAddress(odeIp, odePort));
		try {
			socket.send(vsdPacket);
			System.out.println("\nOBU - Printing VSD in hex: \n" + encodedVsdHex);
			System.out.println("\nOBU - Sent VSD to ODE - Ip: " + odeIp + " Port: " + odePort);
		} catch (IOException e) {
			System.out.println("OBU - Error Sending VSD to ODE");
			e.printStackTrace();
		}

		if (socket != null) {
			socket.close();
			System.out.println("OBU - Closed socket with port " + port);
		}
	}
}

