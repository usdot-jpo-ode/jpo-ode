package us.dot.its.jpo.ode.coder.stream;

import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.Scanner;

import org.junit.Test;

import gov.usdot.cv.security.msg.IEEE1609p2Message;
import mockit.Capturing;
import mockit.Expectations;
import mockit.Mocked;
import us.dot.its.jpo.ode.coder.BsmDecoderHelper;
import us.dot.its.jpo.ode.coder.MessagePublisher;
import us.dot.its.jpo.ode.coder.OdeBsmDataCreaterHelper;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.SerialId;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.util.JsonUtils;

public class JsonDecoderPublisherTest {

   @Mocked
   MessagePublisher mockMessagePublisher;
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
   OdeBsmDataCreaterHelper capturingOdeBsmDataCreaterHelper;
   

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
          new JsonDecoderPublisher(mockMessagePublisher).decodeAndPublish(bis, "testFileName", true);
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

            capturingOdeBsmDataCreaterHelper.createOdeBsmData((J2735Bsm) any, (IEEE1609p2Message) any, anyString, (SerialId) any);
            result = mockOdeBsmData;
            
            
            mockMessagePublisher.publish((OdeData) any);
            times = 1;
         }
      };

      try {

          BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(new byte[] { 1 }));
          new JsonDecoderPublisher(mockMessagePublisher).decodeAndPublish(bis, "testFileName", true);
      } catch (Exception e) {
         fail("Unexpected exception: " + e);
      }
   }

}
