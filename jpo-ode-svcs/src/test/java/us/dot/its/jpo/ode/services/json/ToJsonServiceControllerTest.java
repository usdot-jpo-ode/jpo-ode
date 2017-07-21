package us.dot.its.jpo.ode.services.json;

import org.junit.Test;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class ToJsonServiceControllerTest {

   @Test
   public void test(@Injectable OdeProperties mockOdeProperties, @Mocked final MessageProducer<String, String> mockMessageProducer,
         @Mocked final MessageConsumer<String, String> mockMessageConsumer) {
      new Expectations() {{
         mockMessageConsumer.setName(anyString);
      }};
      ToJsonServiceController mockToJsonServiceController = new ToJsonServiceController(mockOdeProperties);
   }

}
