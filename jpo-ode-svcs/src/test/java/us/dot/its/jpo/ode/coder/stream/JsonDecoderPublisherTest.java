package us.dot.its.jpo.ode.coder.stream;

import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.Scanner;

import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Mocked;
import us.dot.its.jpo.ode.coder.BsmDecoderHelper;
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
   BsmDecoderHelper capturingBsmDecoderHelper;

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

          BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(new byte[] { 1 }));
          new JsonDecoderPublisher(mockMessagePublisher).decodeAndPublish(bis, "testFileName");
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

          BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(new byte[] { 1 }));
          new JsonDecoderPublisher(mockMessagePublisher).decodeAndPublish(bis, "testFileName");
      } catch (Exception e) {
         fail("Unexpected exception: " + e);
      }
   }

}
