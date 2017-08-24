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
import us.dot.its.jpo.ode.coder.MessagePublisher;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.SerialId;

public class HexDecoderPublisherTest {

   @Mocked
   MessagePublisher mockMessagePublisher;
   @Capturing
   BsmDecoderHelper capturingDecoderHelper;
   @Capturing
   HexUtils capturingHexUtils;
   @Mocked
   OdeData mockOdeData;
   @Capturing
   Scanner capturingScanner;

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
         new HexDecoderPublisher(mockMessagePublisher).decodeAndPublish(bis, "testFileName");
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

//<<<<<<< HEAD
//               capturingDecoderHelper.decode(new BufferedInputStream(new ByteArrayInputStream((byte[]) any)), anyString, (SerialId) any);
//=======
//               capturingScanner.nextLine();
//               result = "fakeLine";
//
//               BsmDecoderHelper.decode((BufferedInputStream) any, anyString, (SerialId) any);
//>>>>>>> b20b72fa66df16859ba87c1042e311cc3c59db5f
               
   //I added    ---->        
               capturingScanner.nextLine();
               result = "fakeLine";
               capturingDecoderHelper.decode(new BufferedInputStream(new ByteArrayInputStream((byte[]) any)), anyString, (SerialId) any);
   //          <-----
               result = null;
               times = 1;

               mockMessagePublisher.publish((OdeData) any);
               times = 0;
            }
         };
         BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(new byte[] { 1 }));
         new HexDecoderPublisher(mockMessagePublisher).decodeAndPublish(bis, "testFileName");
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

//<<<<<<< HEAD
               capturingDecoderHelper.decode(new BufferedInputStream(new ByteArrayInputStream((byte[]) any)), anyString, (SerialId) any);
//=======
//               BsmDecoderHelper.decode((BufferedInputStream) any, anyString, (SerialId) any);
//>>>>>>> b20b72fa66df16859ba87c1042e311cc3c59db5f
               result = new Exception("testException123");
               times = 1;

               mockMessagePublisher.publish((OdeData) any);
               times = 0;
            }
         };
         BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(new byte[] { 1 }));
         new HexDecoderPublisher(mockMessagePublisher).decodeAndPublish(bis, "testFileName");
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

//<<<<<<< HEAD
               capturingDecoderHelper.decode(new BufferedInputStream(new ByteArrayInputStream((byte[]) any)), anyString, (SerialId) any);
//=======
//               BsmDecoderHelper.decode((BufferedInputStream) any, anyString, (SerialId) any);
//>>>>>>> b20b72fa66df16859ba87c1042e311cc3c59db5f
               result = mockOdeData;
               times = 1;

               mockMessagePublisher.publish((OdeData) any);
               times = 1;
            }
         };
         BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(new byte[] { 1 }));
         new HexDecoderPublisher(mockMessagePublisher).decodeAndPublish(bis, "testFileName");
      } catch (Exception e) {
         fail("Unexpected exception: " + e);
      }
   }

}
