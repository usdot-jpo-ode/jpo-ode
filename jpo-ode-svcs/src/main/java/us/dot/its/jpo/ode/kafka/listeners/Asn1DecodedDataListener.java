package us.dot.its.jpo.ode.kafka.listeners;

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
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeLogMetadata;
import us.dot.its.jpo.ode.util.XmlUtils;

import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@KafkaListener(id = "Asn1DecodedDataListener", topics = "${ode.kafka.topics.asn1.decoder-output}", containerFactory = "tempFilteringKafkaListenerContainerFactory")
public class Asn1DecodedDataListener {


    private final String jsonMapTopic;
    private final String pojoTxMapTopic;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Set<String> disabledTopics;

    public Asn1DecodedDataListener(KafkaTemplate<String, String> kafkaTemplate,
                                   @Value("${ode.kafka.topics.pojo.tx-map}") String pojoTxMapTopic,
                                   @Value("${ode.kafka.topics.json.map}") String jsonMapTopic,
                                   OdeKafkaProperties odeKafkaProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.pojoTxMapTopic = pojoTxMapTopic;
        this.jsonMapTopic = jsonMapTopic;
        this.disabledTopics = odeKafkaProperties.getDisabledTopics();
    }

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
                log.debug("Publishing message with recordType: {} to {} ", recordType, pojoTxMapTopic);
                send(odeMapData, pojoTxMapTopic);
            }

            // Send all MAP data to OdeMapJson despite the record type
            send(odeMapData, jsonMapTopic);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void send(String odeMapData, String publishTopic) {
        if (disabledTopics.contains(publishTopic)) {
            log.debug("Topic {} is disabled. Skipping sending message.", publishTopic);
            return;
        }

        var future = kafkaTemplate.send(publishTopic, odeMapData);
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error(ex.getMessage(), ex);
            } else {
                log.debug("Successfully sent message to topic {} with offset {} on partition {}",
                        publishTopic, result.getRecordMetadata().offset(), result.getRecordMetadata().partition());
            }
        });
    }
}
