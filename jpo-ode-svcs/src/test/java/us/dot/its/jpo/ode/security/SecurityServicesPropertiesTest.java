package us.dot.its.jpo.ode.security;

import org.junit.jupiter.api.Order;
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
@EnableConfigurationProperties(value = SecurityServicesProperties.class)
class SecurityServicesPropertiesTest {

    @Autowired
    SecurityServicesProperties securityServicesProperties;

    @Test
    @Order(1)
    void getSignatureEndpoint() {
        assertEquals("http://localhost:8090/sign", securityServicesProperties.getSignatureEndpoint());
    }

    @Test
    @Order(2)
    void getSignatureEndpoint_DefaultWhenNoValueProvided() {
        securityServicesProperties.setSignatureEndpoint(null);
        assertEquals("http://test-host:1234/sign", securityServicesProperties.getSignatureEndpoint());
    }

    @Test
    void getHostIP() {
        assertEquals("test-host", securityServicesProperties.getHostIP());
    }

    @Test
    void getPort() {
        assertEquals(1234, securityServicesProperties.getPort());
    }

    @Test
    void isSdwEnabled_DefaultsToTrueWhenNoValueProvided() {
        assertTrue(securityServicesProperties.getIsSdwSigningEnabled());
    }

    @Test
    void isRsuEnabled_DefaultsToFalseWhenNoValueProvided() {
        assertFalse(securityServicesProperties.getIsRsuSigningEnabled());
    }
}