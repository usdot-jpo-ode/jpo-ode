package us.dot.its.jpo.ode.coder.stream;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Mocked;
import us.dot.its.jpo.ode.coder.DecoderPublisherUtils;
import us.dot.its.jpo.ode.coder.MessagePublisher;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.util.JsonUtils;

public class JsonDecoderPublisherTest {

   @Mocked
   MessagePublisher mockMessagePublisher;
   @Mocked
   OdeData mockOdeData;
   @Capturing
   Scanner capturingScanner;
   @Capturing
   JsonUtils capturingJsonUtils;
   @Capturing
   DecoderPublisherUtils capturingDecoderPublisherUtils;

   @Test(timeout = 4000)
   public void shouldNotPublishEmptyFileAndThrowException() {

      new Expectations() {
         {
            capturingScanner.hasNextLine();
            result = false;

            mockMessagePublisher.publish((OdeData) any);
            times = 0;
         }
      };

      try {

         new JsonDecoderPublisher(mockMessagePublisher).decodeAndPublish(new ByteArrayInputStream(new byte[] { 1 }),
               "testFileName");
      } catch (Exception e) {
         fail("Unexpected exception: " + e);
      }
   }

   @Test(timeout = 4000)
   public void shouldPublishMessage() {

      new Expectations() {
         {
            capturingScanner.hasNextLine();
            returns(true, false);

            mockMessagePublisher.publish((OdeData) any);
            times = 1;
         }
      };

      try {

         new JsonDecoderPublisher(mockMessagePublisher).decodeAndPublish(new ByteArrayInputStream(new byte[] { 1 }),
               "testFileName");
      } catch (Exception e) {
         fail("Unexpected exception: " + e);
      }
   }

}
