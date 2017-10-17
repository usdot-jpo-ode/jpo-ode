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

   public OdeBsmDataCreatorHelper() {
   }

   public OdeBsmData createOdeBsmData(J2735Bsm rawBsm, IEEE1609p2Message message, BsmFileParser bsmFileParser,
         SerialId serialId) {

      OdeBsmPayload payload = new OdeBsmPayload(rawBsm);

      OdeBsmMetadata metadata = new OdeBsmMetadata(payload);
      metadata.setSerialId(serialId);

      ZonedDateTime generatedAt;
      if (bsmFileParser != null) {
         metadata.setLogFileName(bsmFileParser.getFilename());
         if (message != null) {
            Date ieeeGenTime = message.getGenerationTime();

            if (ieeeGenTime != null) {
               generatedAt = DateTimeUtils.isoDateTime(ieeeGenTime);
            } else {
               generatedAt = getGeneratedAt(bsmFileParser);
            }
            metadata.setGeneratedAt(generatedAt.toString());
            metadata.setValidSignature(true);
         } else {
            metadata.setGeneratedAt(getGeneratedAt(bsmFileParser).toString());
            metadata.setValidSignature(bsmFileParser.isValidSignature());
         }
      } else {
         /*
          * TODO Temporarily put in place for testing CV PEP. Should be removed
          * after testing is complete.
          */
         metadata.setGeneratedAt(metadata.getReceivedAt());
      }

      metadata.getSerialId().addRecordId(1);
      return new OdeBsmData(metadata, payload);
   }

   private ZonedDateTime getGeneratedAt(BsmFileParser bsmFileParser) {
      return DateTimeUtils.isoDateTime(bsmFileParser.getUtcTimeInSec() * 1000 + bsmFileParser.getmSec());
   }

   public OdeBsmData createOdeBsmData(J2735Bsm rawBsm, String filename, SerialId serialId) {
      BsmFileParser bsmFileParser = (BsmFileParser) new BsmFileParser(serialId.getBundleId()).setFilename(filename).setUtcTimeInSec(0)
            .setValidSignature(false);
      return createOdeBsmData(rawBsm, null, bsmFileParser, serialId);
   }
}
