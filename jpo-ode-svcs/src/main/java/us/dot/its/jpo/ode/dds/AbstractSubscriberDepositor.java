package us.dot.its.jpo.ode.dds;

import java.net.DatagramSocket;
import java.net.SocketException;

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
import us.dot.its.jpo.ode.wrapper.AbstractSubscriberProcessor;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;

/**
 * @author 572682
 * This abstract class provides common and basic functionality for subscribing to
 * a messaging topic, processing the messages as implemented by a given MessageProcessor
 * and depositing the resultant data to SDC/SDW.  
 *
 */
public abstract class AbstractSubscriberDepositor extends AbstractSubscriberProcessor<String, byte[]> {

   protected OdeProperties odeProperties;
   protected DatagramSocket socket;;
   protected TrustManager trustManager;
   protected Coder coder;
   private MessageConsumer<String, byte[]> consumer;

   public AbstractSubscriberDepositor(OdeProperties odeProps, int port) {
      super();
      this.odeProperties = odeProps;
      this.consumer = MessageConsumer.defaultByteArrayMessageConsumer(odeProps.getKafkaBrokers(),
         odeProps.getHostId() + this.getClass().getSimpleName(), this);
      this.consumer.setName(this.getClass().getSimpleName());

      this.coder = J2735.getPERUnalignedCoder();

      try {
         logger.debug("Creating depositor socket on port {}", port);
         this.socket = new DatagramSocket(port);
         this.trustManager = new TrustManager(odeProps, socket);
      } catch (SocketException e) {
         logger.error("Error creating socket with port " + port, e);
      }
   }

   public void start(String... inputTopics) {
      super.start(consumer, inputTopics);
   }

   @Override
   public Object process(byte[] consumedData) {
      if (null == consumedData || consumedData.length == 0) {
         return null;
      }

      logger.info("Received data message for deposit");

      TemporaryID requestID = getRequestId(consumedData);
      SemiDialogID dialogID = getDialogId();

      byte[] encodedMessage = null;
      try {
         if (trustManager.establishTrust(requestID, dialogID)) {
            logger.debug("Sending message to SDC IP: {} Port: {}", odeProperties.getSdcIp(),
                  odeProperties.getSdcPort());
            encodedMessage = encodeMessage(consumedData);
            sendToSdc(encodedMessage);
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
      return encodedMessage;
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
