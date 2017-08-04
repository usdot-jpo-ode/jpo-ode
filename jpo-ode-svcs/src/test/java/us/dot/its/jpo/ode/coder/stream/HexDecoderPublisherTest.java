package us.dot.its.jpo.ode.coder.stream;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Mocked;
import us.dot.its.jpo.ode.coder.DecoderHelper;
import us.dot.its.jpo.ode.coder.MessagePublisher;
import us.dot.its.jpo.ode.model.OdeData;

public class HexDecoderPublisherTest {

   @Mocked
   MessagePublisher mockMessagePublisher;
   @Mocked
   DecoderHelper mockDecoderHelper;
   @Mocked
   OdeData mockOdeData;
   @Capturing
   Scanner capturingScanner;

   @Test (timeout = 4000)
   public void shouldNotDecodeEmptyFileAndThrowException() {

      try {
         new Expectations() {
            {
               capturingScanner.hasNextLine();
               result = false;
            }
         };
         new HexDecoderPublisher(mockMessagePublisher, mockDecoderHelper)
               .decodeAndPublish(new ByteArrayInputStream(new byte[] { 1 }), "testFileName");
      } catch (Exception e) {
         fail("Unexpected exception: " + e);
      }
   }
   
   @Test
   public void shouldNotPublishNullDecode() {
      try {
         new Expectations() {
            {
               capturingScanner.hasNextLine();
               returns(true, false);
            }
         };
         new HexDecoderPublisher(mockMessagePublisher, mockDecoderHelper)
               .decodeAndPublish(new ByteArrayInputStream(new byte[] { 1 }), "testFileName");
      } catch (Exception e) {
         fail("Unexpected exception: " + e);
      }
   }

}
