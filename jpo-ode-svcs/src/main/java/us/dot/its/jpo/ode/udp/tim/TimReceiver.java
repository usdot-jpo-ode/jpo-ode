package us.dot.its.jpo.ode.udp.tim;

import java.net.DatagramPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher;
import us.dot.its.jpo.ode.udp.UdpHexDecoder;

public class TimReceiver extends AbstractUdpReceiverPublisher {
   private static Logger logger = LoggerFactory.getLogger(TimReceiver.class);

   private StringPublisher timPublisher;

   @Autowired
   public TimReceiver(OdeProperties odeProps, OdeKafkaProperties odeKafkaProperties) {
      this(odeProps, odeKafkaProperties, odeProps.getTimReceiverPort(), odeProps.getTimBufferSize());
   }

   public TimReceiver(OdeProperties odeProps, OdeKafkaProperties odeKafkaProperties, int port, int bufferSize) {
      super(odeProps, port, bufferSize);

      this.timPublisher = new StringPublisher(odeProperties, odeKafkaProperties);
   }

   @Override
   public void run() {
      logger.debug("TIM UDP Receiver Service started.");

      byte[] buffer = new byte[bufferSize];

      DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

      do {
         try {
            logger.debug("Waiting for UDP TIM packets...");
            socket.receive(packet);
            if (packet.getLength() > 0) {
               
               String timJson = UdpHexDecoder.buildJsonTimFromPacket(packet);
               if(timJson != null){
                  timPublisher.publish(timJson, timPublisher.getOdeProperties().getKafkaTopicOdeRawEncodedTIMJson());
               }
               
            }
         } catch (Exception e) {
            logger.error("Error receiving packet", e);
         }
      } while (!isStopped());
   }

   
}
