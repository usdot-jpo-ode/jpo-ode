package us.dot.its.jpo.ode.udp.isd;

import java.net.DatagramSocket;

import mockit.Injectable;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;

public class DataReceiptReceiverTest {

   //TODO open-ode
//   @Tested
//   DataReceiptReceiver testDataReceiptReceiver;
//   @Injectable
//   OdeProperties mockOdeProperties;
//   @Injectable
//   DatagramSocket mockDatagramSocket;
//
//   @Test
//   public void test(@Mocked final J2735Util mockJ2735Util, @Mocked final LoggerFactory disabledLoggerFactory) {
//      try {
//         new Expectations() {
//            {
//               J2735Util.decode((Coder) any, (byte[]) any);
//               result = new DataReceipt();
//            }
//         };
//      } catch (DecodeFailedException | DecodeNotSupportedException e1) {
//         fail("Unexpected exception in expectations block.");
//      }
//      try {
//         assertTrue(testDataReceiptReceiver.processPacket(new byte[0]) instanceof DataReceipt);
//      } catch (DecodeFailedException | DecodeNotSupportedException e) {
//         fail("Unexpected exception." + e);
//      }
//   }
//
//   @Test
//   public void shouldReturnNullNotDataReceipt(@Mocked final J2735Util mockJ2735Util,
//         @Mocked final LoggerFactory disabledLoggerFactory) {
//      try {
//         new Expectations() {
//            {
//               J2735Util.decode((Coder) any, (byte[]) any);
//               result = new ServiceResponse();
//            }
//         };
//      } catch (DecodeFailedException | DecodeNotSupportedException e1) {
//         fail("Unexpected exception in expectations block.");
//      }
//      try {
//         assertNull(testDataReceiptReceiver.processPacket(new byte[0]));
//      } catch (DecodeFailedException | DecodeNotSupportedException e) {
//         fail("Unexpected exception." + e);
//      }
//   }

}
