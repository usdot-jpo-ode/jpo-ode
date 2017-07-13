package us.dot.its.jpo.ode.dds;

import static org.junit.Assert.assertNull;

import org.junit.Test;

import mockit.Injectable;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;

public class AbstractSubscriberDepositorTest {

   @Tested
   TestAbstractSubscriberDepositor testAbstractSubscriberDepositor;

   @Injectable
   OdeProperties mockOdeProperties;

   @Injectable
   int mockPort;

   @Test
   public void test() {
      assertNull(testAbstractSubscriberDepositor.call());
   }

}
