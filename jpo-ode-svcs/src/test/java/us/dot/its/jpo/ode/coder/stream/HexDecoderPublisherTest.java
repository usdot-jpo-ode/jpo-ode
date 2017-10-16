package us.dot.its.jpo.ode.coder.stream;

import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.Scanner;

import org.apache.tomcat.util.buf.HexUtils;
import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Mocked;
import us.dot.its.jpo.ode.coder.BsmDecoderHelper;
import us.dot.its.jpo.ode.coder.OdeDataPublisher;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher.ImporterFileType;
import us.dot.its.jpo.ode.importer.parser.BsmLogFileParser;
import us.dot.its.jpo.ode.importer.parser.FileParser.ParserStatus;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.SerialId;

public class HexDecoderPublisherTest {

   @Mocked
   OdeDataPublisher mockOdeDataPublisher;
   @Capturing
   BsmDecoderHelper capturingDecoderHelper;
   @Capturing
   HexUtils capturingHexUtils;
   @Mocked
   OdeData mockOdeData;
   @Capturing
   Scanner capturingScanner;
   @Capturing
   BsmLogFileParser capturingBsmFileParser;

   @Test(timeout = 4000)
   public void shouldNotDecodeEmptyFileAndThrowException() {

      try {
         new Expectations() {
            {
               capturingScanner.hasNextLine();
               result = false;
            }
         };
         BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(new byte[] { 1 }));
         new HexDecoderPublisher(mockOdeDataPublisher).decodeAndPublish(bis, "testFileName", ImporterFileType.BSM_LOG_FILE);
      } catch (Exception e) {
         fail("Unexpected exception: " + e);
      }
   }

   @Test(timeout = 4000)
   public void shouldNotPublishNullDecode() {
      try {
         new Expectations() {
            {
               capturingScanner.hasNextLine();
               returns(true, false);
     
               capturingScanner.nextLine();
               result = "fakeLine";
               
               capturingBsmFileParser.parseFile((BufferedInputStream) any, anyString);
               result = ParserStatus.COMPLETE;

               capturingDecoderHelper.decode((BsmLogFileParser) any, (SerialId) any);
               result = null;
               times = 1;

               mockOdeDataPublisher.publish((OdeData) any, anyString);
               times = 0;
            }
         };
         BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(new byte[] { 1 }));
         new HexDecoderPublisher(mockOdeDataPublisher).decodeAndPublish(bis, "testFileName", ImporterFileType.BSM_LOG_FILE);
      } catch (Exception e) {
         fail("Unexpected exception: " + e);
      }
   }

   @Test(timeout = 4000)
   public void shouldNotPublishExceptionOnDecode() {
      try {
         new Expectations() {
            {
               capturingScanner.hasNextLine();
               returns(true, false);
               
               capturingBsmFileParser.parseFile((BufferedInputStream) any, anyString);
               result = ParserStatus.COMPLETE;

               capturingDecoderHelper.decode((BsmLogFileParser) any, (SerialId) any);
               result = new Exception("testException123");
               times = 1;

               mockOdeDataPublisher.publish((OdeData) any, anyString);
               times = 0;
            }
         };
         BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(new byte[] { 1 }));
         new HexDecoderPublisher(mockOdeDataPublisher).decodeAndPublish(bis, "testFileName", ImporterFileType.BSM_LOG_FILE);
      } catch (Exception e) {
         fail("Unexpected exception: " + e);
      }
   }

   @Test(timeout = 4000)
   public void shouldPublishMessage() {
      try {
         new Expectations() {
            {
               capturingScanner.hasNextLine();
               returns(true, false);

               capturingBsmFileParser.parseFile((BufferedInputStream) any, anyString);
               result = ParserStatus.COMPLETE;
               
               capturingDecoderHelper.decode((BsmLogFileParser) any, (SerialId) any);
               result = mockOdeData;
               times = 1;

               mockOdeDataPublisher.publish((OdeData) any, anyString);
               times = 1;
            }
         };
         BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(new byte[] { 1 }));
         new HexDecoderPublisher(mockOdeDataPublisher).decodeAndPublish(bis, "testFileName", ImporterFileType.BSM_LOG_FILE);
      } catch (Exception e) {
         fail("Unexpected exception: " + e);
      }
   }

}
