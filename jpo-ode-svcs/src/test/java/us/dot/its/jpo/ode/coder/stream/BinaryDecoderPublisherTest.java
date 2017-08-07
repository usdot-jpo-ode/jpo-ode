package us.dot.its.jpo.ode.coder.stream;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Mocked;
import us.dot.its.jpo.ode.coder.BsmDecoderHelper;
import us.dot.its.jpo.ode.coder.MessagePublisher;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.SerialId;

public class BinaryDecoderPublisherTest {

   @Mocked
   MessagePublisher mockMessagePublisher;
   @Capturing
   BsmDecoderHelper capturingDecoderHelper;
   @Mocked
   OdeData mockOdeData;

   @Test(timeout = 4000)
   public void decodeAndPublishShouldNotPublishNull() {
      try {
         new Expectations() {
            {
               BsmDecoderHelper.decode((InputStream) any, anyString, (SerialId) any);
               result = null;
               times = 1;

               mockMessagePublisher.publish((OdeData) any);
               times = 0;
            }
         };

         new BinaryDecoderPublisher(mockMessagePublisher).decodeAndPublish(new ByteArrayInputStream(new byte[] { 1 }),
               "testFileName");

      } catch (Exception e) {
         fail("Unexpected exception: " + e);
      }
   }

   @Test(timeout = 4000)
   public void decodeAndPublishShouldNotPublishException() {
      try {
         new Expectations() {
            {
               BsmDecoderHelper.decode((InputStream) any, anyString, (SerialId) any);
               result = new Exception("testException123");

               mockMessagePublisher.publish((OdeData) any);
               times = 0;
            }
         };

         new BinaryDecoderPublisher(mockMessagePublisher).decodeAndPublish(new ByteArrayInputStream(new byte[] { 1 }),
               "testFileName");

      } catch (Exception e) {
         fail("Unexpected exception: " + e);
      }
   }

   @Test(timeout = 4000)
   public void decodeAndPublishShouldPublishOnce() {
      try {
         new Expectations() {
            {
               BsmDecoderHelper.decode((InputStream) any, anyString, (SerialId) any);
               returns(mockOdeData, null);

               mockMessagePublisher.publish((OdeData) any);
               times = 1;
            }
         };

         new BinaryDecoderPublisher(mockMessagePublisher).decodeAndPublish(new ByteArrayInputStream(new byte[] { 1 }),
               "testFileName");

      } catch (Exception e) {
         fail("Unexpected exception: " + e);
      }
   }

}
