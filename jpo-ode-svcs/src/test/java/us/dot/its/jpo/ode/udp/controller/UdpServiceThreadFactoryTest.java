package us.dot.its.jpo.ode.udp.controller;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import mockit.Injectable;
import mockit.Tested;
import us.dot.its.jpo.ode.udp.controller.UdpServiceThreadFactory;

public class UdpServiceThreadFactoryTest {

   @Tested
   UdpServiceThreadFactory testUdpServiceThreadFactory;

   @Injectable
   String expectedName = "testName123";

   @Test
   public void constructorShouldSetName() {
      assertEquals(expectedName, testUdpServiceThreadFactory.getThreadName());
   }

   @Test
   public void shouldReturnNamedThread(@Injectable Thread testThread) {
      Thread actualThread = testUdpServiceThreadFactory.newThread(testThread);
      assertEquals(expectedName, actualThread.getName());
   }

   @Test
   public void shouldSetThreadName(@Injectable Thread testThreadTwo) {
      Thread actualThreadTwo = testUdpServiceThreadFactory.newThread(testThreadTwo);
      testUdpServiceThreadFactory.setThreadName(expectedName);
     
      assertEquals(expectedName, actualThreadTwo.getName());
      
   }
   
}
