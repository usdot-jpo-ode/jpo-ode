package us.dot.its.jpo.ode.udp.vsd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.oss.asn1.AbstractData;
import com.oss.asn1.Coder;
import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.j2735.dsrc.TemporaryID;
import us.dot.its.jpo.ode.j2735.semi.SemiDialogID;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;
import us.dot.its.jpo.ode.wrapper.serdes.VehSitDataMessageDeserializer;

public class VsdDepositorTest {

   @Tested
   VsdDepositor testVsdDepositor;

   @Injectable
   OdeProperties injectableOdeProperties;

   @Capturing
   MessageConsumer<?,?> capturingMessageConsumer;
   @Capturing
   VehSitDataMessageDeserializer capturingVehSitDataMessageDeserializer;
   @Capturing
   Coder capturingCoder;

   @Mocked
   VehSitDataMessage mockVehSitDataMessage;

   @Test
   public void getDialogIdShouldReturnVSDDialog() {
      assertEquals(SemiDialogID.vehSitData, testVsdDepositor.getDialogId());
   }

   @Test
   public void getRequestIDUsesDeserializer() {
      TemporaryID expectedID = new TemporaryID();
      new Expectations() {
         {
            capturingVehSitDataMessageDeserializer.deserialize(null, (byte[]) any);
            result = mockVehSitDataMessage;

            mockVehSitDataMessage.getRequestID();
            result = expectedID;
         }
      };

      assertEquals(expectedID, testVsdDepositor.getRequestId(new byte[0]));
   }

   @Test
   public void testEncodeMessageException() throws EncodeFailedException, EncodeNotSupportedException {
      new Expectations() {
         {
            capturingVehSitDataMessageDeserializer.deserialize(null, (byte[]) any);

            capturingCoder.encode((AbstractData) any);
         }
      };

      assertNotNull(testVsdDepositor.encodeMessage(new byte[0]));
   }

   @Test
   public void testEncodeMessageSuccess() {
      try {
         new Expectations() {
            {
               capturingVehSitDataMessageDeserializer.deserialize(null, (byte[]) any);

               capturingCoder.encode((AbstractData) any).array();
               result = new byte[0];
            }
         };
      } catch (EncodeFailedException | EncodeNotSupportedException e) {
         fail("Unexpected exception: " + e);
      }

      assertNotNull(testVsdDepositor.encodeMessage(new byte[0]));
   }
}
