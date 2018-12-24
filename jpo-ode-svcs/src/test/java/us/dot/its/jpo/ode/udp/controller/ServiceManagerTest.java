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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.dds.AbstractSubscriberDepositor;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.controller.ServiceManager;

public class ServiceManagerTest {

   @Tested
   ServiceManager testServiceManager;

   @Mocked
   Executors mockExecutors;
   
   @Injectable
   ThreadFactory mockThreadFactory;
   

   @Test
   public void receiverSubmitCallsExecutorService(@Capturing Executors mockExecutors,
         @Injectable AbstractUdpReceiverPublisher mockAbstractUdpReceiverPublisher,
         @Mocked ExecutorService mockExecutorService) {

      new Expectations() {
         {
            Executors.newSingleThreadExecutor((ThreadFactory) any);
            result = mockExecutorService;

            mockExecutorService.submit((AbstractUdpReceiverPublisher) any);
         }
      };

      testServiceManager.submit(mockAbstractUdpReceiverPublisher);

   }
@Test
   public void depositorCallsSubscribe(@Mocked AbstractSubscriberDepositor mockAbstractSubscriberDepositor) {

      new Expectations() {
         {
            mockAbstractSubscriberDepositor.start(anyString);
            times = 1;
         }
      };

      testServiceManager.submit(mockAbstractSubscriberDepositor, "Test");
   }

}
