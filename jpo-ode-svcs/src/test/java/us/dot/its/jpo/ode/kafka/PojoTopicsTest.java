package us.dot.its.jpo.ode.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@EnableConfigurationProperties(value = PojoTopics.class)
class PojoTopicsTest {

    @Autowired
    PojoTopics pojoTopics;

    @Test
    void getBsm() {
        assertEquals("topic.OdeBsmPojo", pojoTopics.getBsm());
    }

    @Test
    void getSpat() {
        assertEquals("topic.OdeSpatPojo", pojoTopics.getSpat());
    }

    @Test
    void getSsm() {
        assertEquals("topic.OdeSsmPojo", pojoTopics.getSsm());
    }

    @Test
    void getTimBroadcast() {
        assertEquals("topic.OdeTimBroadcastPojo", pojoTopics.getTimBroadcast());
    }

    @Test
    void getBsmDuringEvent() {
        assertEquals("topic.OdeBsmDuringEventPojo", pojoTopics.getBsmDuringEvent());
    }

    @Test
    void getRxBsm() {
        assertEquals("topic.OdeBsmRxPojo", pojoTopics.getRxBsm());
    }

    @Test
    void getRxSpat() {
        assertEquals("topic.OdeSpatRxPojo", pojoTopics.getRxSpat());
    }

    @Test
    void getTxBsm() {
        assertEquals("topic.OdeBsmTxPojo", pojoTopics.getTxBsm());
    }

    @Test
    void getTxMap() {
        assertEquals("topic.OdeMapTxPojo", pojoTopics.getTxMap());
    }

    @Test
    void getTxPsm() {
        assertEquals("topic.OdePsmTxPojo", pojoTopics.getTxPsm());
    }

    @Test
    void getTxSpat() {
        assertEquals("topic.OdeSpatTxPojo", pojoTopics.getTxSpat());
    }

    @Test
    void getTxSrm() {
        assertEquals("topic.OdeSrmTxPojo", pojoTopics.getTxSrm());
    }
}