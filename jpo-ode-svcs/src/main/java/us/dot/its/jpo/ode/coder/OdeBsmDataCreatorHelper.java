package us.dot.its.jpo.ode.coder;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.usdot.cv.security.msg.IEEE1609p2Message;
import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.importer.parser.BsmLogFileParser;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeBsmMetadata;
import us.dot.its.jpo.ode.model.OdeBsmPayload;
import us.dot.its.jpo.ode.model.OdeLogMetadata;
import us.dot.its.jpo.ode.model.OdeLogMetadata.SecurityResultCode;
import us.dot.its.jpo.ode.model.OdeMsgMetadata.GeneratedBy;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.builders.BsmBuilder;
import us.dot.its.jpo.ode.plugin.j2735.builders.BsmPart2ContentBuilder.BsmPart2ContentBuilderException;
import us.dot.its.jpo.ode.util.DateTimeUtils;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

public class OdeBsmDataCreatorHelper {

   private OdeBsmDataCreatorHelper() {
   }

   public static OdeBsmData createOdeBsmData(
      J2735Bsm rawBsm, 
      IEEE1609p2Message message, 
      BsmLogFileParser bsmFileParser) {

      OdeBsmPayload payload = new OdeBsmPayload(rawBsm);

      OdeLogMetadata metadata = new OdeBsmMetadata(payload);
      OdeLogMetadataCreatorHelper.updateLogMetadata(metadata, bsmFileParser);
      
      // If we have a valid message, override relevant data from the message
      if (message != null) {
         ZonedDateTime generatedAt;
         Date ieeeGenTime = message.getGenerationTime();

         if (ieeeGenTime != null) {
            generatedAt = DateTimeUtils.isoDateTime(ieeeGenTime);
         } else if (bsmFileParser != null) {
            generatedAt = bsmFileParser.getTimeParser().getGeneratedAt();
         } else {
            generatedAt = DateTimeUtils.nowZDT();
         }
         metadata.setRecordGeneratedAt(DateTimeUtils.isoDateTime(generatedAt));
         metadata.setRecordGeneratedBy(GeneratedBy.OBU);
         metadata.setSecurityResultCode(SecurityResultCode.success);
      }

      return new OdeBsmData(metadata, payload);
   }

   public static OdeBsmData createOdeBsmData(
      J2735Bsm rawBsm, String filename) {
      BsmLogFileParser bsmFileParser = new BsmLogFileParser();
      bsmFileParser.setFilename(filename).getTimeParser().setUtcTimeInSec(0).getSecResCodeParser().setSecurityResultCode(SecurityResultCode.unknown);
;
      return createOdeBsmData(rawBsm, null, bsmFileParser);
   }

   public static OdeBsmData createOdeBsmData(String consumedData) 
         throws JsonProcessingException, IOException, XmlUtilsException, BsmPart2ContentBuilderException  {
      ObjectNode consumed = XmlUtils.toObjectNode(consumedData);

      JsonNode metadataNode = consumed.findValue(AppContext.METADATA_STRING);
      if (metadataNode instanceof ObjectNode) {
         ObjectNode object = (ObjectNode) metadataNode;
         object.remove(AppContext.ENCODINGS_STRING);
      }
      
      OdeBsmMetadata metadata = (OdeBsmMetadata) JsonUtils.fromJson(
         metadataNode.toString(), OdeBsmMetadata.class);

      /*
       *  ODE-755 and ODE-765 Starting with schemaVersion=5 receivedMessageDetails 
       *  will be present in BSM metadata. None should be present in prior versions.
       */
      if (metadata.getSchemaVersion() <= 4) {
         metadata.setReceivedMessageDetails(null);
      }
      
      OdeBsmPayload payload = new OdeBsmPayload(
         BsmBuilder.genericBsm(consumed.findValue("BasicSafetyMessage")));
      return new OdeBsmData(metadata, payload );
   }
}
