package us.dot.its.jpo.ode.services.json;

import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;
import us.dot.its.jpo.ode.wrapper.MessageProcessor;

public class ToJsonServiceControllerTest {

   @Injectable
   OdeProperties injectableOdeProperties;

   @Capturing
   ToJsonConverter<?> capturingToJsonConverter;
   @Capturing
   MessageConsumer<?, ?> capturingMessageConsumer;

   @Test
   public void test() {
      new Expectations() {
         {
            new ToJsonConverter<>((OdeProperties) any, anyBoolean, anyString);
            times = 3;

            new MessageConsumer<>(anyString, anyString, (MessageProcessor<?, ?>) any, anyString);
            times = 3;

            capturingMessageConsumer.setName(anyString);
            times = 3;

            capturingToJsonConverter.start((MessageConsumer) any, anyString);
            times = 3;
         }
      };
      new ToJsonServiceController(injectableOdeProperties);
   }

}
