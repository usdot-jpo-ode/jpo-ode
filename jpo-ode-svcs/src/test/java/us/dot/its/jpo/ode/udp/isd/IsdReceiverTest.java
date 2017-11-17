/**
 * 
 */
package us.dot.its.jpo.ode.udp.isd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

@RunWith(JMockit.class)
public class IsdReceiverTest {

   @Tested
   IsdReceiver testIsdReceiver;

   @Injectable
   OdeProperties mockOdeProps;

   @Test(timeout = 2000) // catch runaway while loop
   public void shouldNotRunWhenStopped(@Capturing DatagramPacket capturingDatagramPacket,
         @Capturing DatagramSocket capturingDatagramSocket, @Capturing MessageProducer<?, ?> capturingMessageProducer) {
      new Expectations() {
         {
            new DatagramPacket((byte[]) any, anyInt);
            times = 1;
         }
      };
      testIsdReceiver.setStopped(true);
      testIsdReceiver.run();
   }

   //TODO open-ode
//   @Test
//   public void testGetPacket(@Capturing DatagramPacket capturingDatagramPacket,
//         @Capturing DatagramSocket capturingDatagramSocket, @Capturing MessageProducer<?, ?> capturingMessageProducer,
//         @Mocked DatagramPacket mockDatagramPacket, @Capturing J2735Util capturingJ2735Util) {
//      try {
//         new Expectations() {
//            {
//               mockDatagramPacket.getLength();
//               returns(1,0);
//               
//               mockDatagramPacket.getAddress().getHostAddress();
//               
//               mockDatagramPacket.getPort();
//               
//               mockDatagramPacket.getData();
//               
//               J2735Util.decode((Coder) any, (byte[]) any);
//               result = null;
//            }
//         };
//      } catch (DecodeFailedException | DecodeNotSupportedException e) {
//         fail("Unexpected exception in expectations block: " + e);
//      }
//      testIsdReceiver.getPacket(mockDatagramPacket);
//   }
   
   @Test
   public void testGetPacketException(@Capturing DatagramPacket capturingDatagramPacket,
         @Capturing DatagramSocket capturingDatagramSocket, @Capturing MessageProducer<?, ?> capturingMessageProducer,
         @Mocked DatagramPacket mockDatagramPacket) {

         new Expectations() {
            {
               mockDatagramPacket.getLength();
               result = 1;
               
               mockDatagramPacket.getAddress();
               result = new IOException("testException123");
            }
         };
      testIsdReceiver.getPacket(mockDatagramPacket);
   }
   
   @Test
   public void testGetPacketZeroLength(@Capturing DatagramPacket capturingDatagramPacket,
         @Capturing DatagramSocket capturingDatagramSocket, @Capturing MessageProducer<?, ?> capturingMessageProducer,
         @Mocked DatagramPacket mockDatagramPacket) {

         new Expectations() {
            {
               mockDatagramPacket.getLength();
               result = 0;
            }
         };
      testIsdReceiver.getPacket(mockDatagramPacket);
   }
   
   //TODO open-ode
//   @Test
//   public void testProcessPacketServiceRequest(@Capturing DatagramPacket capturingDatagramPacket,
//         @Capturing DatagramSocket capturingDatagramSocket, @Capturing MessageProducer<?, ?> capturingMessageProducer,
//         @Mocked DatagramPacket mockDatagramPacket, @Capturing J2735Util capturingJ2735Util, @Mocked ServiceRequest mockServiceRequest) {
//      try {
//         new Expectations() {{
//            J2735Util.decode((Coder) any, (byte[]) any);
//            result = mockServiceRequest;
//         }};
//      } catch (DecodeFailedException | DecodeNotSupportedException e) {
//         fail("Unexpected exception in expectations block: " + e);
//      }
//      
//      try {
//         testIsdReceiver.processPacket(new byte[]{0});
//      } catch (UdpReceiverException e) {
//         fail("Unexpected exception: " + e);
//      }
//   }
//   
//   @Test
//   public void testProcessPacketISD(@Capturing DatagramPacket capturingDatagramPacket,
//         @Capturing DatagramSocket capturingDatagramSocket, @Capturing MessageProducer<?, ?> capturingMessageProducer,
//         @Mocked DatagramPacket mockDatagramPacket, @Capturing J2735Util capturingJ2735Util, @Mocked IntersectionSituationData mockIntersectionSituationData) {
//      try {
//         new Expectations() {{
//            J2735Util.decode((Coder) any, (byte[]) any);
//            result = mockIntersectionSituationData;
//         }};
//      } catch (DecodeFailedException | DecodeNotSupportedException e) {
//         fail("Unexpected exception in expectations block: " + e);
//      }
//      
//      try {
//         testIsdReceiver.processPacket(new byte[]{0});
//      } catch (UdpReceiverException e) {
//         fail("Unexpected exception: " + e);
//      }
//   }

}
