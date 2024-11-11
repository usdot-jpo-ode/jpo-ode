package us.dot.its.jpo.ode.traveler;

import org.springframework.validation.Validator;

public class TimIngestPropertiesValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return TimIngestTrackerProperties.class.equals(clazz);
    }

    @Override
    public void validate(Object target, org.springframework.validation.Errors errors) {
        TimIngestTrackerProperties properties = (TimIngestTrackerProperties) target;

        if (properties.isTrackingEnabled() && properties.getInterval() <= 0) {
            errors.rejectValue("interval", "interval.invalid", "Monitoring interval must be greater than 0");
        }
    }
}
