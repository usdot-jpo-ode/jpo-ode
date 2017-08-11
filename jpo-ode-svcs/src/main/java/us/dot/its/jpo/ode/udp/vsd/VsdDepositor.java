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

   public SemiDialogID getDialogId() {
      return SemiDialogID.vehSitData;
   }

   @Override
   public TemporaryID getRequestId(byte[] encodedMsg) {
      VehSitDataMessage msg = deserializer.deserialize(null, encodedMsg);
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
}
