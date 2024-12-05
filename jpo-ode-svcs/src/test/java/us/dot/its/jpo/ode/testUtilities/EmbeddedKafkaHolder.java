package us.dot.its.jpo.ode.testUtilities;

import org.springframework.kafka.KafkaException;
import org.springframework.kafka.test.EmbeddedKafkaBroker;

public final class EmbeddedKafkaHolder {

    private static final EmbeddedKafkaBroker embeddedKafka = new EmbeddedKafkaBroker(1, true, 1)
            .brokerListProperty("spring.kafka.bootstrap-servers");

    private static boolean started;

    public static EmbeddedKafkaBroker getEmbeddedKafka() {
        if (!started) {
            try {
                embeddedKafka.kafkaPorts(4242);
                embeddedKafka.afterPropertiesSet();
            }
            catch (Exception e) {
                throw new KafkaException("Embedded broker failed to start", e);
            }
            started = true;
        }
        return embeddedKafka;
    }

    private EmbeddedKafkaHolder() {
        super();
    }
}
