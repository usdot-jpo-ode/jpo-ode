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

public class OdeDataPublisherTest {

   @Tested
   OdeDataPublisher testMessagePublisher;
   @Injectable
   OdeProperties testOdeProperties;
   @Injectable
   String testSerializer;
   @Mocked
   OdeBsmData mockOdeBsmData;

   @Capturing
   MessageProducer<String, OdeData> capturingMessageProducer;

   @Test
   public void shouldPublishTwice() {

      new Expectations() {
         {
            capturingMessageProducer.send(anyString, null, (OdeData) any);
            times = 1;
         }
      };

      testMessagePublisher.publish(mockOdeBsmData, "topic");
   }
}
