package us.dot.its.jpo.ode.coder;

import java.time.ZonedDateTime;
import java.util.Date;

import com.fasterxml.jackson.databind.JsonNode;

import gov.usdot.cv.security.msg.IEEE1609p2Message;
import us.dot.its.jpo.ode.importer.parser.BsmFileParser;
import us.dot.its.jpo.ode.model.OdeAsn1Metadata;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeBsmMetadata;
import us.dot.its.jpo.ode.model.OdeBsmPayload;
import us.dot.its.jpo.ode.model.OdeMsgPayload;
import us.dot.its.jpo.ode.model.SerialId;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.util.DateTimeUtils;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

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

   public static OdeBsmData createOdeBsmData(String consumedData) throws XmlUtilsException {
      JsonNode consumed = XmlUtils.toObjectNode(consumedData);
      
      OdeAsn1Metadata metadata = (OdeAsn1Metadata) XmlUtils.fromXmlS(
         consumed.get("metadata").toString(), OdeAsn1Metadata.class);
      
      return OdeBsmDataCreatorHelper.createOdeBsmData(metadata,
         consumed.get("payload")
         .get("data")
         .get("Ieee1609Dot2Data")
         .get("unsecuredData")
         .get("MessageFrame")
         .get("value"));
   }

   private static OdeBsmData createOdeBsmData(OdeAsn1Metadata metadata, JsonNode bsmNode) {
      OdeBsmPayload payload = new OdeBsmPayload();
      
      bsmNode.get("coreData");
      OdeBsmData odeBsmData = new OdeBsmData(metadata, payload );
      return odeBsmData;
   }
}
