package us.dot.its.jpo.ode.udp;

import java.util.concurrent.Executors;

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
import us.dot.its.jpo.ode.wrapper.MessageConsumer;

/**
 * Centralized UDP service dispatcher.
 *
 */
@Controller
public class UdpServicesController {

   private static Logger logger = LoggerFactory.getLogger(UdpServicesController.class);

   @Autowired
   public UdpServicesController(OdeProperties odeProps) {
      super();

      // BSM
      BsmReceiver bsmReceiver = new BsmReceiver(odeProps);
      logger.info("Launching {} ...", bsmReceiver.getClass().getSimpleName());
      Executors.newSingleThreadExecutor().submit(bsmReceiver);

      
      // ISD
      IsdDepositor isdDepositor = new IsdDepositor(odeProps);
      
      logger.info("Launching {} ...", isdDepositor.getClass().getSimpleName());
      MessageConsumer<String, byte[]> isdConsumer = MessageConsumer.defaultByteArrayMessageConsumer(
            odeProps.getKafkaBrokers(), odeProps.getHostId() + this.getClass().getSimpleName(), isdDepositor);
      isdDepositor.subscribe(isdConsumer, odeProps.getKafkaTopicEncodedIsd());
      

      IsdReceiver isdReceiver = new IsdReceiver(odeProps);
      logger.info("Launching {} ...", isdReceiver.getClass().getSimpleName());
      Executors.newSingleThreadExecutor().submit(isdReceiver);

      
      // VSD
      VsdDepositor vsdDepositor = new VsdDepositor(odeProps);
      logger.info("Launching {} ...", vsdDepositor.getClass().getSimpleName());

      MessageConsumer<String, String> vsdConsumer = MessageConsumer.defaultStringMessageConsumer(
            odeProps.getKafkaBrokers(), odeProps.getHostId() + this.getClass().getSimpleName(), vsdDepositor);

      // TODO ODE-314 Using raw JSON for testing. Switch to Filtered JSON.
      vsdDepositor.subscribe(vsdConsumer, odeProps.getKafkaTopicBsmRawJson());

      VsdReceiver vsdReceiver = new VsdReceiver(odeProps);
      logger.info("Launching {} ...", vsdReceiver.getClass().getSimpleName());
      Executors.newSingleThreadExecutor().submit(vsdReceiver);
   }

}
