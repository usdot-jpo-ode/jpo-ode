package us.dot.its.jpo.ode.udp.vsd;

import java.io.ByteArrayInputStream;

import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.AbstractSubscriberDepositor;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.dsrc.TemporaryID;
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
      if (null == record.value()) {
         return new byte[0];
      }
      
      logger.info("Received data: {}", HexUtils.toHexString(record.value()));

      byte[] encodedVsd = record.value();
      try {
         if (trustSession.establishTrust(getRequestId(), SemiDialogID.vehSitData)) {
            logger.debug("Sending VSD to SDC IP: {} Port: {}", odeProperties.getSdcIp(), odeProperties.getSdcPort());

            sendToSdc(encodedVsd);
            messagesSent++;
         } else {
            logger.error("Failed to establish trust, not sending VSD.");
         }
      } catch (UdpUtilException e) {
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

   public TemporaryID getRequestId() {
      TemporaryID reqID = null;
      try {
         reqID = ((VehSitDataMessage) J2735.getPERUnalignedCoder().decode(new ByteArrayInputStream(record.value()),
               new VehSitDataMessage())).requestID;

      } catch (DecodeFailedException | DecodeNotSupportedException e) {
         logger.error("Depositor failed to decode ISD message: {}", e);
      }

      return reqID;
   }

   @Override
   public Logger getLogger() {
      return LoggerFactory.getLogger(this.getClass());
   }
}
