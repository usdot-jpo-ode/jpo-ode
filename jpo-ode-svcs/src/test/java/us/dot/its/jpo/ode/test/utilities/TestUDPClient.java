package us.dot.its.jpo.ode.test.utilities;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.HexUtils;

/**
 * TestUDPClient is a utility class used for sending UDP packets to a specified port. It
 * encapsulates the creation of a DatagramSocket and the logic to send data packets to a local
 * server.
 */
@Slf4j
public class TestUDPClient {

  private final DatagramSocket socket;
  private final InetAddress address;
  private final int sendPort;

  /**
   * Constructs a new TestUDPClient instance configured to send UDP packets to the specified port on
   * the localhost.
   *
   * @param port the port number on the localhost to which UDP packets will be sent
   * @throws SocketException      if there is an error creating the DatagramSocket
   * @throws UnknownHostException if the local host cannot be resolved
   */
  public TestUDPClient(int port) throws SocketException, UnknownHostException {
    socket = new DatagramSocket();
    address = InetAddress.getByName("localhost");
    sendPort = port;
  }

  /**
   * Sends a UDP packet containing the specified message to the configured port on the localhost.
   * The message is converted from a hexadecimal string to a byte array before being sent.
   *
   * @param msg the message to be sent, represented as a hexadecimal string
   * @throws IOException if an I/O error occurs during the sending process
   */
  public void send(String msg) throws IOException {
    byte[] buf = HexUtils.fromHexString(msg);

    DatagramPacket packet = new DatagramPacket(buf, buf.length, address, sendPort);
    socket.send(packet);
  }
}
