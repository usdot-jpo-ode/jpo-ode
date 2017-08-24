package us.dot.its.jpo.ode.coder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import gov.usdot.cv.security.msg.IEEE1609p2Message;
import mockit.Capturing;
import mockit.Expectations;
import mockit.Mocked;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.security.SecurityManager;
import us.dot.its.jpo.ode.security.SecurityManager.SecurityManagerException;

public class BsmDecoderPayloadHelperTest {

   @Capturing
   RawBsmMfSorter capturedRawBsmMFSorter;
   @Capturing
   IEEE1609p2Message capturedIEEE1609p2Message;
   @Capturing
   SecurityManager capturedSecurityManager;
   @Mocked
   OdeObject mockOdeObject;
   @Mocked
   RawBsmMfSorter mockRawBsmMFSorter;
   
//   @Tested
//   BsmDecoderPayloadHelper testBsmDecoderPayloadHelper;
//   @Injectable
//   RawBsmMFSorter injectedRawBsmMFSorter;


   @Test
   public void getBsmPayloadValidTime() {

      new Expectations() {
         {
            mockRawBsmMFSorter.decodeBsm((byte[]) any);
            result = mockOdeObject;
         }
      };

      assertEquals(mockOdeObject,
            new BsmDecoderPayloadHelper(mockRawBsmMFSorter).getBsmPayload(capturedIEEE1609p2Message));

   }

   @Test
   public void getBsmPayloadSecurityManagerException() {

      try {
         new Expectations() {
            {
               SecurityManager.validateGenerationTime((IEEE1609p2Message) any);
               result = new SecurityManagerException("testException123");
            }
         };

         assertEquals(mockOdeObject,
               new BsmDecoderPayloadHelper(mockRawBsmMFSorter).getBsmPayload(capturedIEEE1609p2Message));

      } catch (SecurityManagerException e) {
         fail("SecurityManagerException" + e);
      }
   }

}
