package us.dot.its.jpo.ode.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@EnableConfigurationProperties(value = OdeKafkaProperties.class)
class OdeKafkaPropertiesTest {

    @Autowired
    private OdeKafkaProperties odeKafkaProperties;

    @Test
    void testGetBrokers() {
        assertEquals("localhost:9092", odeKafkaProperties.getBrokers());
    }

    @Test
    void testGetProducerType() {
        assertEquals("sync", odeKafkaProperties.getProducerType());
    }

    @Test
    void testGetKafkaTopicsDisabled() {
        Set<String> kafkaTopicsDisabled = odeKafkaProperties.getDisabledTopics();
        assertEquals(4, kafkaTopicsDisabled.size());
        assertTrue(kafkaTopicsDisabled.contains("topic.OdeBsmRxPojo"));
        assertTrue(kafkaTopicsDisabled.contains("topic.OdeBsmTxPojo"));
        assertTrue(kafkaTopicsDisabled.contains("topic.OdeBsmDuringEventPojo"));
        assertTrue(kafkaTopicsDisabled.contains("topic.OdeTimBroadcastPojo"));
    }
}