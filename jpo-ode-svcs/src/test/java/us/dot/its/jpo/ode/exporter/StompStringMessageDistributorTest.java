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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import mockit.Expectations;
import mockit.Injectable;
//import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.stomp.StompContent;

//@RunWith(JMockit.class)
public class StompStringMessageDistributorTest {

    @Test
    public void shouldConstructAndConvertAndSend(@Injectable SimpMessagingTemplate mockSimpMessagingTemplate,
            @Injectable ConsumerRecord<String, String> mockConsumerRecord) {

        String testTopic = "testTopic123";

        new Expectations() {
            {
                mockSimpMessagingTemplate.convertAndSend(testTopic, (StompContent) any);
                mockConsumerRecord.value();
                result = anyString;
            }
        };

        StompStringMessageDistributor testSSNMP = new StompStringMessageDistributor(mockSimpMessagingTemplate,
                testTopic);

        testSSNMP.setRecord(mockConsumerRecord);

        try {
            assertNull(testSSNMP.call());
        } catch (Exception e) {
            fail("Unexpected exception: " + e);
        }

    }

}
