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
import us.dot.its.jpo.ode.coder.OdeBsmDataCreatorHelper;
import us.dot.its.jpo.ode.coder.OdeStringPublisher;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher.ImporterFileType;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.util.JsonUtils;

public class JsonDecoderPublisherTest {

   @Mocked
   OdeStringPublisher mockOdeStringPublisher;
   @Mocked
   OdeData mockOdeData;
   @Mocked
   OdeBsmData mockOdeBsmData;
   @Capturing
   Scanner capturingScanner;
   @Capturing
   JsonUtils capturingJsonUtils;
   @Capturing
   BsmDecoderHelper capturingBsmDecoderHelper;
   @Capturing
   OdeBsmDataCreatorHelper capturingOdeBsmDataCreaterHelper;
   

   @Test(timeout = 30000)
   public void shouldNotPublishEmptyFileAndThrowException() {

      new Expectations() {
         {
            capturingScanner.hasNextLine();
            result = false;

            mockOdeStringPublisher.publish((OdeData) any, anyString);
            times = 0;
         }
      };

      try {

          BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(new byte[] { 1 }));
          new JsonDecoderPublisher(mockOdeStringPublisher).decodeAndPublish(bis, "testFileName", ImporterFileType.OBU_LOG_FILE);
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

            OdeBsmDataCreatorHelper.createOdeBsmData((J2735Bsm) any, anyString);
            result = mockOdeBsmData;
            
            mockOdeStringPublisher.publish((OdeData) any, anyString);
            times = 1;
         }
      };

      try {

          BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(new byte[] { 1 }));
          new JsonDecoderPublisher(mockOdeStringPublisher).decodeAndPublish(bis, "testFileName", ImporterFileType.OBU_LOG_FILE);
      } catch (Exception e) {
         fail("Unexpected exception: " + e);
      }
   }

}
