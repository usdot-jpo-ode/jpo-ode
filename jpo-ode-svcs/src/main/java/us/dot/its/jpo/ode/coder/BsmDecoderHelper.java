package us.dot.its.jpo.ode.coder;

import java.io.BufferedInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.Ieee1609Dot2Data;
import gov.usdot.cv.security.msg.IEEE1609p2Message;
import us.dot.its.jpo.ode.importer.BsmFileParser;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.model.SerialId;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.oss.Oss1609dot2Coder;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssJ2735Coder;

public class BsmDecoderHelper {

   private static final Logger logger = LoggerFactory.getLogger(BsmDecoderHelper.class);

   private final OssJ2735Coder j2735Coder;
   private final Oss1609dot2Coder ieee1609dotCoder;
   private final Iee1609ContentValidator iee1609ContentValidatorIn;
   private final RawBsmMfSorter rawBsmMFSorterIn;
   private final OdeBsmDataCreaterHelper odeBsmDataCreaterHelperIn;
   private final BsmDecoderPayloadHelper bsmDecoderPayloadHelperIn;

   public BsmDecoderHelper() {
      this.j2735Coder = new OssJ2735Coder();
      this.ieee1609dotCoder = new Oss1609dot2Coder();
      this.iee1609ContentValidatorIn = new Iee1609ContentValidator();
      this.rawBsmMFSorterIn = new RawBsmMfSorter(j2735Coder);
      this.odeBsmDataCreaterHelperIn = new OdeBsmDataCreaterHelper();
      this.bsmDecoderPayloadHelperIn = new BsmDecoderPayloadHelper(rawBsmMFSorterIn);
   }

   public OdeData decode(BsmFileParser bsmFileParser, SerialId serialId) throws Exception {

      Ieee1609Dot2Data ieee1609dot2Data = 
              ieee1609dotCoder.decodeIeee1609Dot2DataStream(bsmFileParser.getPayload());
      OdeObject bsm = null;
      OdeData odeBsmData = null;
      IEEE1609p2Message message = null;

      if (ieee1609dot2Data != null) {
         logger.debug("Attempting to decode as Ieee1609Dot2Data.");
         try {
            message = IEEE1609p2Message.convert(ieee1609dot2Data);
            if (message != null) {
               bsm = bsmDecoderPayloadHelperIn.getBsmPayload(message);
            }
         } catch (Exception e) {
            logger.debug("Message does not have a valid signature. Assuming it is unsigned message...");
            if (iee1609ContentValidatorIn.contentHadUnsecureData(ieee1609dot2Data.getContent())) {
               bsm = rawBsmMFSorterIn.decodeBsm(ieee1609dot2Data.getContent().getSignedData().getTbsData().getPayload()
                     .getData().getContent().getUnsecuredData().byteArrayValue());
            }
         }
      } else {
         // probably raw BSM or MessageFrame
         bsm = rawBsmMFSorterIn.decodeBsm(bsmFileParser.getPayload());
      }
      if (bsm != null) {
         logger.debug("Decoded BSM successfully, creating OdeBsmData object.");
         odeBsmData = odeBsmDataCreaterHelperIn.createOdeBsmData((J2735Bsm) bsm, message, bsmFileParser.getFilename(), serialId);
      } else {
         logger.debug("Failed to decode BSM.");
      }
      return odeBsmData;
   }

}
