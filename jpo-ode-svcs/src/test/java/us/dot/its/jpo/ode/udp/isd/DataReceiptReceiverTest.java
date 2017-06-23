package us.dot.its.jpo.ode.udp.isd;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.DatagramSocket;

import org.junit.Test;
import org.slf4j.LoggerFactory;

import com.oss.asn1.Coder;
import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.asn1.j2735.J2735Util;
import us.dot.its.jpo.ode.j2735.semi.DataReceipt;
import us.dot.its.jpo.ode.j2735.semi.ServiceResponse;

public class DataReceiptReceiverTest {

   @Tested
   DataReceiptReceiver testDataReceiptReceiver;
   @Injectable
   OdeProperties mockOdeProperties;
   @Injectable
   DatagramSocket mockDatagramSocket;

   @Test
   public void test(@Mocked final J2735Util mockJ2735Util, @Mocked final LoggerFactory disabledLoggerFactory) {
      try {
         new Expectations() {
            {
               J2735Util.decode((Coder) any, (byte[]) any);
               result = new DataReceipt();
            }
         };
      } catch (DecodeFailedException | DecodeNotSupportedException e1) {
         fail("Unexpected exception in expectations block.");
      }
      try {
         assertTrue(testDataReceiptReceiver.processPacket(new byte[0]) instanceof DataReceipt);
      } catch (DecodeFailedException | DecodeNotSupportedException e) {
         fail("Unexpected exception." + e);
      }
   }

   @Test
   public void shouldReturnNullNotDataReceipt(@Mocked final J2735Util mockJ2735Util,
         @Mocked final LoggerFactory disabledLoggerFactory) {
      try {
         new Expectations() {
            {
               J2735Util.decode((Coder) any, (byte[]) any);
               result = new ServiceResponse();
            }
         };
      } catch (DecodeFailedException | DecodeNotSupportedException e1) {
         fail("Unexpected exception in expectations block.");
      }
      try {
         assertNull(testDataReceiptReceiver.processPacket(new byte[0]));
      } catch (DecodeFailedException | DecodeNotSupportedException e) {
         fail("Unexpected exception." + e);
      }
   }

}
