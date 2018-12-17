package us.dot.its.jpo.ode.udp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.junit.Test;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;

public class ConcurrentUdpReceiverTest {

   //TODO open-ode
//   @Tested
//   AbstractConcurrentUdpReceiver testAbstractConcurrentUdpReceiver;
//   @Injectable
//   DatagramSocket mockDatagramSocket;
//   @Injectable
//   int bufSize;
//
//   @Test
//   public void shouldCatchSocketIOException() {
//
//      try {
//         new Expectations() {
//            {
//               mockDatagramSocket.receive((DatagramPacket) any);
//            }
//         };
//      } catch (IOException e1) {
//         fail("Unexpected exception in expectations block.");
//      }
//
//      try {
//         testAbstractConcurrentUdpReceiver.receiveDatagram();
//         fail("Expected exception to be thrown.");
//      } catch (Exception e) {
//         assertEquals("Empty datagram packet.", e.getCause().getMessage());
//      }
//   }
}
