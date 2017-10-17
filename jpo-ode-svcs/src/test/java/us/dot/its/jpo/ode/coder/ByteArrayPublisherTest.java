package us.dot.its.jpo.ode.coder;

import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class ByteArrayPublisherTest {

   @Tested
   ByteArrayPublisher testMessagePublisher;
   @Injectable
   OdeProperties testOdeProperties;
   @Injectable
   String testSerializer;
   @Mocked
   byte[] mockOdeBsmData;

   @Capturing
   MessageProducer<String, byte[]> capturingMessageProducer;

   @Test
   public void shouldPublishTwice() {

      new Expectations() {
         {
            capturingMessageProducer.send(anyString, null, (byte[]) any);
            times = 1;
         }
      };

      testMessagePublisher.publish(mockOdeBsmData, "topic");
   }
}
