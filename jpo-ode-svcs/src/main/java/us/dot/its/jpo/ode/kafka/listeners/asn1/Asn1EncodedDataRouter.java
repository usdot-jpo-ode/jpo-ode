/*=============================================================================
 * Copyright 2018 572682
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package us.dot.its.jpo.ode.kafka.listeners.asn1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.ode.OdeTimJsonTopology;
import us.dot.its.jpo.ode.kafka.topics.Asn1CoderTopics;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.model.Asn1Encoding;
import us.dot.its.jpo.ode.model.Asn1Encoding.EncodingRule;
import us.dot.its.jpo.ode.model.OdeAsdPayload;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeMsgMetadata;
import us.dot.its.jpo.ode.model.OdeMsgPayload;
import us.dot.its.jpo.ode.model.SDXDeposit;
import us.dot.its.jpo.ode.plugin.ServiceRequest;
import us.dot.its.jpo.ode.plugin.SituationDataWarehouse.SDW;
import us.dot.its.jpo.ode.plugin.j2735.DdsAdvisorySituationData;
import us.dot.its.jpo.ode.plugin.j2735.builders.GeoRegionBuilder;
import us.dot.its.jpo.ode.rsu.RsuDepositor;
import us.dot.its.jpo.ode.security.SecurityServicesClient;
import us.dot.its.jpo.ode.security.SecurityServicesProperties;
import us.dot.its.jpo.ode.security.models.SignatureResultModel;
import us.dot.its.jpo.ode.traveler.TimTransmogrifier;
import us.dot.its.jpo.ode.uper.SupportedMessageType;
import us.dot.its.jpo.ode.util.CodecUtils;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

/**
 * The Asn1EncodedDataRouter is responsible for routing encoded TIM messages
 * that are consumed from the Kafka topic.Asn1EncoderOutput topic and decide
 * whether to route to the SDX or an RSU.
 **/
@Component
@Slf4j
public class Asn1EncodedDataRouter {

  private static final String BYTES = "bytes";
  private static final String MESSAGE_FRAME = "MessageFrame";
  private static final String ADVISORY_SITUATION_DATA_STRING = "AdvisorySituationData";
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final XmlMapper xmlMapper;

  /**
   * Exception for Asn1EncodedDataRouter specific failures.
   */
  public static class Asn1EncodedDataRouterException extends Exception {
    public Asn1EncodedDataRouterException(String string) {
      super(string);
    }
  }

  private final Asn1CoderTopics asn1CoderTopics;
  private final JsonTopics jsonTopics;
  private final String sdxDepositTopic;
  private final SecurityServicesClient securityServicesClient;
  private final ObjectMapper mapper;

  private final OdeTimJsonTopology odeTimJsonTopology;
  private final RsuDepositor rsuDepositor;
  private final boolean dataSigningEnabledSDW;
  private final boolean dataSigningEnabledRSU;

  /**
   * Instantiates the Asn1EncodedDataRouter to actively consume from Kafka and
   * route the encoded TIM messages to the SDX and RSUs.
   *
   * @param asn1CoderTopics            The specified ASN1 Coder topics
   * @param jsonTopics                 The specified JSON topics to write to
   * @param securityServicesProperties The security services properties to use
   * @param mapper                     The ObjectMapper used for
   *                                   serialization/deserialization
   **/
  public Asn1EncodedDataRouter(Asn1CoderTopics asn1CoderTopics,
      JsonTopics jsonTopics,
      SecurityServicesProperties securityServicesProperties,
      OdeTimJsonTopology odeTimJsonTopology,
      RsuDepositor rsuDepositor,
      SecurityServicesClient securityServicesClient,
      KafkaTemplate<String, String> kafkaTemplate,
      @Value("${ode.kafka.topics.sdx-depositor.input}") String sdxDepositTopic,
      ObjectMapper mapper,
      XmlMapper xmlMapper) {
    super();

    this.asn1CoderTopics = asn1CoderTopics;
    this.jsonTopics = jsonTopics;
    this.sdxDepositTopic = sdxDepositTopic;
    this.securityServicesClient = securityServicesClient;

    this.kafkaTemplate = kafkaTemplate;

    this.rsuDepositor = rsuDepositor;
    this.dataSigningEnabledSDW = securityServicesProperties.getIsSdwSigningEnabled();
    this.dataSigningEnabledRSU = securityServicesProperties.getIsRsuSigningEnabled();

    this.odeTimJsonTopology = odeTimJsonTopology;
    this.mapper = mapper;
    this.xmlMapper = xmlMapper;
  }

  /**
   * Listens for messages from the specified Kafka topic and processes them.
   *
   * @param consumerRecord The Kafka consumer record containing the key and value
   *                       of the consumed message.
   */
  @KafkaListener(id = "Asn1EncodedDataRouter", topics = "${ode.kafka.topics.asn1.encoder-output}")
  public void listen(ConsumerRecord<String, String> consumerRecord)
      throws XmlUtilsException, JsonProcessingException, Asn1EncodedDataRouterException {
    JSONObject consumedObj = XmlUtils.toJSONObject(consumerRecord.value())
        .getJSONObject(OdeAsn1Data.class.getSimpleName());

    JSONObject metadata = consumedObj.getJSONObject(OdeMsgMetadata.METADATA_STRING);

    if (!metadata.has(TimTransmogrifier.REQUEST_STRING)) {
      throw new Asn1EncodedDataRouterException(String.format(
          "Invalid or missing '%s' object in the encoder response. Unable to process record with offset '%s'",
          TimTransmogrifier.REQUEST_STRING, consumerRecord.offset()));
    }

    JSONObject payloadData = consumedObj.getJSONObject(OdeMsgPayload.PAYLOAD_STRING)
        .getJSONObject(OdeMsgPayload.DATA_STRING);
    ServiceRequest request = getServiceRequest(metadata);
    log.debug("Mapped to object ServiceRequest: {}", request);

    if (payloadData.has("code") && payloadData.has("message")) {
      // The ASN.1 encoding has failed. We cannot proceed
      var code = payloadData.get("code");
      var message = payloadData.get("message");

      log.error("ASN.1 encoding failed with code {} and message {}.", code, message);
      throw new Asn1EncodedDataRouterException(
          "ASN.1 encoding failed for offset %d with code %s and message %s."
              .formatted(consumerRecord.offset(), code, message));
    }
    if (!payloadData.has(ADVISORY_SITUATION_DATA_STRING)) {
      processUnsignedMessage(request, metadata, payloadData);
    } else if (request.getSdw() != null) {
      processDoubleEncodedMessage(request, payloadData);
    } else {
      log.warn(
          "Received encoded AdvisorySituationData message without SDW data. This should never happen.");
    }
  }

  private ServiceRequest getServiceRequest(JSONObject metadataJson) throws JsonProcessingException {
    if (metadataJson.has(TimTransmogrifier.REQUEST_STRING)) {
      JSONObject request = metadataJson.getJSONObject(TimTransmogrifier.REQUEST_STRING);
      processRsusIfPresent(request);
    }
    String serviceRequestJson = metadataJson.getJSONObject(TimTransmogrifier.REQUEST_STRING).toString();
    log.debug("ServiceRequest: {}", serviceRequestJson);
    return mapper.readValue(serviceRequestJson, ServiceRequest.class);
  }

  /**
   * When receiving the 'rsus' in xml, since there is only one 'rsu' and there is
   * no construct for array in xml, the rsus does not translate to an array of 1
   * element. This method, resolves this issue. Note: the code modifies the
   * request object in place.
   */
  private void processRsusIfPresent(JSONObject request) {
    final String RSUS_KEY = TimTransmogrifier.RSUS_STRING;
    if (!request.has(RSUS_KEY)) {
      return;
    }

    Object rsus = request.get(RSUS_KEY);
    // Workaround for XML-to-JSON structure issue
    if (rsus instanceof JSONObject rsusJson && rsusJson.has(RSUS_KEY)) {
      Object nestedRsus = rsusJson.get(RSUS_KEY);
      JSONArray normalizedRsus = normalizeRsus(nestedRsus);
      if (normalizedRsus != null) {
        request.put(RSUS_KEY, normalizedRsus);
      } else {
        request.remove(RSUS_KEY);
        log.debug("No RSUs exist in the request: {}", request);
      }
    }
  }

  private JSONArray normalizeRsus(Object rsus) {
    JSONArray normalizedArray = new JSONArray();
    if (rsus instanceof JSONArray rsusArray) {
      // Multiple RSUs case
      log.debug("Multiple RSUs exist in the request");
      for (int i = 0; i < rsusArray.length(); i++) {
        normalizedArray.put(rsusArray.get(i));
      }
      return normalizedArray;
    } else if (rsus instanceof JSONObject) {
      // Single RSU case
      log.debug("Single RSU exists in the request");
      normalizedArray.put(rsus);
      return normalizedArray;
    }

    // No RSU found
    return null;
  }

  // If SDW in metadata and ASD in body (double encoding complete) -> send to SDX
  private void processDoubleEncodedMessage(ServiceRequest request, JSONObject dataObj)
      throws JsonProcessingException {
    depositToSdx(request, dataObj.getJSONObject(ADVISORY_SITUATION_DATA_STRING).getString(BYTES));
  }

  private void processUnsignedMessage(ServiceRequest request, JSONObject metadataJson,
      JSONObject payloadJson) {
    log.info("Processing unsigned message.");
    JSONObject messageFrameJson = payloadJson.getJSONObject(MESSAGE_FRAME);
    var hexEncodedTimBytes = messageFrameJson.getString(BYTES);
    String bytesToSend;
    if ((dataSigningEnabledRSU || dataSigningEnabledSDW)
        && (request.getSdw() != null || request.getRsus() != null)) {
      log.debug("Signing encoded TIM message...");
      String base64EncodedTim = CodecUtils.toBase64(CodecUtils.fromHex(hexEncodedTimBytes));

      // get max duration time and convert from minutes to milliseconds
      // (unsigned integer valid 0 to 2^32-1 in units of milliseconds) from metadata
      int maxDurationTime = Integer.parseInt(metadataJson.get("maxDurationTime").toString()) * 60 * 1000;
      var signedResponse = securityServicesClient.signMessage(base64EncodedTim, maxDurationTime);
      depositToTimCertExpirationTopic(metadataJson, signedResponse, maxDurationTime);
      bytesToSend = signedResponse.getHexEncodedMessageSigned();
    } else {
      log.debug(
          "Signing not enabled or no SDW or RSU data detected. Sending encoded TIM message without signing...");
      bytesToSend = hexEncodedTimBytes;
    }

    log.debug("Encoded message - phase 1: {}", bytesToSend);
    var encodedTimWithoutHeaders = stripHeader(bytesToSend);

    sendToRsus(request, encodedTimWithoutHeaders);
    depositToFilteredTopic(metadataJson, encodedTimWithoutHeaders);
    if (dataSigningEnabledSDW) {
      publishForSecondEncoding(request, bytesToSend);
    } else {
      publishForSecondEncoding(request, encodedTimWithoutHeaders);
    }
  }

  private void depositToTimCertExpirationTopic(JSONObject metadataJson,
      SignatureResultModel signedResponse, int maxDurationTime) {
    String packetId = metadataJson.getString("odePacketID");
    String timStartDateTime = metadataJson.getString("odeTimStartDateTime");
    JSONObject timWithExpiration = new JSONObject();
    timWithExpiration.put("packetID", packetId);
    timWithExpiration.put("startDateTime", timStartDateTime);

    var dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    setExpiryDate(signedResponse, timWithExpiration, dateFormat);
    setRequiredExpiryDate(dateFormat, timStartDateTime, maxDurationTime, timWithExpiration);

    kafkaTemplate.send(jsonTopics.getTimCertExpiration(), timWithExpiration.toString());
  }

  private void depositToSdx(ServiceRequest request, String asdBytes)
      throws JsonProcessingException {
    SDXDeposit sdxDeposit = new SDXDeposit(request.getSdw().getEstimatedRemovalDate(), asdBytes);
    kafkaTemplate.send(this.sdxDepositTopic, mapper.writeValueAsString(sdxDeposit));
  }

  /**
   * Constructs an XML representation of an Advisory Situation Data (ASD) message
   * containing a Traveler Information Message (TIM). Processes the provided
   * service request and signed message to create and structure the ASD before
   * converting it to an XML string output.
   *
   * @param request   the service request object containing meta information,
   *                  service region, delivery time, and other necessary data for
   *                  ASD creation.
   * @param signedMsg the signed Traveler Information Message (TIM) to be included
   *                  in the ASD.
   *
   * @return a String containing the fully crafted ASD message in XML format.
   *         Returns null if the message could not be constructed due to
   *         exceptions.
   */
  private String packageSignedTimIntoAsd(ServiceRequest request, String signedMsg)
      throws JsonProcessingException, ParseException {
    SDW sdw = request.getSdw();
    DdsAdvisorySituationData asd;

    byte sendToRsu = request.getRsus() != null ? DdsAdvisorySituationData.RSU : DdsAdvisorySituationData.NONE;
    byte distroType = (byte) (DdsAdvisorySituationData.IP | sendToRsu);

    asd = new DdsAdvisorySituationData()
        .setServiceRegion(GeoRegionBuilder.ddsGeoRegion(sdw.getServiceRegion()))
        .setTimeToLive(sdw.getTtl()).setGroupID(sdw.getGroupID()).setRecordID(sdw.getRecordId())
        .setAsdmDetails(sdw.getDeliverystart(), sdw.getDeliverystop(), distroType, null);

    var asdJson = (ObjectNode) mapper.readTree(asd.toJson());

    var admDetailsObj = (ObjectNode) asdJson.findValue("asdmDetails");
    admDetailsObj.remove("advisoryMessage");
    admDetailsObj.put("advisoryMessage", signedMsg);

    asdJson.set("asdmDetails", admDetailsObj);

    ObjectNode advisorySituationDataNode = mapper.createObjectNode();
    advisorySituationDataNode.set(ADVISORY_SITUATION_DATA_STRING, asdJson);

    OdeMsgPayload payload = new OdeAsdPayload(asd);

    var payloadNode = (ObjectNode) mapper.readTree(payload.toJson());
    payloadNode.set(OdeMsgPayload.DATA_STRING, advisorySituationDataNode);

    OdeMsgMetadata metadata = new OdeMsgMetadata(payload);
    var metadataNode = (ObjectNode) mapper.readTree(metadata.toJson());

    metadataNode.set("request", mapper.readTree(request.toJson()));

    ArrayNode encodings = buildEncodings();
    var embeddedEncodings = xmlMapper.createObjectNode();
    embeddedEncodings.set(OdeMsgMetadata.ENCODINGS_STRING, encodings);

    metadataNode.set(OdeMsgMetadata.ENCODINGS_STRING, embeddedEncodings);

    ObjectNode message = mapper.createObjectNode();
    message.set(OdeMsgMetadata.METADATA_STRING, metadataNode);
    message.set(OdeMsgPayload.PAYLOAD_STRING, payloadNode);

    ObjectNode root = mapper.createObjectNode();
    root.set(OdeAsn1Data.ODE_ASN1_DATA, message);

    var outputXml = xmlMapper.writeValueAsString(root).replace("<ObjectNode>", "").replace("</ObjectNode>", "");
    log.debug("Fully crafted ASD to be encoded: {}", outputXml);
    return outputXml;
  }

  private ArrayNode buildEncodings() throws JsonProcessingException {
    ArrayNode encodings = mapper.createArrayNode();
    var encoding = new Asn1Encoding(ADVISORY_SITUATION_DATA_STRING, ADVISORY_SITUATION_DATA_STRING,
        EncodingRule.UPER);
    encodings.add(mapper.readTree(mapper.writeValueAsString(encoding)));
    return encodings;
  }

  private void sendToRsus(ServiceRequest request, String encodedMsg) {
    if (null == request.getSnmp() || null == request.getRsus()) {
      log.debug("No RSUs or SNMP provided. Not sending to RSUs.");
      return;
    }
    log.info("Sending message to RSUs...");
    rsuDepositor.deposit(request, encodedMsg);
  }

  private void setRequiredExpiryDate(DateTimeFormatter dateFormat, String timStartDateTime,
      int maxDurationTime, JSONObject timWithExpiration) {
    try {
      var timStartLocalDate = LocalDateTime.ofInstant(Instant.parse(timStartDateTime), ZoneId.of("UTC"));
      var expiryDate = timStartLocalDate.plus(maxDurationTime, ChronoUnit.MILLIS);
      timWithExpiration.put("requiredExpirationDate", expiryDate.format(dateFormat));
    } catch (Exception e) {
      log.error("Unable to parse requiredExpirationDate. Setting requiredExpirationDate to 'null'",
          e);
      timWithExpiration.put("requiredExpirationDate", "null");
    }
  }

  private void setExpiryDate(SignatureResultModel signedResponse, JSONObject timWithExpiration,
      DateTimeFormatter dateFormat) {
    try {
      var messageExpiryMillis = signedResponse.getMessageExpiry() * 1000;
      var expiryDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(messageExpiryMillis), ZoneId.of("UTC"));
      timWithExpiration.put("expirationDate", expiryDate.format(dateFormat));
    } catch (Exception e) {
      log.error(
          "Unable to get expiration date from signed messages response. Setting expirationData to 'null'",
          e);
      timWithExpiration.put("expirationDate", "null");
    }
  }

  /**
   * Strips header from unsigned message (all bytes before the TIM start flag hex
   * value).
   */
  private String stripHeader(String encodedUnsignedTim) {
    int index = encodedUnsignedTim.indexOf(SupportedMessageType.TIM.getStartFlag().toUpperCase());
    if (index == -1) {
      log.warn("No {} hex value found in encoded message", SupportedMessageType.TIM.getStartFlag());
      return encodedUnsignedTim;
    }
    // strip everything before the start flag
    return encodedUnsignedTim.substring(index);
  }

  private void depositToFilteredTopic(JSONObject metadataObj, String hexEncodedTim) {
    String generatedBy = metadataObj.getString("recordGeneratedBy");
    String streamId = metadataObj.getJSONObject("serialId").getString("streamId");
    if (!generatedBy.equalsIgnoreCase("TMC")) {
      log.debug("Not a TMC-generated TIM. Skipping deposit to TMC-filtered topic.");
      return;
    }

    String timString = odeTimJsonTopology.query(streamId);
    if (timString != null) {
      // Set ASN1 data in TIM metadata
      JSONObject timJSON = new JSONObject(timString);
      JSONObject metadataJSON = timJSON.getJSONObject("metadata");
      metadataJSON.put("asn1", hexEncodedTim);
      timJSON.put("metadata", metadataJSON);

      // Send the message w/ asn1 data to the TMC-filtered topic
      kafkaTemplate.send(jsonTopics.getTimTmcFiltered(), timJSON.toString());
    } else {
      log.debug("TIM not found in k-table. Skipping deposit to TMC-filtered topic.");
    }
  }

  private void publishForSecondEncoding(ServiceRequest request, String encodedTimWithoutHeaders) {
    if (request.getSdw() == null) {
      log.debug("SDW not present. No second encoding required.");
      return;
    }

    try {
      log.debug("Publishing message for round 2 encoding");
      String asdPackagedTim = packageSignedTimIntoAsd(request, encodedTimWithoutHeaders);
      kafkaTemplate.send(asn1CoderTopics.getEncoderInput(), asdPackagedTim);
    } catch (Exception e) {
      log.error("Error packaging ASD for round 2 encoding", e);
    }
  }
}
