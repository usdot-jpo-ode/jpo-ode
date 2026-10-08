package us.dot.its.jpo.ode.kafka.listeners.asn1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.asn.j2735.r2024.MessageFrame.DSRCmsgID;
import us.dot.its.jpo.ode.coder.OdeMessageFrameDataCreatorHelper;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeMessageFrameData;
import us.dot.its.jpo.ode.model.OdeMsgPayload;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

/**
 * The Asn1DecodedDataRouter class is a component responsible for processing decoded ASN.1 data from
 * Kafka topics. It listens to messages on a specified Kafka topic and handles the incoming data by
 * processing and forwarding it to different topics based on specific criteria.
 *
 * <p>
 * This listener is specifically designed to handle decoded data produced by the asn1_codec. Upon
 * receiving a payload, it transforms the payload and then determines the appropriate Kafka topic to
 * forward the processed data.
 * </p>
 *
 * <p>
 * The class utilizes Spring Kafka's annotation-driven listener configuration, allowing it to
 * automatically consume messages from a configured Kafka topic.
 * </p>
 */
@Slf4j
@Component
public class Asn1DecodedDataRouter {

  private final JsonTopics jsonTopics;
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final XmlMapper simpleXmlMapper;

  /**
   * Exception for Asn1DecodedDataRouter specific failures.
   */
  public static class Asn1DecodedDataRouterException extends Exception {
    /**
     * Creates an exception with a message describing the routing failure.
     *
     * @param string the routing failure message
     */
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
      JsonTopics jsonTopics, @Qualifier("simpleXmlMapper") XmlMapper simpleXmlMapper) {
    this.kafkaTemplate = kafkaTemplate;
    this.jsonTopics = jsonTopics;
    this.simpleXmlMapper = simpleXmlMapper;
  }

  /**
   * Processes the given Kafka message payload by transforming it into ODE data and publishing it to
   * appropriate Kafka topics based on its record type.
   */
  @KafkaListener(id = "Asn1DecodedDataRouter", topics = "${ode.kafka.topics.asn1.decoder-output}")
  public void listen(ConsumerRecord<String, String> consumerRecord)
      throws XmlUtilsException, JsonProcessingException, Asn1DecodedDataRouterException,
      JsonMappingException, JsonProcessingException, IOException {
    log.debug("Key: {} payload: {}", consumerRecord.key(), consumerRecord.value());

    JSONObject consumed = XmlUtils.toJSONObject(consumerRecord.value())
        .getJSONObject(OdeAsn1Data.class.getSimpleName());

    JSONObject payloadData = consumed.getJSONObject(OdeMsgPayload.PAYLOAD_STRING)
        .getJSONObject(OdeMsgPayload.DATA_STRING);

    if (payloadData.has("code")) {
      throw new Asn1DecodedDataRouterException(
          String.format("Error processing decoded message with code %s and message %s",
              payloadData.getString("code"),
              payloadData.has("message") ? payloadData.getString("message") : "NULL"));
    }

    int msgId = payloadData.getJSONObject("MessageFrame").getInt("messageId");
    DSRCmsgID messageId = new DSRCmsgID(msgId);
    String messageName = messageId.name().orElse("Unknown");

    switch (messageName) {
      case "basicSafetyMessage" -> routeMessageFrame(consumerRecord, jsonTopics.getBsm());
      case "travelerInformation" -> routeMessageFrame(consumerRecord, jsonTopics.getTim());
      case "mapData" -> routeMessageFrame(consumerRecord, jsonTopics.getMap());
      case "signalPhaseAndTimingMessage" -> routeMessageFrame(consumerRecord, jsonTopics.getSpat());
      case "personalSafetyMessage" -> routeMessageFrame(consumerRecord, jsonTopics.getPsm());
      case "signalStatusMessage" -> routeMessageFrame(consumerRecord, jsonTopics.getSsm());
      case "signalRequestMessage" -> routeMessageFrame(consumerRecord, jsonTopics.getSrm());
      case "sensorDataSharingMessage" -> routeMessageFrame(consumerRecord, jsonTopics.getSdsm());
      case "rtcmCorrections" -> routeMessageFrame(consumerRecord, jsonTopics.getRtcm());
      case "roadSafetyMessage" -> routeMessageFrame(consumerRecord, jsonTopics.getRsm());
      default -> log.warn("Unknown message type: {}", messageName);
    }
  }

  private void routeMessageFrame(ConsumerRecord<String, String> consumerRecord, String... topics)
      throws XmlUtils.XmlUtilsException, IOException {
    log.debug("routeMessageFrame to topics: {}", String.join(", ", topics));
    OdeMessageFrameData odeMessageFrameData =
        OdeMessageFrameDataCreatorHelper.createOdeMessageFrameData(
            consumerRecord.value(), 
            simpleXmlMapper);
    String dataStr = JsonUtils.toJson(odeMessageFrameData, false);
    for (String topic : topics) {
      kafkaTemplate.send(topic, consumerRecord.key(), dataStr);
    }
  }
}
