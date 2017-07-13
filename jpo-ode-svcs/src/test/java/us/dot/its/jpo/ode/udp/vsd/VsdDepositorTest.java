package us.dot.its.jpo.ode.udp.vsd;

import static org.junit.Assert.assertNull;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;

public class VsdDepositorTest {

   @Tested
   VsdDepositor testVsdDepositor;

   @Injectable
   OdeProperties mockOdeProperties;

   @Mocked
   ConsumerRecord<String, String> mockConsumerRecord;

   @Test
   public void shouldReturnNullWhenDepositOptionFalse() {
      new Expectations() {
         {
            mockConsumerRecord.value();
            mockOdeProperties.getDepositSanitizedBsmToSdc();
            result = false;
         }
      };
      testVsdDepositor.setRecord(mockConsumerRecord);
      assertNull(testVsdDepositor.deposit());
   }

}
