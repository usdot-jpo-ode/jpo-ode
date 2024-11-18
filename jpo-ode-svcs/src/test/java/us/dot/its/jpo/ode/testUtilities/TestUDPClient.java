package us.dot.its.jpo.ode.testUtilities;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

@Slf4j
public class TestUDPClient {
    private DatagramSocket socket;
    private InetAddress address;
    private int sendPort;

    private byte[] buf;

    public TestUDPClient(int port) throws SocketException, UnknownHostException {
            socket = new DatagramSocket();
            address = InetAddress.getByName("localhost");
            sendPort = port;
    }

    public void send(String msg) throws IOException {
        buf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, sendPort);
        socket.send(packet);
    }

    public void close() {

        socket.close();
    }
}
