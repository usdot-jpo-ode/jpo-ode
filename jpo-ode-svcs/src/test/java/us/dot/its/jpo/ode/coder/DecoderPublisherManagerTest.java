package us.dot.its.jpo.ode.coder;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.coder.stream.BinaryDecoderPublisher;
import us.dot.its.jpo.ode.coder.stream.HexDecoderPublisher;
import us.dot.its.jpo.ode.coder.stream.JsonDecoderPublisher;

public class DecoderPublisherManagerTest {

   @Tested
   DecoderPublisherManager testDecoderPublisherManager;

   @Injectable
   OdeProperties injectableOdeProperties;

   @Capturing
   MessagePublisher capturingMessagePublisher;
   @Capturing
   JsonDecoderPublisher capturingJsonDecoderPublisher;
   @Capturing
   HexDecoderPublisher capturingHexDecoderPublisher;
   @Capturing
   BinaryDecoderPublisher capturingBinaryDecoderPublisher;

   @Test
   public void hexDecoderShouldDecodeHex() {
      try {
         new Expectations() {
            {
               capturingHexDecoderPublisher.decodeAndPublish((InputStream) any, anyString);
               times = 1;
               capturingJsonDecoderPublisher.decodeAndPublish((InputStream) any, anyString);
               times = 0;
               capturingBinaryDecoderPublisher.decodeAndPublish((InputStream) any, anyString);
               times = 0;
            }
         };

         Path testPath = Paths.get("testFile.hex");
         testDecoderPublisherManager.decodeAndPublishFile(testPath, new ByteArrayInputStream(new byte[] { 1 }));
      } catch (Exception e) {
         fail("Unexpected exception: " + e);
      }
   }
   
   @Test
   public void hexDecoderShouldDecodeText() {
      try {
         new Expectations() {
            {
               capturingHexDecoderPublisher.decodeAndPublish((InputStream) any, anyString);
               times = 1;
               capturingJsonDecoderPublisher.decodeAndPublish((InputStream) any, anyString);
               times = 0;
               capturingBinaryDecoderPublisher.decodeAndPublish((InputStream) any, anyString);
               times = 0;
            }
         };

         Path testPath = Paths.get("testFile.txt");
         testDecoderPublisherManager.decodeAndPublishFile(testPath, new ByteArrayInputStream(new byte[] { 1 }));
      } catch (Exception e) {
         fail("Unexpected exception: " + e);
      }
   }

   @Test
   public void jsonDecoderShouldDecodeJson() {
      try {
         new Expectations() {
            {
               capturingHexDecoderPublisher.decodeAndPublish((InputStream) any, anyString);
               times = 0;
               capturingJsonDecoderPublisher.decodeAndPublish((InputStream) any, anyString);
               times = 1;
               capturingBinaryDecoderPublisher.decodeAndPublish((InputStream) any, anyString);
               times = 0;
            }
         };

         Path testPath = Paths.get("testFile.json");
         testDecoderPublisherManager.decodeAndPublishFile(testPath, new ByteArrayInputStream(new byte[] { 1 }));
      } catch (Exception e) {
         fail("Unexpected exception: " + e);
      }
   }

   @Test
   public void binaryDecoderShouldDecodeOther() {
      try {
         new Expectations() {
            {
               capturingHexDecoderPublisher.decodeAndPublish((InputStream) any, anyString);
               times = 0;
               capturingJsonDecoderPublisher.decodeAndPublish((InputStream) any, anyString);
               times = 0;
               capturingBinaryDecoderPublisher.decodeAndPublish((InputStream) any, anyString);
               times = 1;
            }
         };

         Path testPath = Paths.get("testFile.uper");
         testDecoderPublisherManager.decodeAndPublishFile(testPath, new ByteArrayInputStream(new byte[] { 1 }));
      } catch (Exception e) {
         fail("Unexpected exception: " + e);
      }
   }

}
