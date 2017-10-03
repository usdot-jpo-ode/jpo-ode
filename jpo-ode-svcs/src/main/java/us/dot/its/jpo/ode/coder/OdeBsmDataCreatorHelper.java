package us.dot.its.jpo.ode.coder;

import java.time.ZonedDateTime;
import java.util.Date;

import gov.usdot.cv.security.msg.IEEE1609p2Message;
import us.dot.its.jpo.ode.importer.parser.BsmFileParser;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeBsmMetadata;
import us.dot.its.jpo.ode.model.OdeBsmPayload;
import us.dot.its.jpo.ode.model.SerialId;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.util.DateTimeUtils;

public class OdeBsmDataCreatorHelper {

   private OdeBsmDataCreatorHelper() {
   }

   public static OdeBsmData createOdeBsmData(
      J2735Bsm rawBsm, 
      IEEE1609p2Message message, 
      BsmFileParser bsmFileParser) {

      OdeBsmPayload payload = new OdeBsmPayload(rawBsm);

      OdeBsmMetadata metadata = new OdeBsmMetadata(payload);
      OdeLogMetadataCreatorHelper.updateLogMetadata(metadata, bsmFileParser);
      
      // If we have a valid message, override relevant data from the message
      if (message != null) {
         ZonedDateTime generatedAt;
         Date ieeeGenTime = message.getGenerationTime();

         if (ieeeGenTime != null) {
            generatedAt = DateTimeUtils.isoDateTime(ieeeGenTime);
         } else {
            generatedAt = bsmFileParser.getGeneratedAt();
         }
         metadata.setGeneratedAt(generatedAt.toString());
         metadata.setValidSignature(true);
      }

      return new OdeBsmData(metadata, payload);
   }

   public static OdeBsmData createOdeBsmData(
      J2735Bsm rawBsm, String filename, SerialId serialId) {
      BsmFileParser bsmFileParser = new BsmFileParser(serialId.getBundleId());
      bsmFileParser.setFilename(filename).setUtcTimeInSec(0).setValidSignature(false);
      return createOdeBsmData(rawBsm, null, bsmFileParser);
   }
}
