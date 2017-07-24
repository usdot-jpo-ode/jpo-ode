package us.dot.its.jpo.ode.udp.isd;

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
import us.dot.its.jpo.ode.j2735.semi.IntersectionSituationData;
import us.dot.its.jpo.ode.j2735.semi.SemiDialogID;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;

public class IsdDepositorTest {

   @Tested
   IsdDepositor testIsdDepositor;

   @Injectable
   OdeProperties injectableOdeProperties;

   @Capturing
   MessageConsumer<?, ?> capturingMessageConsumer;

   @Test
   public void shouldReturnCorrectSemiDialogID() {
      assertEquals(SemiDialogID.intersectionSitDataDep, testIsdDepositor.getDialogId());
   }

   @Test
   public void shouldReturnRequestID(@Capturing J2735 capturingJ2735, @Mocked PERUnalignedCoder mockPERUnalignedCoder,
         @Capturing MessageConsumer<?, ?> capturingMessageConsumer,
         @Mocked IntersectionSituationData mockIntersectionSituationData) {
      try {
         new Expectations() {
            {
               J2735.getPERUnalignedCoder();
               result = mockPERUnalignedCoder;

               mockPERUnalignedCoder.decode((ByteArrayInputStream) any, (IntersectionSituationData) any);
               result = mockIntersectionSituationData;

               mockIntersectionSituationData.getRequestID();
               result = new TemporaryID();
            }
         };
      } catch (DecodeFailedException | DecodeNotSupportedException e) {
         fail("Unexpected exception in expectations block: " + e);
      }

      IsdDepositor testRequestIsdDepositor = new IsdDepositor(injectableOdeProperties);
      testRequestIsdDepositor.getRequestId(new byte[] { 1, 2, 3 });
   }

   @Test
   public void testGetRequestIDThrowsException(@Capturing J2735 capturingJ2735,
         @Mocked PERUnalignedCoder mockPERUnalignedCoder, @Capturing MessageConsumer<?, ?> capturingMessageConsumer,
         @Mocked IntersectionSituationData mockIntersectionSituationData,
         @Mocked DecodeFailedException mockDecodeFailedException) {
      try {
         new Expectations() {
            {
               J2735.getPERUnalignedCoder();
               result = mockPERUnalignedCoder;

               mockPERUnalignedCoder.decode((ByteArrayInputStream) any, (IntersectionSituationData) any);
               result = mockDecodeFailedException;
            }
         };
      } catch (DecodeFailedException | DecodeNotSupportedException e) {
         fail("Unexpected exception in expectations block: " + e);
      }

      IsdDepositor testRequestIsdDepositor = new IsdDepositor(injectableOdeProperties);
      testRequestIsdDepositor.getRequestId(new byte[] { 1, 2, 3 });
   }

}
