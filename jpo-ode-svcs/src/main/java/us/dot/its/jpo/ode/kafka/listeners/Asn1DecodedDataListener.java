package us.dot.its.jpo.ode.kafka.listeners;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.ode.coder.OdeMapDataCreatorHelper;
import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeLogMetadata;
import us.dot.its.jpo.ode.util.XmlUtils;

/**
 * The Asn1DecodedDataListener class is a component responsible for processing decoded ASN.1 data
 * from Kafka topics. It listens to messages on a specified Kafka topic and handles the incoming
 * data by processing and forwarding it to different topics based on specific criteria.
 *
 * </p>This listener is specifically designed to handle MAP data. Upon receiving a
 * payload, it uses the {@link OdeMapDataCreatorHelper} to transform the payload and then determine
 * the appropriate Kafka topic to forward the processed data.
 *
 * </p>The class utilizes Spring Kafka's annotation-driven listener configuration,
 * allowing it to automatically consume messages from a configured Kafka topic.
 */
@Slf4j
@Component
@KafkaListener(
    id = "Asn1DecodedDataListener",
    topics = "${ode.kafka.topics.asn1.decoder-output}",
    containerFactory = "tempFilteringKafkaListenerContainerFactory"
)
public class Asn1DecodedDataListener {

  private final String jsonMapTopic;
  private final String pojoTxMapTopic;
  private final KafkaTemplate<String, String> kafkaTemplate;

  /**
   * Constructs an instance of Asn1DecodedDataListener.
   *
   * @param kafkaTemplate  the KafkaTemplate used for sending messages to Kafka topics.
   * @param pojoTxMapTopic the name of the Kafka topic to which messages containing map transactions
   *                       (mapTx) in POJO format are sent.
   * @param jsonMapTopic   the name of the Kafka topic where JSON-formatted messages are sent.
   */
  public Asn1DecodedDataListener(KafkaTemplate<String, String> kafkaTemplate,
      @Value("${ode.kafka.topics.pojo.tx-map}") String pojoTxMapTopic,
      @Value("${ode.kafka.topics.json.map}") String jsonMapTopic) {
    this.kafkaTemplate = kafkaTemplate;
    this.pojoTxMapTopic = pojoTxMapTopic;
    this.jsonMapTopic = jsonMapTopic;
  }

  /**
   * Processes the given Kafka message payload by transforming it into ODE MAP data and publishing
   * it to appropriate Kafka topics based on its record type. Specifically, it publishes all
   * transformed MAP data to a JSON topic and conditionally to a transaction-map topic if the record
   * type is `mapTx`.
   *
   * @param keys    the headers of the Kafka message, typically containing metadata for the
   *                message.
   * @param payload the payload of the Kafka message, expected to be a string representation of the
   *                data that needs to be transformed and processed.
   */
  @KafkaHandler
  public void listenToMAPs(@Headers Map<String, Object> keys, @Payload String payload) {
    log.debug("Key: {} payload: {}", keys, payload);
    try {
      String odeMapData = OdeMapDataCreatorHelper.createOdeMapData(payload).toString();

      OdeLogMetadata.RecordType recordType = OdeLogMetadata.RecordType
          .valueOf(XmlUtils.toJSONObject(payload)
              .getJSONObject(OdeAsn1Data.class.getSimpleName())
              .getJSONObject(AppContext.METADATA_STRING)
              .getString("recordType")
          );
      if (recordType == OdeLogMetadata.RecordType.mapTx) {
        log.debug("Publishing message with recordType: {} to {} ", recordType,
            pojoTxMapTopic);
        kafkaTemplate.send(pojoTxMapTopic, odeMapData);
      }

      // Send all MAP data to OdeMapJson despite the record type
      kafkaTemplate.send(jsonMapTopic, odeMapData);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
}
