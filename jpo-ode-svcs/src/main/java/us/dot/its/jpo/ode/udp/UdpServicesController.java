package us.dot.its.jpo.ode.udp;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

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

      // BSM
      BsmReceiver bsmReceiver = new BsmReceiver(odeProps);
      startReceiver(bsmReceiver);

      // ISD
      IsdDepositor isdDepositor = new IsdDepositor(odeProps);

//      MessageConsumer<String, byte[]> isdConsumer = MessageConsumer.defaultByteArrayMessageConsumer(
//            odeProps.getKafkaBrokers(), odeProps.getHostId() + this.getClass().getSimpleName(), isdDepositor);
//      isdConsumer.setName(isdDepositor.getClass().getSimpleName());
      isdDepositor.subscribe(odeProps.getKafkaTopicEncodedIsd());

      IsdReceiver isdReceiver = new IsdReceiver(odeProps);
      startReceiver(isdReceiver);

      // VSD
      VsdDepositor vsdDepositor = new VsdDepositor(odeProps);

//      MessageConsumer<String, String> vsdConsumer = MessageConsumer.defaultStringMessageConsumer(
//            odeProps.getKafkaBrokers(), odeProps.getHostId() + this.getClass().getSimpleName(), vsdDepositor);
//      vsdConsumer.setName(vsdDepositor.getClass().getSimpleName());

      // TODO ODE-314 Using raw JSON for testing. Switch to Filtered JSON.
      vsdDepositor.subscribe(odeProps.getKafkaTopicBsmRawJson());

      VsdReceiver vsdReceiver = new VsdReceiver(odeProps);
      startReceiver(vsdReceiver);
   }
   
   public void startReceiver(AbstractUdpReceiverPublisher recv) {
      logger.debug("Starting UDP receiver service {}", recv.getClass().getSimpleName());
      UdpServiceThreadFactory tf = new UdpServiceThreadFactory(recv.getClass().getSimpleName());
      Executors.newSingleThreadExecutor(tf).submit(recv);
   }

   public class UdpServiceThreadFactory implements ThreadFactory {
      String threadName;

      public UdpServiceThreadFactory(String name) {
         this.threadName = name;
      }

      @Override
      public Thread newThread(Runnable r) {
         Thread t = new Thread(r);
         t.setName(threadName);
         return t;
      }
   }

}
