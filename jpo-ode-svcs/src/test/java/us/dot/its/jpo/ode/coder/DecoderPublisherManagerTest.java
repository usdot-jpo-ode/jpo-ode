package us.dot.its.jpo.ode.coder;

import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
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
   FileDecoderPublisher testDecoderPublisherManager;

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
               capturingHexDecoderPublisher.decodeAndPublish((BufferedInputStream) any, anyString);
               times = 1;
               capturingJsonDecoderPublisher.decodeAndPublish((BufferedInputStream) any, anyString);
               times = 0;
               capturingBinaryDecoderPublisher.decodeAndPublish((BufferedInputStream) any, anyString);
               times = 0;
            }
         };

         Path testPath = Paths.get("testFile.hex");
         BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(new byte[] { 1 }));
         testDecoderPublisherManager.decodeAndPublishFile(testPath, bis );
      } catch (Exception e) {
         fail("Unexpected exception: " + e);
      }
   }
   
   @Test
   public void hexDecoderShouldDecodeText() {
      try {
         new Expectations() {
            {
               capturingHexDecoderPublisher.decodeAndPublish((BufferedInputStream) any, anyString);
               times = 1;
               capturingJsonDecoderPublisher.decodeAndPublish((BufferedInputStream) any, anyString);
               times = 0;
               capturingBinaryDecoderPublisher.decodeAndPublish((BufferedInputStream) any, anyString);
               times = 0;
            }
         };

         Path testPath = Paths.get("testFile.txt");
         BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(new byte[] { 1 }));
         testDecoderPublisherManager.decodeAndPublishFile(testPath, bis );
      } catch (Exception e) {
         fail("Unexpected exception: " + e);
      }
   }

   @Test
   public void jsonDecoderShouldDecodeJson() {
      try {
         new Expectations() {
            {
               capturingHexDecoderPublisher.decodeAndPublish((BufferedInputStream) any, anyString);
               times = 0;
               capturingJsonDecoderPublisher.decodeAndPublish((BufferedInputStream) any, anyString);
               times = 1;
               capturingBinaryDecoderPublisher.decodeAndPublish((BufferedInputStream) any, anyString);
               times = 0;
            }
         };

         Path testPath = Paths.get("testFile.json");
         BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(new byte[] { 1 }));
         testDecoderPublisherManager.decodeAndPublishFile(testPath, bis );
      } catch (Exception e) {
         fail("Unexpected exception: " + e);
      }
   }

   @Test
   public void binaryDecoderShouldDecodeOther() {
      try {
         new Expectations() {
            {
               capturingHexDecoderPublisher.decodeAndPublish((BufferedInputStream) any, anyString);
               times = 0;
               capturingJsonDecoderPublisher.decodeAndPublish((BufferedInputStream) any, anyString);
               times = 0;
               capturingBinaryDecoderPublisher.decodeAndPublish((BufferedInputStream) any, anyString);
               times = 1;
            }
         };

         Path testPath = Paths.get("testFile.uper");
         BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(new byte[] { 1 }));
         testDecoderPublisherManager.decodeAndPublishFile(testPath, bis );
      } catch (Exception e) {
         fail("Unexpected exception: " + e);
      }
   }

}
