package us.dot.its.jpo.ode.traveler;

import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.model.Asn1Encoding;
import us.dot.its.jpo.ode.model.Asn1Encoding.EncodingRule;
import us.dot.its.jpo.ode.model.OdeAsdPayload;
import us.dot.its.jpo.ode.model.OdeMsgMetadata;
import us.dot.its.jpo.ode.model.OdeMsgPayload;
import us.dot.its.jpo.ode.model.OdeTimPayload;
import us.dot.its.jpo.ode.model.SerialId;
import us.dot.its.jpo.ode.plugin.j2735.DdsAdvisorySituationData;
import us.dot.its.jpo.ode.plugin.j2735.J2735DSRCmsgID;
import us.dot.its.jpo.ode.plugin.j2735.builders.TravelerMessageFromHumanToAsnConverter;
import us.dot.its.jpo.ode.plugin.j2735.timstorage.TravelerInputData;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.JsonUtils.JsonUtilsException;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

@Component
public class TimMessageManipulator {
   

   public static final String RSUS_STRING = "rsus";

   public static final String REQUEST_STRING = "request";
   
   private static final Logger logger = LoggerFactory.getLogger(TimMessageManipulator.class);
   
   public TimMessageManipulator() {
      super();
   }
   
   public String convertToXml(DdsAdvisorySituationData asd, ObjectNode encodableTidObj, OdeMsgMetadata timMetadata, SerialId serialIdJ2735)
         throws JsonUtilsException, XmlUtilsException, ParseException {

      TravelerInputData inOrderTid = (TravelerInputData) JsonUtils.jacksonFromJson(encodableTidObj.toString(),
            TravelerInputData.class);
      logger.debug("In Order TravelerInputData: {}", inOrderTid);
      ObjectNode inOrderTidObj = JsonUtils.toObjectNode(inOrderTid.toJson());

      ObjectNode timObj = (ObjectNode) inOrderTidObj.get("tim");

      // Create valid payload from scratch
      OdeMsgPayload payload = null;

      ObjectNode dataBodyObj = JsonUtils.newNode();
      if (null != asd) {
         logger.debug("Converting request to ASD/Ieee1609Dot2Data/MessageFrame!");
         ObjectNode asdObj = JsonUtils.toObjectNode(asd.toJson());
         ObjectNode mfBodyObj = (ObjectNode) asdObj.findValue("MessageFrame");
         mfBodyObj.put("messageId", J2735DSRCmsgID.TravelerInformation.getMsgID());
         mfBodyObj.set("value", (ObjectNode) JsonUtils.newNode().set("TravelerInformation", timObj));

         dataBodyObj.set("AdvisorySituationData", asdObj);

         payload = new OdeAsdPayload(asd);
      } else {
         logger.debug("Converting request to Ieee1609Dot2Data/MessageFrame!");
         // Build a MessageFrame
         ObjectNode mfBodyObj = (ObjectNode) JsonUtils.newNode();
         mfBodyObj.put("messageId", J2735DSRCmsgID.TravelerInformation.getMsgID());
         mfBodyObj.set("value", (ObjectNode) JsonUtils.newNode().set("TravelerInformation", timObj));
         dataBodyObj = (ObjectNode) JsonUtils.newNode().set("MessageFrame", mfBodyObj);
         payload = new OdeTimPayload();
         payload.setDataType("MessageFrame");
      }

      ObjectNode payloadObj = JsonUtils.toObjectNode(payload.toJson());
      payloadObj.set(AppContext.DATA_STRING, dataBodyObj);

      // Create a valid metadata from scratch
      OdeMsgMetadata metadata = new OdeMsgMetadata(payload);
      metadata.setSerialId(serialIdJ2735);
      metadata.setRecordGeneratedBy(timMetadata.getRecordGeneratedBy());
      metadata.setRecordGeneratedAt(timMetadata.getRecordGeneratedAt());
      ObjectNode metaObject = JsonUtils.toObjectNode(metadata.toJson());

      ObjectNode request = (ObjectNode) inOrderTidObj.get(REQUEST_STRING);
      metaObject.set(REQUEST_STRING, request);

      if (request.has(RSUS_STRING)) {
        convertRsusArray(request, metaObject);
      }

      //Add 'encodings' array to metadata
      convertEncodingsArray(asd, metaObject);

      ObjectNode message = JsonUtils.newNode();
      message.set(AppContext.METADATA_STRING, metaObject);
      message.set(AppContext.PAYLOAD_STRING, payloadObj);

      ObjectNode root = JsonUtils.newNode();
      root.set(AppContext.ODE_ASN1_DATA, message);

      // Convert to XML
      logger.debug("pre-xml: {}", root);
      String outputXml = XmlUtils.toXmlStatic(root);

      // Fix tagnames by String replacements
      String fixedXml = outputXml.replaceAll("tcontent>", "content>");// workaround
                                                                      // for the
                                                                      // "content"
                                                                      // reserved
                                                                      // name
      fixedXml = fixedXml.replaceAll("llong>", "long>"); // workaround for
                                                         // "long" being a type
                                                         // in java
      fixedXml = fixedXml.replaceAll("node_LL", "node-LL");
      fixedXml = fixedXml.replaceAll("node_XY", "node-XY");
      fixedXml = fixedXml.replaceAll("node_LatLon>", "node-LatLon>");
      fixedXml = fixedXml.replaceAll("nodeLL>", "NodeLL>");
      fixedXml = fixedXml.replaceAll("nodeXY>", "NodeXY>");

      // workarounds for self-closing tags
      fixedXml = fixedXml.replaceAll(TravelerMessageFromHumanToAsnConverter.EMPTY_FIELD_FLAG, "");
      fixedXml = fixedXml.replaceAll(TravelerMessageFromHumanToAsnConverter.BOOLEAN_OBJECT_TRUE, "<true />");
      fixedXml = fixedXml.replaceAll(TravelerMessageFromHumanToAsnConverter.BOOLEAN_OBJECT_FALSE, "<false />");

      // remove the surrounding <ObjectNode></ObjectNode>
      fixedXml = fixedXml.replace("<ObjectNode>", "");
      fixedXml = fixedXml.replace("</ObjectNode>", "");

      logger.debug("Fixed XML: {}", fixedXml);
      return fixedXml;
   }
   
   private static void convertEncodingsArray(DdsAdvisorySituationData asd, ObjectNode metaObject)
         throws JsonUtilsException, XmlUtilsException {
      ArrayNode encodings = buildEncodings(asd);
      ObjectNode enc = XmlUtils.createEmbeddedJsonArrayForXmlConversion(AppContext.ENCODINGS_STRING, encodings);
      metaObject.set(AppContext.ENCODINGS_STRING, enc);
   }

    private static void convertRsusArray(ObjectNode request, ObjectNode metaObject) {
      //Convert 'rsus' JSON array to XML array
      ObjectNode rsus = XmlUtils.createEmbeddedJsonArrayForXmlConversion(RSUS_STRING, (ArrayNode) request.get(RSUS_STRING));
      request.set(RSUS_STRING, rsus);
    }
    
    private static ArrayNode buildEncodings(DdsAdvisorySituationData asd) throws JsonUtilsException, XmlUtilsException {
       ArrayNode encodings = JsonUtils.newArrayNode();
       encodings.add(buildEncodingNode("MessageFrame", "MessageFrame", EncodingRule.UPER));
       if (null != asd) {
          encodings.add(buildEncodingNode("Ieee1609Dot2Data", "Ieee1609Dot2Data", EncodingRule.COER));
          encodings.add(buildEncodingNode("AdvisorySituationData", "AdvisorySituationData", EncodingRule.UPER));
       }
       return encodings;
    }
    
    public static JsonNode buildEncodingNode(String name, String type, EncodingRule rule) throws JsonUtilsException {
       Asn1Encoding mfEnc = new Asn1Encoding(name, type, rule);
       return JsonUtils.toObjectNode(mfEnc.toJson());
    }

}
