package us.dot.its.jpo.ode.dds;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;

import com.oss.asn1.Coder;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.dsrc.TemporaryID;
import us.dot.its.jpo.ode.j2735.semi.SemiDialogID;
import us.dot.its.jpo.ode.udp.trust.TrustSession;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;
import us.dot.its.jpo.ode.wrapper.MessageProcessor;

public abstract class AbstractSubscriberDepositor<K, V> extends MessageProcessor<K, V> {

   protected Logger logger;
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
      this.logger = getLogger();

      try {
         logger.debug("Creating depositor socket on port {}", port);
         socket = new DatagramSocket(port);
         trustSession = new TrustSession(odeProps, socket);
         pool = Executors.newCachedThreadPool(Executors.defaultThreadFactory());
      } catch (SocketException e) {
         logger.error("Error creating socket with port " + port, e);
      }
   }
   
   @Override
   public byte[] call() {
      logger.debug("Subscriber received data.");
      return deposit();
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

   protected abstract TemporaryID getRequestId();

   protected abstract SemiDialogID getDialogId();

   public DatagramSocket getSocket() {
      return socket;
   }

   public void setSocket(DatagramSocket socket) {
      this.socket = socket;
   }

   protected abstract byte[] deposit();

   protected abstract Logger getLogger();
}
