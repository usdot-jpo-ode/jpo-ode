package us.dot.its.jpo.ode.kafka.listeners.asn1;

import com.fasterxml.jackson.core.JsonProcessingException;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.JSONObject;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonMappingException;

import us.dot.its.jpo.asn.j2735.r2024.MessageFrame.DSRCmsgID;
import us.dot.its.jpo.ode.coder.OdeBsmDataCreatorHelper;
import us.dot.its.jpo.ode.coder.OdeMapDataCreatorHelper;
import us.dot.its.jpo.ode.coder.OdeMessageFrameDataCreatorHelper;
import us.dot.its.jpo.ode.coder.OdePsmDataCreatorHelper;
import us.dot.its.jpo.ode.coder.OdeSpatDataCreatorHelper;
import us.dot.its.jpo.ode.coder.OdeSrmDataCreatorHelper;
import us.dot.its.jpo.ode.coder.OdeSsmDataCreatorHelper;
import us.dot.its.jpo.ode.coder.OdeTimDataCreatorHelper;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.kafka.topics.PojoTopics;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeLogMetadata;
import us.dot.its.jpo.ode.model.OdeLogMetadata.RecordType;
import us.dot.its.jpo.ode.model.OdeMessageFrameData;
import us.dot.its.jpo.ode.model.OdeMsgMetadata;
import us.dot.its.jpo.ode.model.OdeMsgPayload;
import us.dot.its.jpo.ode.model.OdePsmData;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

/**
 * The Asn1DecodedDataRouter class is a component responsible for processing decoded ASN.1 data from
 * Kafka topics. It listens to messages on a specified Kafka topic and handles the incoming data by
 * processing and forwarding it to different topics based on specific criteria.
 *
 * <p>This listener is specifically designed to handle decoded data produced by the asn1_codec.
 * Upon receiving a payload, it transforms the payload and then determines the appropriate Kafka
 * topic to forward the processed data.</p>
 *
 * <p>The class utilizes Spring Kafka's annotation-driven listener configuration,
 * allowing it to automatically consume messages from a configured Kafka topic.</p>
 */
@Slf4j
@Component
public class Asn1DecodedDataRouter {

  private final PojoTopics pojoTopics;
  private final JsonTopics jsonTopics;
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final KafkaTemplate<String, OdeBsmData> bsmDataKafkaTemplate;
  /**
   * Exception for Asn1DecodedDataRouter specific failures.
   */
  public static class Asn1DecodedDataRouterException extends Exception {
    public Asn1DecodedDataRouterException(String string) {
      super(string);
    }
  }

  /**
   * Constructs an instance of Asn1DecodedDataRouter.
   *
   * @param kafkaTemplate the KafkaTemplate used for sending messages to Kafka topics.
   */
  public Asn1DecodedDataRouter(KafkaTemplate<String, String> kafkaTemplate,
                               KafkaTemplate<String, OdeBsmData> bsmDataKafkaTemplate,
                               PojoTopics pojoTopics,
                               JsonTopics jsonTopics) {
    this.kafkaTemplate = kafkaTemplate;
    this.bsmDataKafkaTemplate = bsmDataKafkaTemplate;
    this.pojoTopics = pojoTopics;
    this.jsonTopics = jsonTopics;
  }

  /**
   * Processes the given Kafka message payload by transforming it into ODE data and publishing it to
   * appropriate Kafka topics based on its record type.
   */
  @KafkaListener(
      id = "Asn1DecodedDataRouter",
      topics = "${ode.kafka.topics.asn1.decoder-output}"
  )
  public void listen(ConsumerRecord<String, String> consumerRecord)
      throws XmlUtilsException, JsonProcessingException, Asn1DecodedDataRouterException, JsonMappingException, JsonProcessingException, IOException {
    log.debug("Key: {} payload: {}", consumerRecord.key(), consumerRecord.value());

    JSONObject consumed = XmlUtils.toJSONObject(consumerRecord.value())
        .getJSONObject(OdeAsn1Data.class.getSimpleName());

    JSONObject payloadData = consumed.getJSONObject(OdeMsgPayload.PAYLOAD_STRING).getJSONObject(OdeMsgPayload.DATA_STRING);

    if (payloadData.has("code")) {
      throw new Asn1DecodedDataRouterException(
          String.format("Error processing decoded message with code %s and message %s", payloadData.getString("code"),
              payloadData.has("message") ? payloadData.getString("message") : "NULL")
      );
    }

    int msgId = payloadData.getJSONObject("MessageFrame")
        .getInt("messageId");
    DSRCmsgID messageId = new DSRCmsgID(msgId);
    String messageName = messageId.name().orElse("Unknown");

    var metadataJson = XmlUtils.toJSONObject(consumerRecord.value())
        .getJSONObject(OdeAsn1Data.class.getSimpleName())
        .getJSONObject(OdeMsgMetadata.METADATA_STRING);
    OdeLogMetadata.RecordType recordType = OdeLogMetadata.RecordType
        .valueOf(metadataJson.getString("recordType"));

    String streamId;
    if (Strings.isNullOrEmpty(consumerRecord.key())
        || "null".equalsIgnoreCase(consumerRecord.key())) {
      streamId = metadataJson.getJSONObject("serialId").getString("streamId");
    } else {
      streamId = consumerRecord.key();
    }

    switch (messageName) {
      case "basicSafetyMessage" -> routeBSM(consumerRecord, recordType);
      case "travelerInformation" -> routeTIM(consumerRecord, streamId, recordType);
      case "signalPhaseAndTimingMessage" -> routeSPAT(consumerRecord, recordType);
      case "mapData" -> routeMAP(consumerRecord, recordType);
      case "signalRequestMessage" -> routeSSM(consumerRecord, recordType);
      case "roadSideAlertMessage" -> routeSRM(consumerRecord, recordType);
      case "personalSafetyMessage" -> routePSM(consumerRecord, recordType);
      case "sensorDataSharingMessage" -> routeMessageFrame(consumerRecord, jsonTopics.getSdsm());
      case null, default -> log.warn("Unknown message type: {}", messageName);
    }
  }

  private void routePSM(ConsumerRecord<String, String> consumerRecord, RecordType recordType)
      throws XmlUtils.XmlUtilsException, JsonMappingException, JsonProcessingException, IOException {
    OdePsmData odePsmData = OdePsmDataCreatorHelper.createOdePsmData(consumerRecord.value());
    if (recordType == RecordType.psmTx) {
      kafkaTemplate.send(pojoTopics.getTxPsm(), consumerRecord.key(), odePsmData.toString());
    }
    // Send all PSMs also to OdePsmJson
    kafkaTemplate.send(jsonTopics.getPsm(), consumerRecord.key(), odePsmData.toString());
  }

  private void routeSRM(ConsumerRecord<String, String> consumerRecord, RecordType recordType)
      throws XmlUtils.XmlUtilsException {
    String odeSrmData = OdeSrmDataCreatorHelper.createOdeSrmData(consumerRecord.value()).toString();
    if (recordType == RecordType.srmTx) {
      kafkaTemplate.send(pojoTopics.getTxSrm(), consumerRecord.key(), odeSrmData);
    }
    // Send all SRMs also to OdeSrmJson
    kafkaTemplate.send(jsonTopics.getSrm(), consumerRecord.key(), odeSrmData);
  }

  private void routeSSM(ConsumerRecord<String, String> consumerRecord, RecordType recordType)
      throws XmlUtils.XmlUtilsException {
    String odeSsmData = OdeSsmDataCreatorHelper.createOdeSsmData(consumerRecord.value()).toString();
    if (recordType == RecordType.ssmTx) {
      kafkaTemplate.send(pojoTopics.getSsm(), consumerRecord.key(), odeSsmData);
    }
    // Send all SSMs also to OdeSsmJson
    kafkaTemplate.send(jsonTopics.getSsm(), consumerRecord.key(), odeSsmData);
  }

  private void routeSPAT(ConsumerRecord<String, String> consumerRecord, RecordType recordType)
      throws XmlUtils.XmlUtilsException {
    String odeSpatData =
        OdeSpatDataCreatorHelper.createOdeSpatData(consumerRecord.value()).toString();
    switch (recordType) {
      case dnMsg -> kafkaTemplate.send(
          jsonTopics.getDnMessage(), consumerRecord.key(), odeSpatData);
      case rxMsg -> kafkaTemplate.send(jsonTopics.getRxSpat(), consumerRecord.key(), odeSpatData);
      case spatTx -> kafkaTemplate.send(pojoTopics.getTxSpat(), consumerRecord.key(), odeSpatData);
      default -> log.trace("Consumed SPAT data with record type: {}", recordType);
    }
    // Send all SPATs also to OdeSpatJson
    kafkaTemplate.send(jsonTopics.getSpat(), consumerRecord.key(), odeSpatData);
  }


  private void routeMAP(ConsumerRecord<String, String> consumerRecord, RecordType recordType)
      throws XmlUtilsException {
    String odeMapData = OdeMapDataCreatorHelper.createOdeMapData(consumerRecord.value()).toString();
    if (recordType == RecordType.mapTx) {
      kafkaTemplate.send(pojoTopics.getTxMap(), odeMapData);
    }

    // Send all MAP data to OdeMapJson despite the record type
    kafkaTemplate.send(jsonTopics.getMap(), odeMapData);
  }

  private void routeTIM(ConsumerRecord<String, String> consumerRecord,
                        String streamId,
                        RecordType type) throws XmlUtilsException {
    String odeTimData =
        OdeTimDataCreatorHelper.createOdeTimDataFromDecoded(consumerRecord.value()).toString();
    switch (type) {
      case dnMsg -> kafkaTemplate.send(jsonTopics.getDnMessage(), consumerRecord.key(), odeTimData);
      case rxMsg -> kafkaTemplate.send(jsonTopics.getRxTim(), consumerRecord.key(), odeTimData);
      default -> log.trace("Consumed TIM data with record type: {}", type);
    }
    // Send all TIMs also to OdeTimJson
    kafkaTemplate.send(jsonTopics.getTim(), streamId, odeTimData);
  }

  private void routeBSM(ConsumerRecord<String, String> consumerRecord, RecordType recordType)
      throws XmlUtils.XmlUtilsException {
    // ODE-518/ODE-604 Demultiplex the messages to appropriate topics based on the "recordType"
    OdeBsmData odeBsmData = OdeBsmDataCreatorHelper.createOdeBsmData(consumerRecord.value());
    // NOTE: These three flows in the switch statement are all disabled in all known environments via the disabled-topics configuration settings.
    // We may consider removing this code completely in the future.
    switch (recordType) {
      case bsmLogDuringEvent -> bsmDataKafkaTemplate.send(pojoTopics.getBsmDuringEvent(), consumerRecord.key(),
          odeBsmData);
      case rxMsg -> bsmDataKafkaTemplate.send(pojoTopics.getRxBsm(), consumerRecord.key(), odeBsmData);
      case bsmTx -> bsmDataKafkaTemplate.send(pojoTopics.getTxBsm(), consumerRecord.key(), odeBsmData);
      default -> log.trace("Consumed BSM data with record type: {}", recordType);
    }
    // Send all BSMs also to OdeBsmPojo
    bsmDataKafkaTemplate.send(pojoTopics.getBsm(), consumerRecord.key(), odeBsmData);
  }

  private void routeMessageFrame(ConsumerRecord<String, String> consumerRecord, String ... topics)
  throws XmlUtils.XmlUtilsException, IOException {
    log.debug("routeMessageFrame to topics: {}", String.join(", ", topics));
    OdeMessageFrameData odeMessageFrameData =
        OdeMessageFrameDataCreatorHelper.createOdeMessageFrameData(consumerRecord.value());
    String dataStr = JsonUtils.toJson(odeMessageFrameData, false);
    for (String topic : topics) {
      kafkaTemplate.send(topic, consumerRecord.key(), dataStr);
    }
  }
}
