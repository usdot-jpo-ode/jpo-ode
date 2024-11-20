package us.dot.its.jpo.ode.traveler;

import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;

import static org.junit.jupiter.api.Assertions.*;

class TimIngestPropertiesValidatorTest {

    @Test
    void supports() {
        TimIngestPropertiesValidator validator = new TimIngestPropertiesValidator();
        assertTrue(validator.supports(TimIngestTrackerProperties.class));
        assertFalse(validator.supports(Object.class));
    }

    @Test
    void validate() {
        TimIngestTrackerProperties properties = new TimIngestTrackerProperties();
        properties.setTrackingEnabled(true);
        properties.setInterval(1);

        TimIngestPropertiesValidator validator = new TimIngestPropertiesValidator();
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(properties, "properties");
        validator.validate(properties, errors);

        assertFalse(errors.hasErrors());
    }

    @Test
    void errorThrownForInvalidMonitoringInterval() {
        TimIngestTrackerProperties properties = new TimIngestTrackerProperties();
        properties.setTrackingEnabled(true);
        properties.setInterval(0);

        TimIngestPropertiesValidator validator = new TimIngestPropertiesValidator();
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(properties, "properties");
        validator.validate(properties, errors);

        assertTrue(errors.hasErrors());
    }
}