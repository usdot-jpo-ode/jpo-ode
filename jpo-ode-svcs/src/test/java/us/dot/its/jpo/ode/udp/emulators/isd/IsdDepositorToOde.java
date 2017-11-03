package us.dot.its.jpo.ode.udp.emulators.isd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class IsdDepositorToOde {
	final static int BUFFER_SIZE = 1000;
	private static String odeIp = "127.0.0.1";
	private static int odePort = 46801;
	private static int selfPort = 12321;
	
	public static void main(String[] args) {
		if(args.length < 3){
			System.out.println("Usage Error. Expected: IsdDepositorToOde <OdeIP> <OdePort> <SelfPort>");
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
		depositTest();
	}

	public static void depositTest() {
		// String encodedRequestHex = "800000000000202020230210";
		String encodedRequestHex = "20000000000020202020";
		String encodedIsdHex = "2088260186b49c479c0010c3853981e64aace3f0e05dbfa994558774e000c0008061a804e0c4e78d93760e4c63780b70296008a10010090006639a567649df90bbc639a56a539df90af6639a56b5f9df9067c639a56bb09df9009c639a56c369df8dc500800042c021420020120010c734acec93bf22248c734ad5e93bf22158c734ad9e33bf21a8cc734adb253bf20fc0c734adb5b3bf20138c734adc9d3bf1b940100008580648400404000018e695924277e462d18e6958ee677e4ff20200008b01090800808000031cd2b1644efc8c5a31cd2b1064efca00c04000116029210010100000639a560e59df9188e639a560299df93ff00800022c062420020080000c734abe713bf23168c734abd653bf26904100005f9f8584e4a32800030d40800000802022a00368036800810d0016401640";

		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(selfPort);
			System.out.println("OBU - Started socket with port " + selfPort);
		} catch (SocketException e) {
			System.out.println("OBU - Error creating socket with port " + selfPort);
			e.printStackTrace();
		}

		byte[] encodedRequestByte = null;
		byte[] encodedIsdByte = null;
		try {
			encodedRequestByte = Hex.decodeHex(encodedRequestHex.toCharArray());
			encodedIsdByte = Hex.decodeHex(encodedIsdHex.toCharArray());
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

		DatagramPacket isdPacket = new DatagramPacket(encodedIsdByte, encodedIsdByte.length,
				new InetSocketAddress(odeIp, odePort));
		try {
			socket.send(isdPacket);
			System.out.println("\nOBU - Printing ISD in hex: \n" + encodedIsdHex);
			System.out.println("\nOBU - Sent ISD to ODE - Ip: " + odeIp + " Port: " + odePort);
		} catch (IOException e) {
			System.out.println("OBU - Error Sending ISD to ODE");
			e.printStackTrace();
		}

		if (socket != null) {
			socket.close();
			System.out.println("OBU - Closed socket with port " + selfPort);
		}
	}
}
