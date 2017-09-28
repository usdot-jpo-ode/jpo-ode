package us.dot.its.jpo.ode.coder;

import org.junit.Before;
import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.wrapper.MessageProducer;
import us.dot.its.jpo.ode.wrapper.serdes.OdeBsmSerializer;

public class BsmMessagePublisherTest {

   BsmMessagePublisher testMessagePublisher;

   @Injectable
   OdeProperties injectableOdeProperties;
   @Mocked
   OdeBsmData mockOdeBsmData;
   @Capturing
   MessageProducer<String, OdeData> capturingMessageProducer;

   @Before
   public void setup() {
      testMessagePublisher = new BsmMessagePublisher(injectableOdeProperties, "testKafkaTopic", OdeBsmSerializer.class);
   }

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
