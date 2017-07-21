package us.dot.its.jpo.ode.dds;

import org.junit.Ignore;
import org.junit.Test;

import mockit.Injectable;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;

public class AbstractSubscriberDepositorTest {

   @Tested
   AbstractSubscriberDepositor<?, ?> testAbstractSubscriberDepositor;

   @Injectable
   OdeProperties mockOdeProperties;

   @Injectable
   int mockPort;

   @Ignore
   @Test
   public void testCall() {
      // TODO
   }

}
