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

import static org.junit.Assert.assertNotNull;

import java.util.concurrent.ThreadFactory;

import org.junit.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Mocked;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.AbstractSubscriberDepositor;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
//TODO open-ode
//import us.dot.its.jpo.ode.udp.vsd.VsdDepositor;

public class UdpServicesControllerTest {

   @Mocked
   OdeProperties mockOdeProperties;

   @Capturing
   ServiceManager capturingServiceManager;
   @Mocked
   ServiceManager mockServiceManager;
   @Capturing
   AbstractUdpReceiverPublisher capturingAbstractUdpReceiverPublisher;
   @Capturing
   AbstractSubscriberDepositor capturingAbstractSubscriberDepositor;
 //TODO open-ode
//   @Capturing
//   VsdDepositor capturingVsdDepositor;

   @Test
   public void shouldSubmit5ThreadsVsdOn() {

      new Expectations() {
         {
            mockOdeProperties.getDepositSanitizedBsmToSdc();
            result = true;

            new ServiceManager((ThreadFactory) any);
            result = mockServiceManager;

            mockServiceManager.submit((AbstractUdpReceiverPublisher) any);
            times = 2;

            mockServiceManager.submit((AbstractSubscriberDepositor) any, anyString);
            times = 1;
         }
      };

      assertNotNull(new UdpServicesController(mockOdeProperties));
   }

   @Test
   public void shouldSubmit4ThreadsVsdOff() {

      new Expectations() {
         {
            mockOdeProperties.getDepositSanitizedBsmToSdc();
            result = false;

            new ServiceManager((ThreadFactory) any);
            result = mockServiceManager;

            mockServiceManager.submit((AbstractUdpReceiverPublisher) any);
            times = 2;

            mockServiceManager.submit((AbstractSubscriberDepositor) any, anyString);
            times = 1;

          //TODO open-ode
//            new VsdDepositor((OdeProperties) any);
//            times = 0;
         }
      };

      assertNotNull(new UdpServicesController(mockOdeProperties));
   }

}
