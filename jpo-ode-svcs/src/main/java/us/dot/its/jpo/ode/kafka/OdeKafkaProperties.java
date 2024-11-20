package us.dot.its.jpo.ode.kafka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

import java.util.Set;

@Configuration
@ConfigurationProperties(prefix = "ode.kafka")
@Data
@Validated(value = OdeKafkaPropertiesValidator.class)
public class OdeKafkaProperties {
    private String brokers;
    private Set<String> disabledTopics;
    private Producer producer;

    @Data
    public static class Producer {
        private Integer batchSize = MessageProducer.DEFAULT_PRODUCER_BATCH_SIZE_BYTES;
        private Integer bufferMemory = MessageProducer.DEFAULT_PRODUCER_BUFFER_MEMORY_BYTES;
        private Integer lingerMs = MessageProducer.DEFAULT_PRODUCER_LINGER_MS;
        private Integer retries = MessageProducer.DEFAULT_PRODUCER_RETRIES;
        private String acks = MessageProducer.DEFAULT_PRODUCER_ACKS;
        private String keySerializer = MessageProducer.SERIALIZATION_STRING_SERIALIZER;
        private String valueSerializer = MessageProducer.SERIALIZATION_BYTE_ARRAY_SERIALIZER;
        private String compressionType = MessageProducer.COMPRESSION_TYPE;
        private String partitionerClass = "org.apache.kafka.clients.producer.internals.DefaultPartitioner";
        private String type = "sync";
    }
}