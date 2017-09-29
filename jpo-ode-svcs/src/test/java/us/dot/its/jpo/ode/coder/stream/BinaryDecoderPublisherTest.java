package us.dot.its.jpo.ode.coder.stream;

import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Mocked;
import us.dot.its.jpo.ode.coder.BsmDecoderHelper;
import us.dot.its.jpo.ode.coder.OdeDataPublisher;
import us.dot.its.jpo.ode.importer.BsmFileParser;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher.ImporterFileType;
import us.dot.its.jpo.ode.importer.LogFileParser.ParserStatus;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.SerialId;

public class BinaryDecoderPublisherTest {

   @Mocked
   OdeDataPublisher mockOdeDataPublisher;
   @Capturing
   BsmDecoderHelper capturingDecoderHelper;
   @Mocked
   OdeData mockOdeData;
   @Capturing
   BsmFileParser capturingBsmFileParser;

   @Test(timeout = 4000)
   public void decodeAndPublishShouldNotPublishNull() {
      try {
         new Expectations() {
            {
               capturingBsmFileParser.parse((BufferedInputStream) any, anyString, anyInt);
               result = ParserStatus.COMPLETE;
               
               capturingDecoderHelper.decode( (BsmFileParser) any, (SerialId) any);
               result = null;
               times = 1;

               mockOdeDataPublisher.publish((OdeData) any, anyString);
               times = 0;
            }
         };

         BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(new byte[] { 1 }));
         new BinaryDecoderPublisher(mockOdeDataPublisher).decodeAndPublish(bis, "testFileName", ImporterFileType.BSM_LOG_FILE);

      } catch (Exception e) {
         fail("Unexpected exception: " + e);
      }
   }

   @Test(timeout = 4000)
   public void decodeAndPublishShouldNotPublishException() {
      try {
         new Expectations() {
            {
               capturingBsmFileParser.parse((BufferedInputStream) any, anyString, anyInt);
               result = ParserStatus.COMPLETE;
               
               capturingDecoderHelper.decode( (BsmFileParser) any, (SerialId) any);
               result = new Exception("testException123");

               mockOdeDataPublisher.publish((OdeData) any, anyString);
               times = 0;
            }
         };

         BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(new byte[] { 1 }));
         new BinaryDecoderPublisher(mockOdeDataPublisher).decodeAndPublish(bis, "testFileName", ImporterFileType.BSM_LOG_FILE);

      } catch (Exception e) {
         fail("Unexpected exception: " + e);
      }
   }

   @Test(timeout = 4000)
   public void decodeAndPublishShouldPublishOnce() {
      try {
         new Expectations() {
            {
               capturingBsmFileParser.parse((BufferedInputStream) any, anyString, anyInt);
               result = ParserStatus.COMPLETE;
               
               capturingDecoderHelper.decode((BsmFileParser) any, (SerialId) any);
               returns(mockOdeData, null);

               mockOdeDataPublisher.publish((OdeData) any, anyString);
               times = 1;
            }
         };

         BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(new byte[] { 1 }));
         new BinaryDecoderPublisher(mockOdeDataPublisher).decodeAndPublish(bis, "testFileName", ImporterFileType.BSM_LOG_FILE);

      } catch (Exception e) {
         fail("Unexpected exception: " + e);
      }
   }

}
