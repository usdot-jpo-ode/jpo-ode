package us.dot.its.jpo.ode.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@EnableConfigurationProperties(value = Asn1CoderTopics.class)
class Asn1CoderTopicsTest {

    @Autowired
    Asn1CoderTopics asn1CoderTopics;

    @Test
    void getDecoderInput() {
        assertEquals("topic.Asn1DecoderInput", asn1CoderTopics.getDecoderInput());
    }

    @Test
    void getDecoderOutput() {
        assertEquals("topic.Asn1DecoderOutput", asn1CoderTopics.getDecoderOutput());
    }

    @Test
    void getEncoderInput() {
        assertEquals("topic.Asn1EncoderInput", asn1CoderTopics.getEncoderInput());
    }

    @Test
    void getEncoderOutput() {
        assertEquals("topic.Asn1EncoderOutput", asn1CoderTopics.getEncoderOutput());
    }
}