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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.udp.bsm.BsmReceiver;
import us.dot.its.jpo.ode.udp.isd.IsdDepositor;
import us.dot.its.jpo.ode.udp.isd.IsdReceiver;

/**
 * Centralized UDP service dispatcher.
 *
 */
@Controller
public class UdpServicesController {

   private Logger logger = LoggerFactory.getLogger(UdpServicesController.class);

   @Autowired
   public UdpServicesController(OdeProperties odeProps) {
      super();

      // Start the UDP receivers
      ServiceManager rm = new ServiceManager(new UdpServiceThreadFactory("UdpReceiverManager"));

      logger.debug("Starting UDP receiver services...");
      rm.submit(new BsmReceiver(odeProps));
      rm.submit(new IsdReceiver(odeProps));
   // TODO open-ode
//      rm.submit(new VsdReceiver(odeProps));
      logger.debug("UDP receiver services started.");

      logger.debug("Starting UDP depositor services...");
      rm.submit(new IsdDepositor(odeProps), odeProps.getKafkaTopicIsdPojo());
      if (odeProps.getDepositSanitizedBsmToSdc()) {
      // TODO open-ode
//         rm.submit(new VsdDepositor(odeProps), odeProps.getKafkaTopicVsdPojo());
      } else {
         logger.warn("WARNING - SDC BSM/VSD deposit option disabled, not starting VSD depositor service.");
      }
      logger.debug("UDP depositor services started.");
   }
}
