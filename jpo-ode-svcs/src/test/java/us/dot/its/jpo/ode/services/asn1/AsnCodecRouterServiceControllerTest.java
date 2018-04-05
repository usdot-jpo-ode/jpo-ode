package us.dot.its.jpo.ode.services.asn1;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;

public class AsnCodecRouterServiceControllerTest {

   @Capturing
   MessageConsumer<?, ?> capturingMessageConsumer;

   @Capturing
   Asn1DecodedDataRouter capturingAsn1DecodedDataRouter;
   
   @Capturing 
   Asn1EncodedDataRouter capturingAsn1EncodedDataRouter;

   @Injectable
   OdeProperties injectableOdeProperties;

   @Test
   public void shouldStartTwoConsumers() {

      new Expectations() {
         {
            MessageConsumer.defaultStringMessageConsumer(anyString, anyString, (Asn1DecodedDataRouter) any);
            times = 2;
         }
      };

      assertNotNull(new AsnCodecRouterServiceController(injectableOdeProperties));
   }

}
