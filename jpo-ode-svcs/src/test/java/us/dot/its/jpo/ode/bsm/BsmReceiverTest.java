package us.dot.its.jpo.ode.bsm;

import static org.junit.Assert.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BsmReceiverTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		int port = 12321;
		//String odeIp = "2001:4802:7801:102:be76:4eff:fe20:eb5"; // ode instance
		//String odeIp = "162.242.218.130";
		String odeIp = "127.0.0.1";
		int odePort = 46800;

		String uperBsmHex ="401480CA4000000000000000000000000000000000000000000000000000000000000000F800D9EFFFB7FFF00000000000000000000000000000000000000000000000000000001FE07000000000000000000000000000000000001FF0"; 

		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(port);
			System.out.println("OBU - Started socket with port " + port);
		} catch (SocketException e) {
			System.out.println("OBU - Error creating socket with port " + port);
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
		System.out.println("OBU - Printing uperBsm in hex: \n" + uperBsmHex);
		System.out.println("\nOBU - Sending uperBsm to ODE - Ip: " + odeIp + " Port: " + odePort);
		try {
			socket.send(reqPacket);
		} catch (IOException e) {
			System.out.println("OBU - Error Sending uperBsm to ODE");
			e.printStackTrace();
		}

		if (socket != null) {
			socket.close();
			System.out.println("OBU - Closed socket with port " + port);
		}
	}

}




