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
import us.dot.its.jpo.ode.model.TravelerInputData;
import us.dot.its.jpo.ode.plugin.SituationDataWarehouse.SDW;
import us.dot.its.jpo.ode.plugin.builders.timstorage.Tim;
import us.dot.its.jpo.ode.plugin.j2735.DdsAdvisorySituationData;
import us.dot.its.jpo.ode.plugin.j2735.J2735DSRCmsgID;
import us.dot.its.jpo.ode.plugin.j2735.builders.GeoRegionBuilder;
import us.dot.its.jpo.ode.plugin.j2735.builders.TravelerMessageFromHumanToAsnConverter;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.XmlUtils;

public class TimControllerHelper {

   private TimControllerHelper() {
      throw new UnsupportedOperationException();
   }

   public static String convertToXml(TravelerInputData travelerinputData, ObjectNode encodableTidObj)
         throws Exception {
      Tim inOrderTid = (Tim) JsonUtils.jacksonFromJson(encodableTidObj.toString(), Tim.class);

      ObjectNode inOrderTidObj = JsonUtils.toObjectNode(inOrderTid.toJson());

      JsonNode timObj = inOrderTidObj.remove("tim");
      ObjectNode requestObj = inOrderTidObj; // with 'tim' element removed,
                                             // encodableTid becomes the
                                             // 'request' element

      // Create a MessageFrame
      ObjectNode mfBodyObj = JsonUtils.newNode();
      mfBodyObj.put("messageId", J2735DSRCmsgID.TravelerInformation.getMsgID());
      mfBodyObj.set("value", (ObjectNode) JsonUtils.newNode().set("TravelerInformation", timObj));

      // Create valid payload from scratch
      OdeMsgPayload payload = null;

      ObjectNode dataBodyObj = JsonUtils.newNode();
      SDW sdw = travelerinputData.getSdw();
      if (null != sdw) {
         DdsAdvisorySituationData asd = new DdsAdvisorySituationData(travelerinputData.getSnmp().getDeliverystart(),
               travelerinputData.getSnmp().getDeliverystop(), null,
               GeoRegionBuilder.ddsGeoRegion(sdw.getServiceRegion()), sdw.getTtl());
         ObjectNode asdBodyObj = JsonUtils.toObjectNode(asd.toJson());
         ObjectNode asdmDetails = (ObjectNode) asdBodyObj.get("asdmDetails");
         // Remove 'advisoryMessageBytes' and add 'advisoryMessage'
         asdmDetails.remove("advisoryMessageBytes");
         asdmDetails.set("advisoryMessage", JsonUtils.newNode().set("MessageFrame", mfBodyObj));

         dataBodyObj = (ObjectNode) JsonUtils.newNode().set("AdvisorySituationData", asdBodyObj);

         // Create valid payload from scratch
         payload = new OdeAsdPayload();

      } else {
         dataBodyObj = (ObjectNode) JsonUtils.newNode().set("MessageFrame", mfBodyObj);
         payload = new OdeTimPayload();
         payload.setDataType("MessageFrame");
      }

      ObjectNode payloadObj = JsonUtils.toObjectNode(payload.toJson());
      payloadObj.set(AppContext.DATA_STRING, dataBodyObj);

      // Create a valid metadata from scratch
      OdeMsgMetadata metadata = new OdeMsgMetadata(payload);
      ObjectNode metaObject = JsonUtils.toObjectNode(metadata.toJson());
      metaObject.set("request", requestObj);

      // Create encoding instructions
      ArrayNode encodings = JsonUtils.newArrayNode();
      Asn1Encoding mfEnc = new Asn1Encoding("MessageFrame", "MessageFrame", EncodingRule.UPER);
      encodings.add(JsonUtils.newNode().set("encodings", JsonUtils.toObjectNode(mfEnc.toJson())));
      if (null != sdw) {
         Asn1Encoding asdEnc = new Asn1Encoding("AdvisorySituationData", "AdvisorySituationData", EncodingRule.UPER);
         encodings.add(JsonUtils.newNode().set("encodings", JsonUtils.toObjectNode(asdEnc.toJson())));
      }
      ObjectNode encodingWrap = (ObjectNode) JsonUtils.newNode().set("wrap", encodings);
      metaObject.set("encodings_palceholder", null);

      ObjectNode message = JsonUtils.newNode();
      message.set(AppContext.METADATA_STRING, metaObject);
      message.set(AppContext.PAYLOAD_STRING, payloadObj);

      ObjectNode root = JsonUtils.newNode();
      root.set("OdeAsn1Data", message);

      // Convert to XML
      String outputXml = XmlUtils.toXmlS(root);
      String encStr = XmlUtils.toXmlS(encodingWrap).replace("</wrap><wrap>", "").replace("<wrap>", "")
            .replace("</wrap>", "").replace("<ObjectNode>", "<encodings>").replace("</ObjectNode>", "</encodings>");
      outputXml = outputXml.replace("<encodings_palceholder/>", encStr);

      // Fix tagnames by String replacements
      String fixedXml = outputXml.replaceAll("tcontent>", "content>");// workaround
                                                                      // for the
                                                                      // "content"
                                                                      // reserved
                                                                      // name
      fixedXml = fixedXml.replaceAll("llong>", "long>"); // workaround for
                                                         // "long" being a type
                                                         // in java
      fixedXml = fixedXml.replaceAll("node_LL3>", "node-LL3>");
      fixedXml = fixedXml.replaceAll("node_LatLon>", "node-LatLon>");
      fixedXml = fixedXml.replaceAll("nodeLL>", "NodeLL>");
      fixedXml = fixedXml.replaceAll("nodeXY>", "NodeXY>");
      fixedXml = fixedXml.replaceAll("sequence>", "SEQUENCE>");
      fixedXml = fixedXml.replaceAll("geographicalPath>", "GeographicalPath>");

      // workarounds for self-closing tags
      fixedXml = fixedXml.replaceAll(TravelerMessageFromHumanToAsnConverter.EMPTY_FIELD_FLAG, "");
      fixedXml = fixedXml.replaceAll(TravelerMessageFromHumanToAsnConverter.BOOLEAN_OBJECT_TRUE, "<true />");
      fixedXml = fixedXml.replaceAll(TravelerMessageFromHumanToAsnConverter.BOOLEAN_OBJECT_FALSE, "<false />");

      // remove the surrounding <ObjectNode></ObjectNode>
      fixedXml = fixedXml.replace("<ObjectNode>", "");
      fixedXml = fixedXml.replace("</ObjectNode>", "");

      return fixedXml;
   }
}
