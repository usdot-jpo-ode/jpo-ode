package us.dot.its.jpo.ode.traveler;

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
import us.dot.its.jpo.ode.plugin.RoadSideUnit.RSU;
import us.dot.its.jpo.ode.plugin.SNMP;
import us.dot.its.jpo.ode.plugin.ServiceRequest;
import us.dot.its.jpo.ode.plugin.SituationDataWarehouse.SDW;
import us.dot.its.jpo.ode.plugin.ieee1609dot2.Ieee1609Dot2Content;
import us.dot.its.jpo.ode.plugin.ieee1609dot2.Ieee1609Dot2Data;
import us.dot.its.jpo.ode.plugin.ieee1609dot2.Ieee1609Dot2DataTag;
import us.dot.its.jpo.ode.plugin.j2735.DdsAdvisorySituationData;
import us.dot.its.jpo.ode.plugin.j2735.J2735DSRCmsgID;
import us.dot.its.jpo.ode.plugin.j2735.J2735MessageFrame;
import us.dot.its.jpo.ode.plugin.j2735.builders.GeoRegionBuilder;
import us.dot.its.jpo.ode.plugin.j2735.builders.TravelerMessageFromHumanToAsnConverter;
import us.dot.its.jpo.ode.plugin.j2735.timstorage.MessageFrame;
import us.dot.its.jpo.ode.plugin.j2735.timstorage.TravelerInputData;
import us.dot.its.jpo.ode.rsu.RsuProperties;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.JsonUtils.JsonUtilsException;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

public class TimTransmogrifier {

   private static final String ADVISORY_SITUATION_DATA = "AdvisorySituationData";
   private static final String MESSAGE_FRAME = "MessageFrame";

   public static final String RSUS_STRING = "rsus";
   public static final String REQUEST_STRING = "request";

   public static class TimTransmogrifierException extends Exception {

      private static final long serialVersionUID = -923627369025468080L;

      public TimTransmogrifierException(String message) {
         super(message);
      }

   }

   private TimTransmogrifier() {
      throw new UnsupportedOperationException();
   }

   public static String obfuscateRsuPassword(String message) {
      return message.replaceAll("\"rsuPassword\": *\".*?\"", "\"rsuPassword\":\"*\"");
   }

   public static DdsAdvisorySituationData buildASD(ServiceRequest serviceRequest) throws TimTransmogrifierException {
      Ieee1609Dot2DataTag ieeeDataTag = new Ieee1609Dot2DataTag();
      Ieee1609Dot2Data ieee = new Ieee1609Dot2Data();
      Ieee1609Dot2Content ieeeContent = new Ieee1609Dot2Content();
      J2735MessageFrame j2735Mf = new J2735MessageFrame();
      MessageFrame mf = new MessageFrame();
      mf.setMessageFrame(j2735Mf);
      ieeeContent.setUnsecuredData(mf);
      ieee.setContent(ieeeContent);
      ieeeDataTag.setIeee1609Dot2Data(ieee);

      byte sendToRsu = serviceRequest.getRsus() != null ? DdsAdvisorySituationData.RSU : DdsAdvisorySituationData.NONE;
      byte distroType = (byte) (DdsAdvisorySituationData.IP | sendToRsu);

      SNMP snmp = serviceRequest.getSnmp();
      SDW sdw = serviceRequest.getSdw();
      DdsAdvisorySituationData asd = null;

      if (null != sdw) {
         try {
            // take deliverystart and stop times from SNMP object, if present
            // else take from SDW object
            if (null != snmp) {

               asd = new DdsAdvisorySituationData()
                     .setAsdmDetails(snmp.getDeliverystart(), snmp.getDeliverystop(), distroType, ieeeDataTag)
                     .setServiceRegion(GeoRegionBuilder.ddsGeoRegion(sdw.getServiceRegion()))
                     .setTimeToLive(sdw.getTtl()).setGroupID(sdw.getGroupID()).setRecordID(sdw.getRecordId());
            } else {
               asd = new DdsAdvisorySituationData()
                     .setAsdmDetails(sdw.getDeliverystart(), sdw.getDeliverystop(), distroType, ieeeDataTag)
                     .setServiceRegion(GeoRegionBuilder.ddsGeoRegion(sdw.getServiceRegion()))
                     .setTimeToLive(sdw.getTtl()).setGroupID(sdw.getGroupID()).setRecordID(sdw.getRecordId());
            }

         } catch (Exception e) {
            throw new TimTransmogrifierException("Failed to build AdvisorySituationData: " + e.getMessage());
         }
      }
      return asd;
   }

   public static String convertToXml(DdsAdvisorySituationData asd, ObjectNode encodableTidObj,
         OdeMsgMetadata timMetadata, SerialId serialIdJ2735) throws JsonUtilsException, XmlUtilsException {

      TravelerInputData inOrderTid = (TravelerInputData) JsonUtils.jacksonFromJson(encodableTidObj.toString(),
            TravelerInputData.class, true);

      ObjectNode inOrderTidObj = JsonUtils.toObjectNode(inOrderTid.toJson());

      ObjectNode timObj = (ObjectNode) inOrderTidObj.get("tim");

      // Create valid payload from scratch
      OdeMsgPayload payload = null;

      ObjectNode dataBodyObj = JsonUtils.newNode();
      if (null != asd) {
         ObjectNode asdObj = JsonUtils.toObjectNode(asd.toJson());
         ObjectNode mfBodyObj = (ObjectNode) asdObj.findValue(MESSAGE_FRAME);
         mfBodyObj.put("messageId", J2735DSRCmsgID.TravelerInformation.getMsgID());
         mfBodyObj.set("value", (ObjectNode) JsonUtils.newNode().set(TravelerMessageFromHumanToAsnConverter.TRAVELER_INFORMATION, timObj));

         dataBodyObj.set(ADVISORY_SITUATION_DATA, asdObj);

         payload = new OdeAsdPayload(asd);
      } else {
         // Build a MessageFrame
         ObjectNode mfBodyObj = JsonUtils.newNode();
         mfBodyObj.put("messageId", J2735DSRCmsgID.TravelerInformation.getMsgID());
         mfBodyObj.set("value", (ObjectNode) JsonUtils.newNode().set(TravelerMessageFromHumanToAsnConverter.TRAVELER_INFORMATION, timObj));
         dataBodyObj = (ObjectNode) JsonUtils.newNode().set(MESSAGE_FRAME, mfBodyObj);
         payload = new OdeTimPayload();
         payload.setDataType(MESSAGE_FRAME);
      }

      ObjectNode payloadObj = JsonUtils.toObjectNode(payload.toJson());
      payloadObj.set(AppContext.DATA_STRING, dataBodyObj);

      // Create a valid metadata from scratch
      OdeMsgMetadata metadata = new OdeMsgMetadata(payload);
      metadata.setSerialId(serialIdJ2735);
      metadata.setRecordGeneratedBy(timMetadata.getRecordGeneratedBy());
      metadata.setRecordGeneratedAt(timMetadata.getRecordGeneratedAt());
      metadata.setSchemaVersion(timMetadata.getSchemaVersion());
      metadata.setMaxDurationTime(timMetadata.getMaxDurationTime());
      metadata.setOdePacketID(timMetadata.getOdePacketID());
      metadata.setOdeTimStartDateTime(timMetadata.getOdeTimStartDateTime());
      ObjectNode metaObject = JsonUtils.toObjectNode(metadata.toJson());

      ObjectNode request = (ObjectNode) inOrderTidObj.get(REQUEST_STRING);
      metaObject.set(REQUEST_STRING, request);

      if (request.has(RSUS_STRING)) {
         convertRsusArray(request);
      }

      // Add 'encodings' array to metadata
      convertEncodingsArray(asd, metaObject);

      ObjectNode message = JsonUtils.newNode();
      message.set(AppContext.METADATA_STRING, metaObject);
      message.set(AppContext.PAYLOAD_STRING, payloadObj);

      ObjectNode root = JsonUtils.newNode();
      root.set(AppContext.ODE_ASN1_DATA, message);

      // Convert to XML
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

      return fixedXml;
   }

   private static void convertEncodingsArray(DdsAdvisorySituationData asd, ObjectNode metaObject)
         throws JsonUtilsException {
      ArrayNode encodings = buildEncodings(asd);
      ObjectNode enc = XmlUtils.createEmbeddedJsonArrayForXmlConversion(AppContext.ENCODINGS_STRING, encodings);
      metaObject.set(AppContext.ENCODINGS_STRING, enc);
   }

   private static void convertRsusArray(ObjectNode request) {
      // Convert 'rsus' JSON array to XML array
      ObjectNode rsus = XmlUtils.createEmbeddedJsonArrayForXmlConversion(RSUS_STRING,
            (ArrayNode) request.get(RSUS_STRING));
      request.set(RSUS_STRING, rsus);
   }

   private static ArrayNode buildEncodings(DdsAdvisorySituationData asd) throws JsonUtilsException {
      ArrayNode encodings = JsonUtils.newArrayNode();
      encodings.add(buildEncodingNode(MESSAGE_FRAME, MESSAGE_FRAME, EncodingRule.UPER));
      if (null != asd) {
         encodings.add(buildEncodingNode("Ieee1609Dot2Data", "Ieee1609Dot2Data", EncodingRule.COER));
         encodings.add(buildEncodingNode(ADVISORY_SITUATION_DATA, ADVISORY_SITUATION_DATA, EncodingRule.UPER));
      }
      return encodings;
   }

   public static void updateRsuCreds(RSU rsu, RsuProperties rsuProperties) {

      if (rsu.getRsuUsername() == null || rsu.getRsuUsername().isEmpty()) {
         rsu.setRsuUsername(rsuProperties.getUsername());
      }

      if (rsu.getRsuPassword() == null || rsu.getRsuPassword().isEmpty()) {
         rsu.setRsuPassword(rsuProperties.getPassword());
      }
   }

   public static JsonNode buildEncodingNode(String name, String type, EncodingRule rule) throws JsonUtilsException {
      Asn1Encoding mfEnc = new Asn1Encoding(name, type, rule);
      return JsonUtils.toObjectNode(mfEnc.toJson());
   }
}
