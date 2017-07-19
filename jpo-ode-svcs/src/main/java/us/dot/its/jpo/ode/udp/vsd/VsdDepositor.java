package us.dot.its.jpo.ode.udp.vsd;

import java.io.ByteArrayInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.AbstractSubscriberDepositor;
import us.dot.its.jpo.ode.j2735.dsrc.TemporaryID;
import us.dot.its.jpo.ode.j2735.semi.SemiDialogID;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage;

/**
 * Publishes VSDs to SDC.
 */
public class VsdDepositor extends AbstractSubscriberDepositor {

   public VsdDepositor(OdeProperties odeProps) {
      super(odeProps, odeProps.getVsdDepositorPort());

      if (!odeProps.getDepositSanitizedBsmToSdc()) {
         logger.warn("WARNING - SDC BSM/VSD deposit option disabled, not starting VSD depositor service.");
         this.consumer = null;
         return;
      }
      consumer.setName(this.getClass().getSimpleName());
   }

   public SemiDialogID getDialogId() {
      return SemiDialogID.vehSitData;
   }

   public TemporaryID getRequestId(byte[] encodedMsg) {
      TemporaryID reqID = null;
      try {
         reqID = ((VehSitDataMessage) coder.decode(new ByteArrayInputStream(encodedMsg), new VehSitDataMessage()))
               .getRequestID();

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
