package us.dot.its.jpo.ode.newcoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.usdot.cv.security.msg.IEEE1609p2Message;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735MessageFrame;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssJ2735Coder;
import us.dot.its.jpo.ode.security.SecurityManager;

public class ByteDecoderPublisher {

   private static final Logger logger = LoggerFactory.getLogger(ByteDecoderPublisher.class);
   private MessagePublisher publisher;
   private OssJ2735Coder j2735Coder;

   public ByteDecoderPublisher(MessagePublisher dataPub, OssJ2735Coder j2735plugin) {
      this.publisher = dataPub;
      this.j2735Coder = j2735plugin;
   }

   public void decodeAndPublish(byte[] bytes) throws Exception {
      OdeData decoded;

      try {
         decoded = decode(bytes);
         if (decoded != null) {
            logger.debug("Decoded: {}", decoded);
            publisher.publish(decoded);
         }
      } catch (Exception e) {
         String msg = "Error decoding and publishing data.";
         EventLogger.logger.error(msg, e);
         throw new Exception("Error decoding and publishing data.", e);
      }
   }

   public OdeData decode(byte[] data) throws Exception {
      IEEE1609p2Message message = null;

      OdeData odeBsmData = null;
      OdeObject bsm = null;
      try {
         message = SecurityManager.decodeSignedMessage(data);
         if (message != null) {
            SecurityManager.validateGenerationTime(message);
            bsm = decodeBsm(message.getPayload());
         } else {
            bsm = decodeBsm(data);
         }
      } catch (Exception e) {
         logger.debug("Message does not have a valid signature");
      }

      if (bsm != null && message != null) {
         odeBsmData = DecoderPublisherUtils.createOdeBsmData((J2735Bsm) bsm, message, null, null);
      }

      return odeBsmData;
   }

   private OdeObject decodeBsm(byte[] bytes) {
      J2735MessageFrame mf = (J2735MessageFrame) j2735Coder.decodeUPERMessageFrameBytes(bytes);
      if (mf != null) {
         return mf.getValue();
      } else {
         return j2735Coder.decodeUPERBsmBytes(bytes);
      }
   }

}
