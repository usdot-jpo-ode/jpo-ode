package us.dot.its.jpo.ode;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import us.dot.its.jpo.ode.rsu.RSUProperties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@Import({BuildProperties.class})
@EnableConfigurationProperties(value = {OdeProperties.class, RSUProperties.class, org.springframework.boot.info.BuildProperties.class})
class OdePropertiesTest {

    @Autowired
    OdeProperties testOdeProperties;

    @Test
    void testOutputSchemaVersion() {
        assertEquals(7, testOdeProperties.getOutputSchemaVersion());
    }

    @Test
    void testPluginsLocations() {
        assertEquals("plugins", testOdeProperties.getPluginsLocations());
    }

    @Test
    void testHostIP() {
        assertEquals("test-host", testOdeProperties.getHostIP());
    }

    @Test
    void testVerboseJson() {
        assertFalse(testOdeProperties.isVerboseJson());
    }

    @Test
    void testSecuritySvcsSignatureUri() {
        String expected = "http://" + testOdeProperties.getHostIP() + ":" + testOdeProperties.getSecuritySvcsPort() + "/"
                + testOdeProperties.getSecuritySvcsSignatureEndpoint();
        assertEquals(expected, testOdeProperties.getSecuritySvcsSignatureUri());
    }

    @Test
    void testSecuritySvcsPort() {
        assertEquals(8090, testOdeProperties.getSecuritySvcsPort());
    }

    @Test
    void testSecuritySvcsSignatureEndpoint() {
        assertEquals("sign", testOdeProperties.getSecuritySvcsSignatureEndpoint());
    }

    @Test
    void testRsuProperties() {
        RSUProperties rsuProperties = testOdeProperties.rsuProperties();

        assertEquals(100, rsuProperties.getSrmSlots());
        assertEquals("test-username", rsuProperties.getUsername());
        assertEquals("test-password", rsuProperties.getPassword());
    }
}