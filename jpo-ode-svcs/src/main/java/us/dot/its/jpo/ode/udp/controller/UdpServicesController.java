package us.dot.its.jpo.ode.udp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.udp.bsm.BsmReceiver;
import us.dot.its.jpo.ode.udp.generic.GenericReceiver;
import us.dot.its.jpo.ode.udp.tim.TimReceiver;
import us.dot.its.jpo.ode.udp.ssm.SsmReceiver;
import us.dot.its.jpo.ode.udp.srm.SrmReceiver;
import us.dot.its.jpo.ode.udp.spat.SpatReceiver;
import us.dot.its.jpo.ode.udp.map.MapReceiver;
import us.dot.its.jpo.ode.udp.psm.PsmReceiver;

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

      // BSM internal
      rm.submit(new BsmReceiver(odeProps));

      // TIM internal
      rm.submit(new TimReceiver(odeProps));

      // SSM internal port
      rm.submit(new SsmReceiver(odeProps));
      
      // SRM internal port
      rm.submit(new SrmReceiver(odeProps));

      // SPAT internal port
      rm.submit(new SpatReceiver(odeProps));

      // MAP internal port
      rm.submit(new MapReceiver(odeProps));

      // PSM internal port
      rm.submit(new PsmReceiver(odeProps));

      // Generic Receiver internal port
      rm.submit(new GenericReceiver(odeProps));

      logger.debug("UDP receiver services started.");
   }
}
