package us.dot.its.jpo.ode.kafka.listeners.asn1;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.asn.j2735.r2024.MessageFrame.DSRCmsgID;
import us.dot.its.jpo.ode.coder.OdeMessageFrameDataCreatorHelper;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeMsgPayload;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.XmlUtils;

/** Routes legacy external ADM output when {@code ode.asn1.codec-mode=external}. */
@Component
@ConditionalOnProperty(
    name = "ode.asn1.codec-mode", havingValue = "external", matchIfMissing = true)
public class Asn1DecodedDataRouter {

  private final JsonTopics topics;
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final XmlMapper xmlMapper;

  public Asn1DecodedDataRouter(
      KafkaTemplate<String, String> kafkaTemplate,
      JsonTopics topics,
      @Qualifier("simpleXmlMapper") XmlMapper xmlMapper) {
    this.kafkaTemplate = kafkaTemplate;
    this.topics = topics;
    this.xmlMapper = xmlMapper;
  }

  /** Routes one external decoder result to its message-specific JSON topic. */
  @KafkaListener(id = "Asn1DecodedDataRouter", topics = "${ode.kafka.topics.asn1.decoder-output}")
  public void listen(ConsumerRecord<String, String> record) throws Exception {
    JSONObject consumed = XmlUtils.toJSONObject(record.value())
        .getJSONObject(OdeAsn1Data.class.getSimpleName());
    JSONObject data = consumed.getJSONObject(OdeMsgPayload.PAYLOAD_STRING)
        .getJSONObject(OdeMsgPayload.DATA_STRING);
    if (data.has("code")) {
      throw new IllegalArgumentException("External ASN.1 decode failed: " + data);
    }
    String destination = destination(new DSRCmsgID(
        data.getJSONObject("MessageFrame").getInt("messageId")).name().orElse("Unknown"));
    if (destination == null) {
      return;
    }
    var decoded = OdeMessageFrameDataCreatorHelper.createOdeMessageFrameData(record.value(), xmlMapper);
    kafkaTemplate.send(destination, record.key(), JsonUtils.toJson(decoded, false));
  }

  private String destination(String name) {
    return switch (name) {
      case "basicSafetyMessage" -> topics.getBsm();
      case "travelerInformation" -> topics.getTim();
      case "mapData" -> topics.getMap();
      case "signalPhaseAndTimingMessage" -> topics.getSpat();
      case "personalSafetyMessage" -> topics.getPsm();
      case "signalStatusMessage" -> topics.getSsm();
      case "signalRequestMessage" -> topics.getSrm();
      case "sensorDataSharingMessage" -> topics.getSdsm();
      case "rtcmCorrections" -> topics.getRtcm();
      case "roadSafetyMessage" -> topics.getRsm();
      default -> null;
    };
  }
}
