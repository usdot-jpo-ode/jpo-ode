package us.dot.its.jpo.ode.exporter;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;

@RunWith(JMockit.class)
public class ExporterTest {

    
    @Test
    public void shouldRun(@Mocked OdeProperties mockOdeProperties,
            @Injectable SimpMessagingTemplate mockSimpMessagingTemplate,
            @Mocked final MessageConsumer<String, byte[]> mockByteArrayConsumer,
            @Mocked final MessageConsumer<String, String> mockStringConsumer) {

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
            Exporter odeBsmExporter = new OdeBsmExporter(
                mockOdeProperties, testTopic, mockSimpMessagingTemplate);
            odeBsmExporter.setConsumer(mockStringConsumer);
            odeBsmExporter.run();
            
            Exporter FilteredBsmExporter = new FilteredBsmExporter(mockOdeProperties, testTopic, mockSimpMessagingTemplate);
            FilteredBsmExporter.setConsumer(mockByteArrayConsumer);
            FilteredBsmExporter.run();
            
            Exporter exporter = new Exporter("testTopic") {
               
               @Override
               protected void subscribe() {
                  ;
               }
            };
            exporter.run();
            assertNull(exporter.getConsumer());
            assertEquals("testTopic", exporter.getTopic());
            
            exporter = new Exporter("topic", null) {
               
               @Override
               protected void subscribe() {
                  ;
               }
            };
            exporter.run();
            
            assertNull(exporter.getConsumer());
            assertEquals("topic", exporter.getTopic());
            exporter.setTopic("topic2");
            assertEquals("topic2", exporter.getTopic());
        } catch (Exception e) {
            fail("Unexpected exception: " + e);
        }
    }

}
