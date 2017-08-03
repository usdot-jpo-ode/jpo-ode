package us.dot.its.jpo.ode.newcoder;

import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.Ieee1609Dot2Data;
import gov.usdot.cv.security.msg.IEEE1609p2Message;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.model.SerialId;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735MessageFrame;
import us.dot.its.jpo.ode.plugin.j2735.oss.Oss1609dot2Coder;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssJ2735Coder;
import us.dot.its.jpo.ode.security.SecurityManager;
import us.dot.its.jpo.ode.security.SecurityManager.SecurityManagerException;

public class BinaryDecoderPublisher {

   private static final Logger logger = LoggerFactory.getLogger(BinaryDecoderPublisher.class);

   private OssJ2735Coder j2735Coder;

   private Oss1609dot2Coder ieee1609dotCoder;

   private SerialId serialId;

   private MessagePublisher publisher;

   private static AtomicInteger bundleId = new AtomicInteger(1);

   public BinaryDecoderPublisher(OssJ2735Coder jDec, Oss1609dot2Coder ieeeDec, SerialId serId, MessagePublisher dataPub) {
      this.j2735Coder = jDec;
      this.ieee1609dotCoder = ieeeDec;
      this.serialId = serId;

      this.serialId = serId;
      this.serialId.setBundleId(bundleId.incrementAndGet());

      this.publisher = dataPub;
   }

   public void decodeBinaryAndPublish(InputStream is, String fileName) throws Exception {
      OdeData decoded = null;

      do {
         try {
            decoded = decode(is, fileName);
            if (decoded != null) {
               logger.debug("Decoded: {}", decoded);
               publisher.publish(decoded);
            } else {
               logger.debug("Failed to decode, null.");
            }
         } catch (Exception e) {
            String msg = "Error decoding and publishing data.";
            EventLogger.logger.error(msg, e);
            logger.error(msg, e);
         }
      } while (decoded != null);
   }

   public OdeData decode(InputStream is, String fileName) throws Exception {
      Ieee1609Dot2Data ieee1609dot2Data = ieee1609dotCoder.decodeIeee1609Dot2DataStream(is);

      OdeObject bsm = null;
      OdeData odeBsmData = null;
      IEEE1609p2Message message = null;
      if (ieee1609dot2Data != null) {
         try {
            message = IEEE1609p2Message.convert(ieee1609dot2Data);

            bsm = getBsmPayload(message);
         } catch (Exception e) {
            logger.debug("Message does not have a valid signature");
            bsm = decodeBsm(ieee1609dot2Data.getContent().getSignedData().getTbsData().getPayload().getData()
                  .getContent().getUnsecuredData().byteArrayValue());
         }
      } else { // probably raw BSM or MessageFrame
         bsm = decodeBsm(is);
      }

      if (bsm != null) {
         odeBsmData = DecoderPublisherUtils.createOdeBsmData((J2735Bsm) bsm, message, fileName, serialId);
      }

      return odeBsmData;
   }

   private OdeObject decodeBsm(InputStream is) {
      J2735MessageFrame mf = (J2735MessageFrame) j2735Coder.decodeUPERMessageFrameStream(is);
      if (mf != null) {
         return mf.getValue();
      } else {
         return j2735Coder.decodeUPERBsmStream(is);
      }
   }

   private OdeObject getBsmPayload(IEEE1609p2Message message) {
      try {
         SecurityManager.validateGenerationTime(message);
      } catch (SecurityManagerException e) {
         logger.error("Error validating message.", e);
      }

      return decodeBsm(message.getPayload());
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
