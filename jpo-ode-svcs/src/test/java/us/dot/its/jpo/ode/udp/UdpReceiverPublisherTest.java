package us.dot.its.jpo.ode.udp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.slf4j.LoggerFactory;

import com.oss.asn1.AbstractData;
import com.oss.asn1.Coder;
import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;
import com.oss.asn1.ValidateFailedException;
import com.oss.asn1.ValidateNotSupportedException;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.asn1.j2735.J2735Util;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher.UdpReceiverException;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

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

   @Test
   public void testDecodeData(@Mocked final MessageProducer<String, byte[]> mockMessageProducer,
         @Mocked final J2735Util mockJ2735Util, @Mocked AbstractData expectedReturn) {

      try {
         new Expectations() {
            {
               J2735Util.decode((Coder) any, (byte[]) any);
               result = expectedReturn;
            }
         };
      } catch (DecodeFailedException | DecodeNotSupportedException e) {
         fail("Unexpected exception in expectations block " + e);
      }

      try {
         assertEquals(expectedReturn, testAbstractUdpReceiverPublisher.decodeData(new byte[0]));
      } catch (UdpReceiverException e) {
         fail("Unexpected exception " + e);
      }
   }

   @Test
   public void testDecodedDataError(@Mocked final MessageProducer<String, byte[]> mockMessageProducer,
         @Mocked final J2735Util mockJ2735Util) throws UdpReceiverException, ValidateFailedException, ValidateNotSupportedException {

      AbstractData decodedData = testAbstractUdpReceiverPublisher.decodeData(new byte[0]);
      assertFalse(decodedData.isValid());
   }

}
