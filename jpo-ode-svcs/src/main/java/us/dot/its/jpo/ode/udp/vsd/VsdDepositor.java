package us.dot.its.jpo.ode.udp.vsd;

import java.io.ByteArrayInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oss.asn1.AbstractData;
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
      consumer.setName(this.getClass().getSimpleName());
   }

   public SemiDialogID getDialogId() {
      return SemiDialogID.vehSitData;
   }

   @Override
   public TemporaryID getRequestId(byte[] encodedMsg) {
      TemporaryID reqID = null;
      try {
         reqID = ((VehSitDataMessage) coder.decode(new ByteArrayInputStream(encodedMsg), new VehSitDataMessage()))
               .getRequestID();

      } catch (DecodeFailedException e) {
            AbstractData partialDecodedMessage = e.getDecodedData();
            if (partialDecodedMessage != null) {
                logger.error("Error, message only partially decoded.");
                reqID = ((VehSitDataMessage)partialDecodedMessage).getRequestID();
            } else {
                logger.debug("Ignoring extraneous bytes at the end of the input stream.");
            }
         logger.error("Depositor failed to decode VSD message: {}", e);
      } catch (DecodeNotSupportedException e) {
         logger.error("Depositor failed to decode VSD message: {}", e);
      }

      return reqID;
   }

   @Override
   public Logger getLogger() {
      return LoggerFactory.getLogger(this.getClass());
   }
}
