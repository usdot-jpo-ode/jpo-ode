package us.dot.its.jpo.ode.coder;

import java.io.BufferedInputStream;
import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.Ieee1609Dot2Data;
import gov.usdot.cv.security.msg.IEEE1609p2Message;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeBsmMetadata;
import us.dot.its.jpo.ode.model.OdeBsmPayload;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.model.SerialId;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735MessageFrame;
import us.dot.its.jpo.ode.plugin.j2735.oss.Oss1609dot2Coder;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssJ2735Coder;
import us.dot.its.jpo.ode.security.SecurityManager;
import us.dot.its.jpo.ode.security.SecurityManager.SecurityManagerException;
import us.dot.its.jpo.ode.util.DateTimeUtils;

public class BsmDecoderHelper {

   private static final Logger logger = LoggerFactory.getLogger(BsmDecoderHelper.class);

   private static final OssJ2735Coder j2735Coder = new OssJ2735Coder();
   private static final Oss1609dot2Coder ieee1609dotCoder = new Oss1609dot2Coder();

   private BsmDecoderHelper() {
   }

   public static OdeData decode(BufferedInputStream bis, String fileName, SerialId serialId) throws Exception {
      Ieee1609Dot2Data ieee1609dot2Data = ieee1609dotCoder.decodeIeee1609Dot2DataStream(bis);

      OdeObject bsm = null;
      OdeData odeBsmData = null;
      IEEE1609p2Message message = null;
      if (ieee1609dot2Data != null) {
         try {
            message = IEEE1609p2Message.convert(ieee1609dot2Data);

            if (message != null) {
                bsm = BsmDecoderHelper.getBsmPayload(message);
            }
         } catch (Exception e) {
             logger.debug("Message does not have a valid signature. Assuming it is unsigned message...");
             bsm = BsmDecoderHelper.decodeBsm(
                ieee1609dot2Data.getContent().getSignedData().getTbsData().getPayload()
                  .getData().getContent().getUnsecuredData().byteArrayValue());
         }
      } else { // probably raw BSM or MessageFrame
         bsm = BsmDecoderHelper.decodeBsm(bis);
      }

      if (bsm != null) {
         odeBsmData = BsmDecoderHelper.createOdeBsmData((J2735Bsm) bsm, message, fileName, serialId);
      }

      return odeBsmData;
   }

   private static OdeObject decodeBsm(BufferedInputStream is) {
      J2735MessageFrame mf = (J2735MessageFrame) j2735Coder.decodeUPERMessageFrameStream(is);
      if (mf != null) {
         return mf.getValue();
      } else {
         return j2735Coder.decodeUPERBsmStream(is);
      }
   }

   private static OdeObject getBsmPayload(IEEE1609p2Message message) {
      try {
         SecurityManager.validateGenerationTime(message);
      } catch (SecurityManagerException e) {
         logger.error("Error validating message.", e);
      }

      return BsmDecoderHelper.decodeBsm(message.getPayload());
   }

   private static OdeObject decodeBsm(byte[] bytes) {
      J2735MessageFrame mf = (J2735MessageFrame) j2735Coder.decodeUPERMessageFrameBytes(bytes);
      if (mf != null) {

         logger.info("Decoding as message frame...");
         return mf.getValue();
      } else {

         logger.info("Decoding as bsm without message frame...");
         return j2735Coder.decodeUPERBsmBytes(bytes);
      }
   }

   public static OdeData decode(byte[] data, String fileName, SerialId serialId) throws Exception {
      IEEE1609p2Message message = null;

      OdeData odeBsmData = null;
      OdeObject bsm = null;
      try {
         message = SecurityManager.decodeSignedMessage(data);
         bsm = getBsmPayload(message);
      } catch (Exception e) {
         logger.debug("Message does not have a valid signature. Assuming it is unsigned message...");
         bsm = BsmDecoderHelper.decodeBsm(data);
      }

      if (bsm != null) {
         odeBsmData = BsmDecoderHelper.createOdeBsmData((J2735Bsm) bsm, message, fileName, serialId);
      }

      return odeBsmData;
   }

   public static OdeBsmData createOdeBsmData(
       J2735Bsm rawBsm, 
       IEEE1609p2Message message, 
       String fileName,
       SerialId serialId) {
      OdeBsmPayload payload = new OdeBsmPayload(rawBsm);

      OdeBsmMetadata metadata = new OdeBsmMetadata(payload);
      metadata.setSerialId(serialId);

      if (message != null) {
         ZonedDateTime generatedAt = DateTimeUtils.isoDateTime(message.getGenerationTime());
         metadata.setGeneratedAt(generatedAt.toString());

         metadata.setValidSignature(true);
      }

      metadata.getSerialId().addRecordId(1);
      metadata.setLogFileName(fileName);
      return new OdeBsmData(metadata, payload);
   }

}
