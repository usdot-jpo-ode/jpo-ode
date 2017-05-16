package us.dot.its.jpo.ode.exporter;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;

import static org.junit.Assert.fail;

@RunWith(JMockit.class)
public class ExporterTest {

    
    @Test
    public void shouldRun(@Mocked OdeProperties mockOdeProperties,
            @Injectable SimpMessagingTemplate mockSimpMessagingTemplate,
            @Mocked final MessageConsumer<String, byte[]> mockByteArrayConsumer,
            @Mocked MessageConsumer<String, String> mockStringConsumer) {

        String testTopic = "testTopic123";

        new Expectations() {
            {
                mockOdeProperties.getKafkaBrokers();
                result = anyString;

                mockOdeProperties.getHostId();
                result = anyString;

                mockStringConsumer.close();
            }
        };

        try {
            Exporter testExporter = new Exporter(mockOdeProperties, mockSimpMessagingTemplate, testTopic, "byte");
            testExporter.setStringConsumer(mockStringConsumer);
            testExporter.run();
        } catch (Exception e) {
            fail("Unexpected exception: " + e);
        }
    }

}
