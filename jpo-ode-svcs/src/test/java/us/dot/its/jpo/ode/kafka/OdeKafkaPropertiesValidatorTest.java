package us.dot.its.jpo.ode.kafka;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OdeKafkaPropertiesValidatorTest {

    @Test
    void supports() {
        OdeKafkaPropertiesValidator validator = new OdeKafkaPropertiesValidator();
        assertTrue(validator.supports(OdeKafkaProperties.class));
        assertFalse(validator.supports(Object.class));
    }

    @Test
    void validate() {
        OdeKafkaProperties properties = new OdeKafkaProperties();
        properties.setBrokers("localhost:9092");
        OdeKafkaProperties.Producer producer = new OdeKafkaProperties.Producer();
        producer.setAcks("all");
        properties.setProducer(producer);

        OdeKafkaPropertiesValidator validator = new OdeKafkaPropertiesValidator();
        org.springframework.validation.BeanPropertyBindingResult errors = new org.springframework.validation.BeanPropertyBindingResult(properties, "properties");
        validator.validate(properties, errors);

        assertFalse(errors.hasErrors());
    }

    @Test
    void errorThrownForInvalidAcks() {
        OdeKafkaProperties properties = new OdeKafkaProperties();
        properties.setBrokers("localhost:9092");
        OdeKafkaProperties.Producer producer = new OdeKafkaProperties.Producer();
        producer.setAcks("none");
        properties.setProducer(producer);

        OdeKafkaPropertiesValidator validator = new OdeKafkaPropertiesValidator();
        org.springframework.validation.BeanPropertyBindingResult errors = new org.springframework.validation.BeanPropertyBindingResult(properties, "properties");
        validator.validate(properties, errors);

        assertTrue(errors.hasErrors());
    }

    @Test
    void errorThrownForInvalidBrokers() {
        OdeKafkaProperties properties = new OdeKafkaProperties();
        properties.setBrokers("");
        OdeKafkaProperties.Producer producer = new OdeKafkaProperties.Producer();
        producer.setAcks("all");
        properties.setProducer(producer);

        OdeKafkaPropertiesValidator validator = new OdeKafkaPropertiesValidator();
        org.springframework.validation.BeanPropertyBindingResult errors = new org.springframework.validation.BeanPropertyBindingResult(properties, "properties");
        validator.validate(properties, errors);

        assertTrue(errors.hasErrors());
    }

    @Test
    void errorThrownForInvalidKafkaType() {
        OdeKafkaProperties properties = new OdeKafkaProperties();
        OdeKafkaProperties.Producer producer = new OdeKafkaProperties.Producer();
        properties.setProducer(producer);
        properties.setKafkaType("REDPANDA");

        OdeKafkaPropertiesValidator validator = new OdeKafkaPropertiesValidator();
        org.springframework.validation.BeanPropertyBindingResult errors = new org.springframework.validation.BeanPropertyBindingResult(properties, "properties");
        validator.validate(properties, errors);

        assertTrue(errors.hasErrors());
        assertNotNull(errors.getFieldError("kafkaType"));
    }

    @Test
    void errorThrownForInvalidConfluentProperties() {
        OdeKafkaProperties properties = new OdeKafkaProperties();
        OdeKafkaProperties.Producer producer = new OdeKafkaProperties.Producer();
        properties.setProducer(producer);
        properties.setKafkaType("CONFLUENT");
        ConfluentProperties confluentProperties = new ConfluentProperties();
        properties.setConfluent(confluentProperties);

        OdeKafkaPropertiesValidator validator = new OdeKafkaPropertiesValidator();
        org.springframework.validation.BeanPropertyBindingResult errors = new org.springframework.validation.BeanPropertyBindingResult(properties, "properties");
        validator.validate(properties, errors);
        assertTrue(errors.hasErrors());
        assertNotNull(errors.getFieldError("confluent.password"));
        assertNotNull(errors.getFieldError("confluent.username"));
    }
}