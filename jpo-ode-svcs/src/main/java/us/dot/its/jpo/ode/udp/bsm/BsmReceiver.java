package us.dot.its.jpo.ode.udp.bsm;

import java.net.DatagramPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.UdpHexDecoder;

public class BsmReceiver extends AbstractUdpReceiverPublisher {

   private static Logger logger = LoggerFactory.getLogger(BsmReceiver.class);

   private StringPublisher bsmPublisher;

   @Autowired
   public BsmReceiver(OdeProperties odeProps) {
      this(odeProps, odeProps.getBsmReceiverPort(), odeProps.getBsmBufferSize());

      this.bsmPublisher = new StringPublisher(odeProps);
   }

   public BsmReceiver(OdeProperties odeProps, int port, int bufferSize) {
      super(odeProps, port, bufferSize);

      this.bsmPublisher = new StringPublisher(odeProps);
   }

   @Override
   public void run() {

      logger.debug("BSM UDP Receiver Service started.");

      byte[] buffer = new byte[bufferSize];

      DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

      do {
         try {
            logger.debug("Waiting for UDP BSM packets...");
            socket.receive(packet);
            if (packet.getLength() > 0) {
               String bsmJson = UdpHexDecoder.buildJsonBsmFromPacket(packet);

               if(bsmJson != null){
                  bsmPublisher.publish(bsmJson, bsmPublisher.getOdeProperties().getKafkaTopicOdeRawEncodedBSMJson());
               }
               
               
            }
         } catch (Exception e) {
            logger.error("Error receiving packet", e);
         }
      } while (!isStopped());
   }

   
}
