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

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@Import({BuildProperties.class})
@EnableConfigurationProperties(value = {OdeProperties.class, org.springframework.boot.info.BuildProperties.class})
class OdePropertiesTest {

    @Autowired
    OdeProperties testOdeProperties;

    @Test
    void testOutputSchemaVersion() {
        assertEquals(7, testOdeProperties.getOutputSchemaVersion());
    }
}