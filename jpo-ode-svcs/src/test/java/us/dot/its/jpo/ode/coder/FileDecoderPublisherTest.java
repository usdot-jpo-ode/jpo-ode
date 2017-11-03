package us.dot.its.jpo.ode.coder;

import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Ignore;
import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.coder.stream.BinaryDecoderPublisher;
import us.dot.its.jpo.ode.coder.stream.HexDecoderPublisher;
import us.dot.its.jpo.ode.coder.stream.JsonDecoderPublisher;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher.ImporterFileType;

@Ignore
public class FileDecoderPublisherTest {

   @Injectable
   OdeProperties injectableOdeProperties;
   @Tested
   FileDecoderPublisher testedFileDecoderPublisher;
   @Capturing
   JsonDecoderPublisher capturingJsonDecoderPublisher;
   @Capturing
   HexDecoderPublisher capturingHexDecoderPublisher;
   @Capturing
   BinaryDecoderPublisher capturingBinaryDecoderPublisher;
   @Capturing
   OdeDataPublisher capturedMessagePublisher;


   @Test
   public void hexDecoderShouldDecodeHex() {
      try {
         new Expectations() {
            {
               capturingHexDecoderPublisher.decodeAndPublish((BufferedInputStream) any, anyString, ImporterFileType.OBU_LOG_FILE);
               times = 1;
               capturingJsonDecoderPublisher.decodeAndPublish((BufferedInputStream) any, anyString, ImporterFileType.OBU_LOG_FILE);
               times = 0;
               capturingBinaryDecoderPublisher.decodeAndPublish((BufferedInputStream) any, anyString, ImporterFileType.OBU_LOG_FILE);
               times = 0;
            }
         };

         Path testPath = Paths.get("testFile.hex");
         BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(new byte[] { 1 }));
         testedFileDecoderPublisher.decodeAndPublishFile(testPath, bis, ImporterFileType.OBU_LOG_FILE);
      } catch (Exception e) {
         fail("Unexpected exception: " + e);
      }
   }
   
   @Test
   public void hexDecoderShouldDecodeText() {
      try {
         new Expectations() {
            {
               capturingHexDecoderPublisher.decodeAndPublish((BufferedInputStream) any, anyString, ImporterFileType.OBU_LOG_FILE);
               times = 1;
               capturingJsonDecoderPublisher.decodeAndPublish((BufferedInputStream) any, anyString, ImporterFileType.OBU_LOG_FILE);
               times = 0;
               capturingBinaryDecoderPublisher.decodeAndPublish((BufferedInputStream) any, anyString, ImporterFileType.OBU_LOG_FILE);
               times = 0;
            }
         };

         Path testPath = Paths.get("testFile.txt");
         BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(new byte[] { 1 }));
         testedFileDecoderPublisher.decodeAndPublishFile(testPath, bis, ImporterFileType.OBU_LOG_FILE);
      } catch (Exception e) {
         fail("Unexpected exception: " + e);
      }
   }

   @Test
   public void jsonDecoderShouldDecodeJson() {
      try {
         new Expectations() {
            {
               capturingHexDecoderPublisher.decodeAndPublish((BufferedInputStream) any, anyString, ImporterFileType.OBU_LOG_FILE);
               times = 0;
               capturingJsonDecoderPublisher.decodeAndPublish((BufferedInputStream) any, anyString, ImporterFileType.OBU_LOG_FILE);
               times = 1;
               capturingBinaryDecoderPublisher.decodeAndPublish((BufferedInputStream) any, anyString, ImporterFileType.OBU_LOG_FILE);
               times = 0;
            }
         };

         Path testPath = Paths.get("testFile.json");
         BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(new byte[] { 1 }));
         testedFileDecoderPublisher.decodeAndPublishFile(testPath, bis, ImporterFileType.OBU_LOG_FILE);
      } catch (Exception e) {
         fail("Unexpected exception: " + e);
      }
   }

   @Test
   public void binaryDecoderShouldDecodeOther() {
      try {
         new Expectations() {
            {
               capturingHexDecoderPublisher.decodeAndPublish((BufferedInputStream) any, anyString, ImporterFileType.OBU_LOG_FILE);
               times = 0;
               capturingJsonDecoderPublisher.decodeAndPublish((BufferedInputStream) any, anyString, ImporterFileType.OBU_LOG_FILE);
               times = 0;
               capturingBinaryDecoderPublisher.decodeAndPublish((BufferedInputStream) any, anyString, ImporterFileType.OBU_LOG_FILE);
               times = 1;
            }
         };

         Path testPath = Paths.get("testFile.uper");
         BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(new byte[] { 1 }));
         testedFileDecoderPublisher.decodeAndPublishFile(testPath, bis, ImporterFileType.OBU_LOG_FILE);
      } catch (Exception e) {
         fail("Unexpected exception: " + e);
      }
   }
}