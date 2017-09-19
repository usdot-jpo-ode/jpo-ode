package us.dot.its.jpo.ode.coder;

import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class MessagePublisherTest {

   @Tested
   MessagePublisher testMessagePublisher;
   @Injectable
   OdeProperties testOdeProperties;
   @Mocked
   OdeBsmData mockOdeBsmData;

   @Capturing
   MessageProducer<String, OdeData> capturingMessageProducer;

   @Test
   public void shouldPublishTwice() {

      new Expectations() {
         {
            capturingMessageProducer.send(anyString, null, (OdeData) any);
            times = 2;
         }
      };

      testMessagePublisher.publish(mockOdeBsmData);
   }
}
