package us.dot.its.jpo.ode.udp.vsd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SerializationUtils;

import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.AbstractSubscriberDepositor;
import us.dot.its.jpo.ode.j2735.semi.SemiDialogID;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage;
import us.dot.its.jpo.ode.udp.UdpUtil.UdpUtilException;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;

/**
 * Publishes VSDs to SDC.
 */
public class VsdDepositor extends AbstractSubscriberDepositor<String, byte[]> {

   public VsdDepositor(OdeProperties odeProps) {
      super(odeProps, odeProps.getVsdDepositorPort());
      
      if (!odeProps.getDepositSanitizedBsmToSdc()) {
         logger.warn("WARNING - SDC BSM/VSD deposit option disabled, not starting VSD depositor service.");
         return;
      }
      
      consumer = MessageConsumer.defaultByteArrayMessageConsumer(odeProps.getKafkaBrokers(),
            odeProps.getHostId() + this.getClass().getSimpleName(), this);
      consumer.setName(this.getClass().getSimpleName());
   }

   @Override
   public byte[] call() {
      VehSitDataMessage vsd = (VehSitDataMessage) SerializationUtils.deserialize(record.value());
      logger.info("Received VSD.");

      byte[] encodedVsd = null;
      try {
         if (trustSession.establishTrust(vsd.requestID, SemiDialogID.vehSitData)) {
            logger.debug("Sending VSD to SDC IP: {} Port: {}", odeProperties.getSdcIp(), odeProperties.getSdcPort());

            encodedVsd = coder.encode(vsd).array();
            sendToSdc(encodedVsd);
            messagesSent++;
         } else {
            logger.error("Failed to establish trust, not sending VSD.");
         }
      } catch (UdpUtilException | EncodeFailedException | EncodeNotSupportedException e) {
         logger.error("Error Sending Isd to SDC", e);
         return new byte[0];
      }

      logger.info("VSDs sent since session start: {}/{}", messagesSent,
            odeProperties.getMessagesUntilTrustReestablished());

      if (messagesSent >= odeProperties.getMessagesUntilTrustReestablished()) {
         trustSession.setTrustEstablished(false);
      }

      return encodedVsd;
   }

   @Override
   public Logger getLogger() {
      return LoggerFactory.getLogger(this.getClass());
   }
}
