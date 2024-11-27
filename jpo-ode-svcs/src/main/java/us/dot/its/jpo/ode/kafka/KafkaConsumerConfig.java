package us.dot.its.jpo.ode.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.JSONObject;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeMapData;
import us.dot.its.jpo.ode.plugin.j2735.J2735DSRCmsgID;
import us.dot.its.jpo.ode.util.XmlUtils;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@Slf4j
public class KafkaConsumerConfig {

    private final KafkaProperties kafkaProperties;
    private final OdeKafkaProperties odeKafkaProperties;

    public KafkaConsumerConfig(KafkaProperties kafkaProperties, OdeKafkaProperties odeKafkaProperties) {
        this.kafkaProperties = kafkaProperties;
        this.odeKafkaProperties = odeKafkaProperties;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        var consumerProps = kafkaProperties.buildConsumerProperties();
        if ("CONFLUENT".equals(this.odeKafkaProperties.getKafkaType())) {
            consumerProps.put("sasl.jaas.config", odeKafkaProperties.getConfluent().getSaslJaasConfig());
        }
        return new DefaultKafkaConsumerFactory<>(consumerProps);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, OdeMapData> odeMapDataConsumerFactory() {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        if ("CONFLUENT".equals(this.odeKafkaProperties.getKafkaType())) {
            props.put("sasl.jaas.config", odeKafkaProperties.getConfluent().getSaslJaasConfig());
        }
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(OdeMapData.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OdeMapData> odeMapDataConsumerListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OdeMapData> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(odeMapDataConsumerFactory());
        return factory;
    }

    /**
     * @return factory A listener factory that supports filtering out messages that don't match a specific pattern
     * <p>
     * @deprecated This method is intended to be short-lived. It exists to allow consumption via the Asn1DecodedDataRouter &
     * the Asn1DecodedDataListener while we are migrating from hand-rolled Kafka implementation to Spring's Kafka implementation
     */
    @Bean
    @Deprecated(forRemoval = true)
    public ConcurrentKafkaListenerContainerFactory<String, String> tempFilteringKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setRecordFilterStrategy(getFilterStrategySpringKafkaSupportedMessageTypesOnly());

        return factory;
    }

    /**
     * While migrating to Spring Kafka the consumers provided from this factory will only consume (and ack) messages
     * we support via the Spring Kafka implementation. All other messages will be handled by the Asn1DecodedDataRouter
     *
     * @return RecordFilterStrategy<String, String> filter
     */
    private static RecordFilterStrategy<String, String> getFilterStrategySpringKafkaSupportedMessageTypesOnly() {
        return consumerRecord -> {
            try {
                JSONObject consumed = XmlUtils.toJSONObject(consumerRecord.value()).getJSONObject(OdeAsn1Data.class.getSimpleName());

                J2735DSRCmsgID messageId = J2735DSRCmsgID.valueOf(
                        consumed.getJSONObject(AppContext.PAYLOAD_STRING)
                                .getJSONObject(AppContext.DATA_STRING)
                                .getJSONObject("MessageFrame")
                                .getInt("messageId")
                );

                // Filter out all messages EXCEPT for MAP messages
                return !J2735DSRCmsgID.MAPMessage.equals(messageId);
            } catch (XmlUtils.XmlUtilsException e) {
                log.warn("Unable to parse JSON object", e);
                return false;
            } catch (Exception e) {
                log.warn("Failed to detect message ID", e);
                return false;
            }
        };
    }
}
