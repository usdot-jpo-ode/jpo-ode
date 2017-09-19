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
import us.dot.its.jpo.ode.coder.BsmMessagePublisher;
import us.dot.its.jpo.ode.importer.parser.BsmFileParser;
import us.dot.its.jpo.ode.importer.parser.LogFileParser.ParserStatus;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.SerialId;

public class HexDecoderPublisherTest {

   @Mocked
   BsmMessagePublisher mockMessagePublisher;
   @Capturing
   BsmDecoderHelper capturingDecoderHelper;
   @Capturing
   HexUtils capturingHexUtils;
   @Mocked
   OdeData mockOdeData;
   @Capturing
   Scanner capturingScanner;
   @Capturing
   BsmFileParser capturingBsmFileParser;

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
         new HexDecoderPublisher(mockMessagePublisher).decodeAndPublish(bis, "testFileName", false);
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
               
               capturingBsmFileParser.parse((BufferedInputStream) any, anyString);
               result = ParserStatus.COMPLETE;

               capturingDecoderHelper.decode((BsmFileParser) any, (SerialId) any);
               result = null;
               times = 1;

               mockMessagePublisher.publish((OdeData) any);
               times = 0;
            }
         };
         BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(new byte[] { 1 }));
         new HexDecoderPublisher(mockMessagePublisher).decodeAndPublish(bis, "testFileName", true);
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
               
               capturingBsmFileParser.parse((BufferedInputStream) any, anyString);
               result = ParserStatus.COMPLETE;

               capturingDecoderHelper.decode((BsmFileParser) any, (SerialId) any);
               result = new Exception("testException123");
               times = 1;

               mockMessagePublisher.publish((OdeData) any);
               times = 0;
            }
         };
         BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(new byte[] { 1 }));
         new HexDecoderPublisher(mockMessagePublisher).decodeAndPublish(bis, "testFileName", true);
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

               capturingBsmFileParser.parse((BufferedInputStream) any, anyString);
               result = ParserStatus.COMPLETE;
               
               capturingDecoderHelper.decode((BsmFileParser) any, (SerialId) any);
               result = mockOdeData;
               times = 1;

               mockMessagePublisher.publish((OdeData) any);
               times = 1;
            }
         };
         BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(new byte[] { 1 }));
         new HexDecoderPublisher(mockMessagePublisher).decodeAndPublish(bis, "testFileName", true);
      } catch (Exception e) {
         fail("Unexpected exception: " + e);
      }
   }

}
