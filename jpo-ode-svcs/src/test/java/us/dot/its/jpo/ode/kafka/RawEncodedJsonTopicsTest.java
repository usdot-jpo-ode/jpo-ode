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
@EnableConfigurationProperties(value = RawEncodedJsonTopics.class)
class RawEncodedJsonTopicsTest {

    @Autowired
    RawEncodedJsonTopics rawEncodedJsonTopics;

    @Test
    void getBsm() {
        assertEquals("topic.OdeRawEncodedBSMJson", rawEncodedJsonTopics.getBsm());
    }

    @Test
    void getMap() {
        assertEquals("topic.OdeRawEncodedMAPJson", rawEncodedJsonTopics.getMap());
    }

    @Test
    void getPsm() {
        assertEquals("topic.OdeRawEncodedPSMJson", rawEncodedJsonTopics.getPsm());
    }

    @Test
    void getSpat() {
        assertEquals("topic.OdeRawEncodedSPATJson", rawEncodedJsonTopics.getSpat());
    }

    @Test
    void getSrm() {
        assertEquals("topic.OdeRawEncodedSRMJson", rawEncodedJsonTopics.getSrm());
    }

    @Test
    void getSsm() {
        assertEquals("topic.OdeRawEncodedSSMJson", rawEncodedJsonTopics.getSsm());
    }

    @Test
    void getTim() {
        assertEquals("topic.OdeRawEncodedTIMJson", rawEncodedJsonTopics.getTim());
    }
}