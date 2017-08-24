package us.dot.its.jpo.ode.dds;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.Executors;

import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oss.asn1.Coder;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.dsrc.TemporaryID;
import us.dot.its.jpo.ode.j2735.semi.SemiDialogID;
import us.dot.its.jpo.ode.udp.UdpUtil;
import us.dot.its.jpo.ode.udp.UdpUtil.UdpUtilException;
import us.dot.its.jpo.ode.udp.trust.TrustManager;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;
import us.dot.its.jpo.ode.wrapper.MessageProcessor;

public abstract class AbstractSubscriberDepositor extends MessageProcessor<String, byte[]> {

   protected final Logger logger;
   protected OdeProperties odeProperties;
   protected DatagramSocket socket;;
   protected TrustManager trustManager;
   protected Coder coder;
   protected MessageConsumer<String, byte[]> consumer;

   public AbstractSubscriberDepositor(OdeProperties odeProps, int port) {
      this.odeProperties = odeProps;
      this.coder = J2735.getPERUnalignedCoder();
      this.logger = getLogger();
      this.consumer = MessageConsumer.defaultByteArrayMessageConsumer(odeProps.getKafkaBrokers(),
            odeProps.getHostId() + this.getClass().getSimpleName(), this);

      try {
         logger.debug("Creating depositor socket on port {}", port);
         this.socket = new DatagramSocket(port);
         this.trustManager = new TrustManager(odeProps, socket);
      } catch (SocketException e) {
         logger.error("Error creating socket with port " + port, e);
      }
   }

   @Override
   public byte[] call() {
      if (null == record.value()) {
         return null;
      }

      logger.info("Received data message for deposit");

      TemporaryID requestID = getRequestId(record.value());
      SemiDialogID dialogID = getDialogId();

      try {
         if (trustManager.establishTrust(requestID, dialogID)) {
            logger.debug("Sending message to SDC IP: {} Port: {}", odeProperties.getSdcIp(),
                  odeProperties.getSdcPort());
            sendToSdc(encodeMessage((byte[]) record.value()));
            trustManager.incrementSessionTracker(requestID);
         } else {
            logger.error("Failed to establish trust, not sending message.");
         }
      } catch (UdpUtilException e) {
         logger.error("Error Sending message to SDC", e);
         return null;
      }

      String hexRequestID = HexUtils.toHexString(requestID.byteArrayValue());
      logger.info("Messages sent since sessionID {} start: {}/{}", hexRequestID,
            trustManager.getSessionMessageCount(requestID), odeProperties.getMessagesUntilTrustReestablished());

      if (trustManager.getSessionMessageCount(requestID) >= odeProperties.getMessagesUntilTrustReestablished()) {
         trustManager.endTrustSession(requestID);
      }

      return record.value();
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

   public Logger getLogger() {
      return LoggerFactory.getLogger(this.getClass());
   }
   
   public TemporaryID getRequestId(byte[] encodedMsg) {
      return new TemporaryID(encodedMsg);
   };

   public abstract SemiDialogID getDialogId();
   public abstract byte[] encodeMessage(byte[] serializedMsg);
}
