package us.dot.its.jpo.ode.udp.vsd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;

import org.junit.Test;

import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;
import com.oss.asn1.PERUnalignedCoder;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.dsrc.TemporaryID;
import us.dot.its.jpo.ode.j2735.semi.SemiDialogID;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;

public class VsdDepositorTest {

   @Tested
   VsdDepositor testVsdDepositor;

   @Injectable
   OdeProperties injectableOdeProperties;

   @Mocked
   OdeProperties mockOdeProperties;

   @Test
   public void getDialogIdShouldReturnVSDDialog() {
      assertEquals(SemiDialogID.vehSitData, testVsdDepositor.getDialogId());
   }

   @Test
   public void testGetRequestID(@Capturing J2735 capturingJ2735, @Mocked PERUnalignedCoder mockPERUnalignedCoder,
         @Capturing MessageConsumer<?, ?> capturingMessageConsumer, @Mocked VehSitDataMessage mockVehSitDataMessage) {
      try {
         new Expectations() {
            {
               mockOdeProperties.getDepositSanitizedBsmToSdc();
               result = true;

               J2735.getPERUnalignedCoder();
               result = mockPERUnalignedCoder;

               mockPERUnalignedCoder.decode((ByteArrayInputStream) any, (VehSitDataMessage) any);
               result = mockVehSitDataMessage;

               mockVehSitDataMessage.getRequestID();
               result = new TemporaryID();
            }
         };
      } catch (DecodeFailedException | DecodeNotSupportedException e) {
         fail("Unexpected exception in expectations block: " + e);
      }

      VsdDepositor testRequestVsdDepositor = new VsdDepositor(mockOdeProperties);
      testRequestVsdDepositor.getRequestId(new byte[] { 1, 2, 3 });
   }

   @Test
   public void testGetRequestIDThrowsException(@Capturing J2735 capturingJ2735,
         @Mocked PERUnalignedCoder mockPERUnalignedCoder, @Capturing MessageConsumer<?, ?> capturingMessageConsumer,
         @Mocked VehSitDataMessage mockVehSitDataMessage, @Mocked DecodeFailedException mockDecodeFailedException) {
      try {
         new Expectations() {
            {
               mockOdeProperties.getDepositSanitizedBsmToSdc();
               result = true;

               J2735.getPERUnalignedCoder();
               result = mockPERUnalignedCoder;

               mockPERUnalignedCoder.decode((ByteArrayInputStream) any, (VehSitDataMessage) any);
               result = mockDecodeFailedException;
            }
         };
      } catch (DecodeFailedException | DecodeNotSupportedException e) {
         fail("Unexpected exception in expectations block: " + e);
      }

      VsdDepositor testRequestVsdDepositor = new VsdDepositor(mockOdeProperties);
      testRequestVsdDepositor.getRequestId(new byte[] { 1, 2, 3 });
   }

}
