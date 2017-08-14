package us.dot.its.jpo.ode.udp.vsd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;

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

   @Override
   public SemiDialogID getDialogId() {
      return SemiDialogID.vehSitData;
   }

   @Override
   public TemporaryID getRequestId(byte[] serializedMsg) {
      VehSitDataMessage msg = deserializer.deserialize(null, serializedMsg);
      return msg.getRequestID();
   }

   @Override
   public Logger getLogger() {
      return LoggerFactory.getLogger(this.getClass());
   }
   
   @Override
   public byte[] encodeMessage(byte[] serializedMsg) {
      VehSitDataMessage msg = deserializer.deserialize(null, serializedMsg);
      logger.info("VSD ready to send: {}", msg);
      
      byte[] encodedMsg = null;
      try {
         encodedMsg = coder.encode(msg).array();
      } catch (EncodeFailedException | EncodeNotSupportedException e) {
         logger.error("Failed to encode serialized VSD for sending.", e);
      }
      
      return encodedMsg;
   }
}
