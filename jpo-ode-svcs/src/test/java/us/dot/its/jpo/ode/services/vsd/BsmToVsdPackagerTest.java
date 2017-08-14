package us.dot.its.jpo.ode.services.vsd;

import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;

import mockit.Injectable;
import mockit.Tested;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class BsmToVsdPackagerTest {

   @Tested
   BsmToVsdPackager testBsmToVsdPackager;

   @Injectable
   MessageProducer<String, byte[]> injectableMessageProducer;
   @Injectable
   String injectableOutputTopic = "testOutputTopic";

   @Test
   @Ignore
   public void test() {
      // TODO
      fail("Not yet implemented");
   }

}
