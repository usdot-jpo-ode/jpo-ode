package us.dot.its.jpo.ode.security;

import org.springframework.validation.Validator;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class SecurityServicesPropertiesValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return SecurityServicesProperties.class.equals(clazz);
    }

    @Override
    public void validate(Object target, org.springframework.validation.Errors errors) {
        SecurityServicesProperties properties = (SecurityServicesProperties) target;

        if (Boolean.FALSE.equals(properties.getIsRsuSigningEnabled()) && Boolean.FALSE.equals(properties.getIsSdwSigningEnabled())) {
            // if neither RSU nor SDW are enabled, then no further validation is needed because no security services are enabled
            return;
        }

        if (properties.getSignatureEndpoint() != null && !properties.getSignatureEndpoint().isEmpty()) {
            String errorCode = "signatureEndpoint.invalid";
            String fieldName = "signatureEndpoint";
            try {
                URI uri = new URI(properties.getSignatureEndpoint());
                if (!uri.getScheme().equals("http") && !uri.getScheme().equals("https")) {
                    errors.rejectValue(fieldName, errorCode, "Signature endpoint must be an http URL");
                }
                if (uri.getHost() == null || Objects.equals(uri.getHost(), "null") || uri.getHost().isEmpty()) {
                    errors.rejectValue(fieldName, errorCode, "Signature endpoint must have a host");
                }
                if (uri.getPort() < 0) {
                    errors.rejectValue(fieldName, errorCode, "Signature endpoint must have a port");
                }
            } catch (URISyntaxException e) {
                errors.rejectValue(fieldName, errorCode, "Signature endpoint must be a valid URL");
            }
        } else {
            if (properties.getHostIP() == null || properties.getHostIP().isEmpty()) {
                errors.rejectValue("hostIP", "hostIP.invalid", "Host IP must be provided");
            }
            if (properties.getPort() <= 0) {
                errors.rejectValue("port", "port.invalid", "Port must be greater than 0");
            }
        }
    }
}
