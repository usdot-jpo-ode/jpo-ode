package us.dot.its.jpo.ode.udp.vsd;

import static org.junit.Assert.assertNull;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Ignore;
import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;

public class VsdDepositorTest {

   @Tested
   VsdDepositor testVsdDepositor;

   @Injectable
   OdeProperties mockOdeProperties;

   @Mocked
   ConsumerRecord<String, byte[]> mockConsumerRecord;

   @Test @Ignore
   public void shouldReturnNullWhenDepositOptionFalse(@Capturing MessageConsumer<?, ?> mockMessageConsumer) {
      new Expectations() {
         {
            mockOdeProperties.getDepositSanitizedBsmToSdc();
            result = false;
         }
      };
      testVsdDepositor.setRecord(mockConsumerRecord);
      assertNull(testVsdDepositor.call());
   }

   @Test @Ignore
   public void shouldNotSendIncompleteVsd(@Capturing MessageConsumer<?, ?> mockMessageConsumer,
         @Capturing JsonUtils mockJsonUtils, @Mocked J2735Bsm mockJ2735Bsm) {
      new Expectations() {
         {
            mockOdeProperties.getDepositSanitizedBsmToSdc();
            result = true;

            JsonUtils.fromJson(anyString, (Class) any);
            result = mockJ2735Bsm;

            mockJ2735Bsm.getCoreData().getId();
            result = anyString;
         }
      };
      testVsdDepositor.setRecord(mockConsumerRecord);
      assertNull(testVsdDepositor.call());
   }

}
