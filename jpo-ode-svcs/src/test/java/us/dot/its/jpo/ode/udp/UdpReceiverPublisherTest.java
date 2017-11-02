package us.dot.its.jpo.ode.udp;

import org.slf4j.LoggerFactory;

import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;

public class UdpReceiverPublisherTest {

   @Tested
   AbstractUdpReceiverPublisher testAbstractUdpReceiverPublisher;
   @Injectable
   OdeProperties mockOdeProps;
   @Injectable
   int port;
   @Injectable
   int bufferSize;

   @Mocked
   LoggerFactory disabledLogger;

   //TODO open-ode
//   @Test
//   public void testDecodeData(@Mocked final MessageProducer<String, byte[]> mockMessageProducer,
//         @Mocked final J2735Util mockJ2735Util, @Mocked AbstractData expectedReturn) {
//
//      try {
//         new Expectations() {
//            {
//               //TODO open-ode
//               J2735Util.decode((Coder) any, (byte[]) any);
//               result = expectedReturn;
//            }
//         };
//      } catch (DecodeFailedException | DecodeNotSupportedException e) {
//         fail("Unexpected exception in expectations block " + e);
//      }
//
//      try {
//         assertEquals(expectedReturn, testAbstractUdpReceiverPublisher.decodeData(new byte[0]));
//      } catch (UdpReceiverException e) {
//         fail("Unexpected exception " + e);
//      }
//   }
//
//   @Test
//   public void testDecodedDataError(@Mocked final MessageProducer<String, byte[]> mockMessageProducer,
//         @Mocked final J2735Util mockJ2735Util, @Mocked final UdpReceiverException mockUdpReceiverException) {
//
//      try {
//         new Expectations() {
//            {
//               J2735Util.decode((Coder) any, (byte[]) any);
//               result = mockUdpReceiverException;
//            }
//         };
//      } catch (DecodeFailedException | DecodeNotSupportedException e) {
//         fail("Unexpected exception in expectations block " + e);
//      }
//
//      try {
//         testAbstractUdpReceiverPublisher.decodeData(new byte[0]);
//         fail("Expected exception.");
//      } catch (UdpReceiverException e) {
//         assertTrue(e instanceof UdpReceiverException);
//      }
//   }

}
