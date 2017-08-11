package us.dot.its.jpo.ode.udp.vsd;

import java.io.ByteArrayInputStream;

import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oss.asn1.AbstractData;
import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;
import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.AbstractSubscriberDepositor;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.dsrc.TemporaryID;
import us.dot.its.jpo.ode.j2735.semi.SemiDialogID;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage;
import us.dot.its.jpo.ode.udp.UdpUtil.UdpUtilException;
import us.dot.its.jpo.ode.wrapper.VehSitDataMessageDeserializer;

/**
 * Publishes VSDs to SDC.
 */
public class VsdDepositor extends AbstractSubscriberDepositor {

   private VehSitDataMessageDeserializer deserializer;

   public VsdDepositor(OdeProperties odeProps) {
      super(odeProps, odeProps.getVsdDepositorPort());
      consumer.setName(this.getClass().getSimpleName());
      this.deserializer = new VehSitDataMessageDeserializer();
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
            sendToSdc(encodeMessage((byte[]) record.value()));
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

   @Override
   public SemiDialogID getDialogId() {
      return SemiDialogID.vehSitData;
   }

   @Override
   public TemporaryID getRequestId(byte[] serializedMsg) {
      VehSitDataMessage msg = deserializer.deserialize(null, serializedMsg);
      return msg.getRequestID();
//      TemporaryID reqID = null;
//      try {
//         reqID = ((VehSitDataMessage) coder.decode(new ByteArrayInputStream(encodedMsg), new VehSitDataMessage()))
//               .getRequestID();
//
//      } catch (DecodeFailedException e) {
//            AbstractData partialDecodedMessage = e.getDecodedData();
//            if (partialDecodedMessage != null) {
//                logger.error("Error, message only partially decoded.");
//                reqID = ((VehSitDataMessage)partialDecodedMessage).getRequestID();
//            } else {
//                logger.debug("Failed to partially decode message.");
//            }
//      } catch (DecodeNotSupportedException e) {
//         logger.error("Depositor failed to decode VSD message: {}", e);
//      }
//
//      return reqID;
   }

   @Override
   public Logger getLogger() {
      return LoggerFactory.getLogger(this.getClass());
   }
   
   public byte[] encodeMessage(byte[] serializedMsg) {
      VehSitDataMessage msg = deserializer.deserialize(null, serializedMsg);
      try {
         return J2735.getPERUnalignedCoder().encode(msg).array();
      } catch (EncodeFailedException | EncodeNotSupportedException e) {
         logger.error("Failed to encode serialized VSD for sending.", e);
         return null;
      }
   }
}
