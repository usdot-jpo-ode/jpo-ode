/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.ode.udp.controller;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

import mockit.Injectable;
import mockit.Tested;
import us.dot.its.jpo.ode.udp.controller.UdpServiceThreadFactory;

public class UdpServiceThreadFactoryTest {

   @Tested
   UdpServiceThreadFactory testUdpServiceThreadFactory;

   @Injectable
   String expectedName = "testName123";

   @Test @Ignore
   public void constructorShouldSetName() {
      assertEquals(expectedName, testUdpServiceThreadFactory.getThreadName());
   }

   @Test @Ignore
   public void shouldReturnNamedThread(@Injectable Thread testThread) {
      Thread actualThread = testUdpServiceThreadFactory.newThread(testThread);
      assertEquals(expectedName, actualThread.getName());
   }

   @Test @Ignore
   public void shouldSetThreadName(@Injectable Thread testThreadTwo) {
      Thread actualThreadTwo = testUdpServiceThreadFactory.newThread(testThreadTwo);
      testUdpServiceThreadFactory.setThreadName(expectedName);
     
      assertEquals(expectedName, actualThreadTwo.getName());
      
   }
   
}
