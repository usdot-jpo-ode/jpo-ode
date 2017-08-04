package us.dot.its.jpo.ode.coder.stream;

import static org.junit.Assert.fail;

import org.junit.Test;

import mockit.Expectations;
import mockit.Mocked;
import us.dot.its.jpo.ode.coder.DecoderHelper;
import us.dot.its.jpo.ode.coder.MessagePublisher;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.SerialId;

public class ByteDecoderPublisherTest {

   @Mocked
   MessagePublisher mockMessagePublisher;
   @Mocked
   DecoderHelper mockDecoderHelper;
   @Mocked
   OdeData mockOdeData;

   @Test
   public void decodeAndPublishShouldNotPublishNull() {

      try {
         new Expectations() {
            {
               mockDecoderHelper.decode((byte[]) any, anyString, (SerialId) any);
               result = null;
               times = 1;

               mockMessagePublisher.publish((OdeData) any);
               times = 0;
            }
         };
         new ByteDecoderPublisher(mockMessagePublisher, mockDecoderHelper).decodeAndPublish(new byte[] { 1 });
      } catch (Exception e) {
         fail("Unexpected exception: " + e);
      }
   }

   @Test
   public void decodeAndPublishShouldNotPublishException() {
      try {
         new Expectations() {
            {
               mockDecoderHelper.decode((byte[]) any, anyString, (SerialId) any);
               result = new Exception("testException123");
               times = 1;

               mockMessagePublisher.publish((OdeData) any);
               times = 0;
            }
         };
         new ByteDecoderPublisher(mockMessagePublisher, mockDecoderHelper).decodeAndPublish(new byte[] { 1 });
      } catch (Exception e) {
         fail("Unexpected exception: " + e);
      }
   }

   @Test
   public void decodeAndPublishShouldPublishData() {
      try {
         new Expectations() {
            {
               mockDecoderHelper.decode((byte[]) any, anyString, (SerialId) any);
               result = mockOdeData;
               times = 1;

               mockMessagePublisher.publish((OdeData) any);
               times = 1;
            }
         };
         new ByteDecoderPublisher(mockMessagePublisher, mockDecoderHelper).decodeAndPublish(new byte[] { 1 });
      } catch (Exception e) {
         fail("Unexpected exception: " + e);
      }
   }

}