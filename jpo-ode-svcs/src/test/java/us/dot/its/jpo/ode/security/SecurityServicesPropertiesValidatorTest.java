package us.dot.its.jpo.ode.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecurityServicesPropertiesValidatorTest {

        @Test
        void supports() {
            SecurityServicesPropertiesValidator validator = new SecurityServicesPropertiesValidator();
            assertTrue(validator.supports(SecurityServicesProperties.class));
            assertFalse(validator.supports(Object.class));
        }

        @Test
        void validate() {
            SecurityServicesProperties properties = new SecurityServicesProperties();
            properties.setIsRsuSigningEnabled(true);
            properties.setIsSdwSigningEnabled(true);
            properties.setSignatureEndpoint("http://localhost:8080/sign");

            SecurityServicesPropertiesValidator validator = new SecurityServicesPropertiesValidator();
            org.springframework.validation.BeanPropertyBindingResult errors = new org.springframework.validation.BeanPropertyBindingResult(properties, "properties");
            validator.validate(properties, errors);

            assertFalse(errors.hasErrors());
        }

        @Test
        void errorThrownForInvalidSignatureEndpoint() {
            SecurityServicesProperties properties = new SecurityServicesProperties();
            properties.setIsRsuSigningEnabled(true);
            properties.setIsSdwSigningEnabled(true);
            properties.setSignatureEndpoint("localhost:8080/sign");

            SecurityServicesPropertiesValidator validator = new SecurityServicesPropertiesValidator();
            org.springframework.validation.BeanPropertyBindingResult errors = new org.springframework.validation.BeanPropertyBindingResult(properties, "properties");
            validator.validate(properties, errors);

            assertTrue(errors.hasErrors());
        }

        @Test
        void errorThrownForInvalidHostIP() {
            SecurityServicesProperties properties = new SecurityServicesProperties();
            properties.setIsRsuSigningEnabled(true);
            properties.setIsSdwSigningEnabled(true);
            properties.setHostIP(null);
            properties.setPort(8080);

            SecurityServicesPropertiesValidator validator = new SecurityServicesPropertiesValidator();
            org.springframework.validation.BeanPropertyBindingResult errors = new org.springframework.validation.BeanPropertyBindingResult(properties, "properties");
            validator.validate(properties, errors);

            assertTrue(errors.hasErrors());
        }

        @Test
        void errorThrownForInvalidPort() {
            SecurityServicesProperties properties = new SecurityServicesProperties();
            properties.setIsRsuSigningEnabled(true);
            properties.setIsSdwSigningEnabled(true);
            properties.setHostIP("localhost");

            SecurityServicesPropertiesValidator validator = new SecurityServicesPropertiesValidator();
            org.springframework.validation.BeanPropertyBindingResult errors = new org.springframework.validation.BeanPropertyBindingResult(properties, "properties");
            validator.validate(properties, errors);

            assertTrue(errors.hasErrors());
        }
}