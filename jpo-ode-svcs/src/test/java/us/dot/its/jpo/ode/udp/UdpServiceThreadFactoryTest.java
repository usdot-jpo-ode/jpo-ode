package us.dot.its.jpo.ode.udp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import mockit.Injectable;
import mockit.Tested;
import us.dot.its.jpo.ode.udp.manager.UdpServiceThreadFactory;

public class UdpServiceThreadFactoryTest {

   @Tested
   UdpServiceThreadFactory testUdpServiceThreadFactory;

   @Injectable
   String expectedName = "testName123";

   @Test
   public void constructorShouldSetName() {
      assertEquals(expectedName, testUdpServiceThreadFactory.threadName);
   }

   @Test
   public void shouldReturnNamedThread(@Injectable Thread testThread) {
      Thread actualThread = testUdpServiceThreadFactory.newThread(testThread);
      assertEquals(expectedName, actualThread.getName());
   }

}
