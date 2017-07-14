package us.dot.its.jpo.ode.udp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.AbstractSubscriberDepositor;
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
      
      List<AbstractUdpReceiverPublisher> receiverList = new ArrayList<>();
      receiverList.add(new BsmReceiver(odeProps));
      receiverList.add(new IsdReceiver(odeProps));
      receiverList.add(new VsdReceiver(odeProps));
      startReceiverServices(receiverList);
      
      Map<AbstractSubscriberDepositor<?, ?>, String> depositorList = new HashMap<>();
      depositorList.put(new IsdDepositor(odeProps), odeProps.getKafkaTopicEncodedIsd());
      depositorList.put(new VsdDepositor(odeProps), odeProps.getKafkaTopicBsmRawJson());
      startDepositorServices(depositorList);
   }
   
   public void startDepositorServices(Map<AbstractSubscriberDepositor<?,?>, String> servicesMap) {
      for (Map.Entry<AbstractSubscriberDepositor<?,?>, String> entry : servicesMap.entrySet()) {
         AbstractSubscriberDepositor<?, ?> depper = entry.getKey();
         depper.subscribe(entry.getValue());
     }
   }
   
   public void startReceiverServices(List<AbstractUdpReceiverPublisher> recvList) {
      for(AbstractUdpReceiverPublisher recv : recvList ) {
      logger.debug("Starting UDP receiver service {}", recv.getClass().getSimpleName());
      UdpServiceThreadFactory tf = new UdpServiceThreadFactory(recv.getClass().getSimpleName());
      Executors.newSingleThreadExecutor(tf).submit(recv);
      }
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
