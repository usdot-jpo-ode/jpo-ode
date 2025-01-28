package us.dot.its.jpo.ode.kafka;

import org.springframework.validation.Validator;

import java.util.List;

public class OdeKafkaPropertiesValidator implements Validator {

    private static final List<String> VALID_ACKS = List.of("all", "0", "1", "-1");
    private static final List<String> VALID_KAFKA_TYPES = List.of("", "CONFLUENT");

    @Override
    public boolean supports(Class<?> clazz) {
        return OdeKafkaProperties.class.equals(clazz);
    }

    @Override
    public void validate(Object target, org.springframework.validation.Errors errors) {
        OdeKafkaProperties properties = (OdeKafkaProperties) target;

        if (!VALID_ACKS.contains(properties.getProducer().getAcks())) {
            errors.rejectValue("producer.acks", "acks value must be one of: " + VALID_ACKS);
        }

        if (properties.getBrokers() == null || properties.getBrokers().isEmpty()) {
            errors.rejectValue("brokers", "brokers must be set");
        } else {
            if (!properties.getBrokers().contains(":")) {
                errors.rejectValue("brokers", "broker must be in the format host:port");
            }
        }

        if (!VALID_KAFKA_TYPES.contains(properties.getKafkaType())) {
            errors.rejectValue("kafkaType", "type value must be one of: " + VALID_KAFKA_TYPES);
        }

        if ("CONFLUENT".equals(properties.getKafkaType())) {
            ConfluentProperties confluent = properties.getConfluent();
            if (confluent.getPassword() == null) {
                errors.rejectValue("confluent.password", "when kafka-type is set to CONFLUENT the password must be set");
            }
            if (confluent.getUsername() == null) {
                errors.rejectValue("confluent.username", "when kafka-type is set to CONFLUENT the username must be set");
            }
        }
    }
}
