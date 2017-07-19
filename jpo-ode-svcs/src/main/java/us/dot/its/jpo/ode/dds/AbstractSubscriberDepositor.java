package us.dot.its.jpo.ode.dds;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;

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
   protected DatagramSocket socket = null;
   protected TrustManager trustManager;
   protected Coder coder;
   protected ExecutorService pool;
   protected MessageConsumer<String, byte[]> consumer;

   public AbstractSubscriberDepositor(OdeProperties odeProps, int port) {
      this.odeProperties = odeProps;
      this.coder = J2735.getPERUnalignedCoder();
      logger = getLogger();

      try {
         logger.debug("Creating depositor socket on port {}", port);
         socket = new DatagramSocket(port);
         trustManager = new TrustManager(odeProps, socket);
      } catch (SocketException e) {
         logger.error("Error creating socket with port " + port, e);
      }
   }

   @Override
   public byte[] call() {
      if (null == record.value()) {
         return new byte[0];
      }

      logger.info("Received data message for deposit");

      TemporaryID requestID = getRequestId(record.value());
      SemiDialogID dialogID = getDialogId();

      try {
         if (trustManager.establishTrust(requestID, dialogID)) {
            logger.debug("Sending message to SDC IP: {} Port: {}", odeProperties.getSdcIp(),
                  odeProperties.getSdcPort());
            sendToSdc((byte[]) record.value());
            trustManager.incrementSessionTracker(requestID);
         } else {
            logger.error("Failed to establish trust, not sending message.");
         }
      } catch (UdpUtilException e) {
         logger.error("Error Sending message to SDC", e);
         return new byte[0];
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

   public DatagramSocket getSocket() {
      return socket;
   }

   public void setSocket(DatagramSocket socket) {
      this.socket = socket;
   }

   public abstract Logger getLogger();

   public abstract SemiDialogID getDialogId();

   public abstract TemporaryID getRequestId(byte[] encodedMsg);
}
