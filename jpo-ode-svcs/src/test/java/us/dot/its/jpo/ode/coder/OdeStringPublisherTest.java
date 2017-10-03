package us.dot.its.jpo.ode.coder;

import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class OdeStringPublisherTest {

   @Tested
   OdeStringPublisher testOdeStringPublisher;

   @Injectable
   OdeProperties injectableOdeProperties;

   @Capturing
   MessageProducer<String, String> capturingMessageProducer;

   @Test
   public void publishShouldCallMessageProducer() {
      new Expectations() {
         {
            capturingMessageProducer.send(anyString, null, anyString);
            times = 1;
         }
      };

      testOdeStringPublisher.publish(new OdeData(), "testTopic");
   }
}
