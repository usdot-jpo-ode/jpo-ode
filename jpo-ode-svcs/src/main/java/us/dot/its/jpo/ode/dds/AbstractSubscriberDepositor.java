package us.dot.its.jpo.ode.dds;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;

import com.oss.asn1.Coder;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.udp.UdpUtil;
import us.dot.its.jpo.ode.udp.UdpUtil.UdpUtilException;
import us.dot.its.jpo.ode.udp.trust.TrustSession;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;
import us.dot.its.jpo.ode.wrapper.MessageProcessor;

public abstract class AbstractSubscriberDepositor<K, V> extends MessageProcessor<K, V> {

   protected final Logger logger;
   protected OdeProperties odeProperties;
   protected DatagramSocket socket = null;
   protected TrustSession trustSession;
   protected int messagesSent;
   protected Coder coder;
   protected ExecutorService pool;
   protected MessageConsumer<K, V> consumer;

   public AbstractSubscriberDepositor(OdeProperties odeProps, int port) {
      this.odeProperties = odeProps;
      this.messagesSent = 0;
      this.coder = J2735.getPERUnalignedCoder();
      
      logger = getLogger();

      try {
         logger.debug("Creating depositor socket on port {}", port);
         socket = new DatagramSocket(port);
         trustSession = new TrustSession(odeProps, socket);
      } catch (SocketException e) {
         logger.error("Error creating socket with port " + port, e);
      }
   }

   /**
    * Starts a Kafka listener that runs call() every time a new msg arrives
    * 
    * @param consumer
    * @param topics
    */
   public void subscribe(String... topics) {
      for (String topic : topics) {
         logger.debug("Subscribing to {}", topic);
      }
      Executors.newSingleThreadExecutor().submit(() -> consumer.subscribe(topics));
   }

   public void sendToSdc(byte[] msgBytes) throws UdpUtilException {
      UdpUtil.send(socket, msgBytes, odeProperties.getSdcIp(), odeProperties.getSdcPort());
   }

   public abstract Logger getLogger();

   public DatagramSocket getSocket() {
      return socket;
   }

   public void setSocket(DatagramSocket socket) {
      this.socket = socket;
   }
}
