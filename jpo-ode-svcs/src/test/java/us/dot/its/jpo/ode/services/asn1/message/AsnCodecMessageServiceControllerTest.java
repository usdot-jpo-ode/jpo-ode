package us.dot.its.jpo.ode.services.asn1.message;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.kafka.Asn1CoderTopics;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.kafka.RawEncodedJsonTopics;

import static org.junit.Assert.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@EnableConfigurationProperties(value = {OdeKafkaProperties.class, RawEncodedJsonTopics.class, Asn1CoderTopics.class})
class AsnCodecMessageServiceControllerTest {

    @Autowired
    OdeKafkaProperties odeKafkaProps;

    @Autowired
    RawEncodedJsonTopics rawEncodedJsonTopics;

    @Autowired
    Asn1CoderTopics asn1CoderTopics;

    @Test
    void shouldStartTwoConsumers() {
        AsnCodecMessageServiceController asnCodecMessageServiceController = new AsnCodecMessageServiceController(odeKafkaProps, rawEncodedJsonTopics, asn1CoderTopics);
        assertNotNull(asnCodecMessageServiceController);
    }

}
