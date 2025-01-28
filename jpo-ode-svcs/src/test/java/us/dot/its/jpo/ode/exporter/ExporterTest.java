/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.ode.exporter;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;

import static org.junit.Assert.*;

import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;

class ExporterTest {

    @Test
    void shouldRun(@Mocked OdeKafkaProperties odeKafkaProperties,
            @Injectable SimpMessagingTemplate mockSimpMessagingTemplate,
            @Mocked final MessageConsumer<String, byte[]> mockByteArrayConsumer,
            @Mocked final MessageConsumer<String, String> mockStringConsumer) {

        String testTopic = "testTopic123";

        new Expectations() {
            {
                odeKafkaProperties.getBrokers();
                result = anyString;

                mockStringConsumer.close();
            }
        };

        try {
            Exporter odeBsmExporter = new StompStringExporter(
                    testTopic,
                    mockSimpMessagingTemplate, "odeTopic", odeKafkaProperties.getBrokers());
            odeBsmExporter.setConsumer(mockStringConsumer);
            odeBsmExporter.run();
            
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
