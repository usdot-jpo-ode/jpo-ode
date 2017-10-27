package us.dot.its.jpo.ode.udp.vsd;

//TODO open-ode
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import us.dot.its.jpo.ode.OdeProperties;
//import us.dot.its.jpo.ode.dds.AbstractSubscriberDepositor;
//
///**
// * Publishes VSDs to SDC.
// */
//public class VsdDepositor extends AbstractSubscriberDepositor {
//
//   //TODO open-ode
////   private VehSitDataMessageDeserializer deserializer;
//
//   public VsdDepositor(OdeProperties odeProps) {
//      super(odeProps, odeProps.getVsdDepositorPort());
//      //TODO open-ode
////      this.deserializer = new VehSitDataMessageDeserializer();
//   }
//
//   @Override
//   public SemiDialogID getDialogId() {
//      return SemiDialogID.vehSitData;
//   }
//
//   @Override
//   public TemporaryID getRequestId(byte[] serializedMsg) {
//      return null;
//      //TODO open-ode
////      VehSitDataMessage msg = deserializer.deserialize(null, serializedMsg);
////      return msg.getRequestID();
//   }
//
//   @Override
//   public Logger getLogger() {
//      return LoggerFactory.getLogger(this.getClass());
//   }
//   
//   @Override
//   public byte[] encodeMessage(byte[] serializedMsg) {
//      return serializedMsg;
//      VehSitDataMessage msg = deserializer.deserialize(null, serializedMsg);
//      logger.info("VSD ready to send (JSON): {}", msg);
//      
//      byte[] encodedMsg = null;
//      try {
//         encodedMsg = coder.encode(msg).array();
//         logger.info("VSD ready to send (HEX): {}", HexUtils.toHexString(encodedMsg));
//      } catch (EncodeFailedException | EncodeNotSupportedException e) {
//         logger.error("Failed to encode serialized VSD for sending.", e);
//      }
//      
//      return encodedMsg;
//   }
//}
