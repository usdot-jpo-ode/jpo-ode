package us.dot.its.jpo.ode.coder;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.coder.stream.JsonDecoderPublisher;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher.ImporterFileType;

public class FileDecoderPublisherTest {

   @Injectable
   OdeProperties injectableOdeProperties;
   @Tested
   FileDecoderPublisher testedFileDecoderPublisher;
   @Capturing
   JsonDecoderPublisher capturingJsonDecoderPublisher;
   @Capturing
   OdeStringPublisher capturingOdeStringPublisher;

   @Test
   public void testNoException() {

         new Expectations() {
            {
               capturingJsonDecoderPublisher.decodeAndPublish((BufferedInputStream) any, anyString, ImporterFileType.OBU_LOG_FILE);
               times = 1;
            }
         };

         Path testPath = Paths.get("testFile.hex");
         BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(new byte[] { 1 }));
         testedFileDecoderPublisher.decodeAndPublishFile(testPath, bis, ImporterFileType.OBU_LOG_FILE);

   }
   
   @Test
   public void testException() {

         new Expectations() {
            {
               capturingJsonDecoderPublisher.decodeAndPublish((BufferedInputStream) any, anyString, ImporterFileType.OBU_LOG_FILE);
               result = new IOException("testException123");
               times = 1;
            }
         };

         Path testPath = Paths.get("testFile.hex");
         BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(new byte[] { 1 }));
         testedFileDecoderPublisher.decodeAndPublishFile(testPath, bis, ImporterFileType.OBU_LOG_FILE);

   }
}