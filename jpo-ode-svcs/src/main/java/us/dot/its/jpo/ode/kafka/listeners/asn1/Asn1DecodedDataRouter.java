package us.dot.its.jpo.ode.kafka.listeners.asn1;

import com.fasterxml.jackson.core.JsonProcessingException;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.JSONObject;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.ode.coder.OdeBsmDataCreatorHelper;
import us.dot.its.jpo.ode.coder.OdeMapDataCreatorHelper;
import us.dot.its.jpo.ode.coder.OdePsmDataCreatorHelper;
import us.dot.its.jpo.ode.coder.OdeSpatDataCreatorHelper;
import us.dot.its.jpo.ode.coder.OdeSrmDataCreatorHelper;
import us.dot.its.jpo.ode.coder.OdeSsmDataCreatorHelper;
import us.dot.its.jpo.ode.coder.OdeTimDataCreatorHelper;
import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.kafka.topics.PojoTopics;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeLogMetadata;
import us.dot.its.jpo.ode.model.OdeLogMetadata.RecordType;
import us.dot.its.jpo.ode.plugin.j2735.J2735DSRCmsgID;
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
      throws XmlUtilsException, JsonProcessingException {
    log.debug("Key: {} payload: {}", consumerRecord.key(), consumerRecord.value());

    JSONObject consumed = XmlUtils.toJSONObject(consumerRecord.value())
        .getJSONObject(OdeAsn1Data.class.getSimpleName());
    J2735DSRCmsgID messageId = J2735DSRCmsgID.valueOf(
        consumed.getJSONObject(AppContext.PAYLOAD_STRING)
            .getJSONObject(AppContext.DATA_STRING)
            .getJSONObject("MessageFrame")
            .getInt("messageId")
    );

    var metadataJson = XmlUtils.toJSONObject(consumerRecord.value())
        .getJSONObject(OdeAsn1Data.class.getSimpleName())
        .getJSONObject(AppContext.METADATA_STRING);
    OdeLogMetadata.RecordType recordType = OdeLogMetadata.RecordType
        .valueOf(metadataJson.getString("recordType"));

    String streamId;
    if (Strings.isNullOrEmpty(consumerRecord.key())
        || "null".equalsIgnoreCase(consumerRecord.key())) {
      streamId = metadataJson.getJSONObject("serialId").getString("streamId");
    } else {
      streamId = consumerRecord.key();
    }

    switch (messageId) {
      case BasicSafetyMessage -> routeBSM(consumerRecord, recordType);
      case TravelerInformation -> routeTIM(consumerRecord, streamId, recordType);
      case SPATMessage -> routeSPAT(consumerRecord, recordType);
      case MAPMessage -> routeMAP(consumerRecord, recordType);
      case SSMMessage -> routeSSM(consumerRecord, recordType);
      case SRMMessage -> routeSRM(consumerRecord, recordType);
      case PersonalSafetyMessage -> routePSM(consumerRecord, recordType);
      case null, default -> log.warn("Unknown message type: {}", messageId);
    }
  }

  private void routePSM(ConsumerRecord<String, String> consumerRecord, RecordType recordType)
      throws XmlUtils.XmlUtilsException {
    String odePsmData = OdePsmDataCreatorHelper.createOdePsmData(consumerRecord.value()).toString();
    if (recordType == RecordType.psmTx) {
      kafkaTemplate.send(pojoTopics.getTxPsm(), consumerRecord.key(), odePsmData);
    }
    // Send all PSMs also to OdePsmJson
    kafkaTemplate.send(jsonTopics.getPsm(), consumerRecord.key(), odePsmData);
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
    switch (recordType) {
      case bsmLogDuringEvent ->
          bsmDataKafkaTemplate.send(pojoTopics.getBsmDuringEvent(), consumerRecord.key(),
              odeBsmData);
      case rxMsg ->
          bsmDataKafkaTemplate.send(pojoTopics.getRxBsm(), consumerRecord.key(), odeBsmData);
      case bsmTx ->
          bsmDataKafkaTemplate.send(pojoTopics.getTxBsm(), consumerRecord.key(), odeBsmData);
      default -> log.trace("Consumed BSM data with record type: {}", recordType);
    }
    // Send all BSMs also to OdeBsmPojo
    bsmDataKafkaTemplate.send(pojoTopics.getBsm(), consumerRecord.key(), odeBsmData);
  }
}
