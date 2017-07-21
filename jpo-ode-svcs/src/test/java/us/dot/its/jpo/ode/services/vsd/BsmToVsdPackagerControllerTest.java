package us.dot.its.jpo.ode.services.vsd;

import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Mocked;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class BsmToVsdPackagerControllerTest {

   @Mocked
   OdeProperties mockOdeProperties;

   @Capturing
   BsmToVsdPackager capturingBsmToVsdPackager;

   @Test
   public void shouldNotCreatePackagerWhenTopicDisabled() {

      new Expectations() {

         {
            mockOdeProperties.isEnabledVsdKafkaTopic();
            result = false;

            new BsmToVsdPackager((MessageProducer) any, anyString);
            times = 0;

         }
      };
      new BsmToVsdPackagerController(mockOdeProperties);
   }

   @Test
   public void shouldCreatePackagerWhenTopicEnabled(@Capturing MessageProducer<?,?> capturingMessageProducer,
         @Capturing MessageConsumer<?, ?> capturingMessageConsumer) {
      new Expectations() {
         {
            mockOdeProperties.isEnabledVsdKafkaTopic();
            result = true;

            new BsmToVsdPackager((MessageProducer) any, anyString);
            times = 1;

         }
      };
      new BsmToVsdPackagerController(mockOdeProperties);
   }

}
