package us.dot.its.jpo.ode.udp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.udp.bsm.BsmReceiver;
import us.dot.its.jpo.ode.udp.isd.IsdDepositor;
import us.dot.its.jpo.ode.udp.isd.IsdReceiver;
import us.dot.its.jpo.ode.udp.vsd.VsdDepositor;
import us.dot.its.jpo.ode.udp.vsd.VsdReceiver;

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
      rm.submit(new VsdReceiver(odeProps));
      logger.debug("UDP receiver services started.");

      logger.debug("Starting UDP depositor services...");
      rm.submit(new IsdDepositor(odeProps), odeProps.getKafkaTopicEncodedIsd());
      if (odeProps.getDepositSanitizedBsmToSdc()) {
         rm.submit(new VsdDepositor(odeProps), odeProps.getKafkaTopicEncodedVsd());
      } else {
         logger.warn("WARNING - SDC BSM/VSD deposit option disabled, not starting VSD depositor service.");
      }
      logger.debug("UDP depositor services started.");
   }
}
