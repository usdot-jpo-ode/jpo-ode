package us.dot.its.jpo.ode;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@EnableConfigurationProperties(value = OdeKafkaProperties.class)
class OdeTimJsonTopologyTest {

    @Autowired
    private OdeKafkaProperties odeKafkaProperties;

    @Value("${ode.kafka.topics.json.tim}")
    private String timTopic;

    private OdeTimJsonTopology odeTimJsonTopology;

    @BeforeEach
    void setUp() throws SecurityException, IllegalArgumentException {
        odeTimJsonTopology = new OdeTimJsonTopology(odeKafkaProperties, timTopic);
        Awaitility.setDefaultTimeout(250, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    @Test
    void testStop() {
        odeTimJsonTopology.stop();
        Awaitility.await().untilAsserted(() -> assertFalse(odeTimJsonTopology.isRunning()));
    }

    @Test
    void testIsRunning() {
        Awaitility.await().untilAsserted(() -> assertTrue(odeTimJsonTopology.isRunning()));
    }
}