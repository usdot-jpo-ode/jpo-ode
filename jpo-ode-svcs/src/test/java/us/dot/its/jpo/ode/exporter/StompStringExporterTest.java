/*=============================================================================
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import us.dot.its.jpo.ode.stomp.StompContent;

class StompStringExporterTest {

  private SimpMessagingTemplate mockTemplate;

  @BeforeEach
  public void setup() {
    mockTemplate = Mockito.mock(SimpMessagingTemplate.class);
  }

  @Test
  void testListenUnfiltered() {
    // Arrange
    String unfilteredTopic = "/topic/unfiltered";
    String topic = "testTopic";
    String messageKey = "key";
    String messageValue = "testMessage";
    long offset = 10;
    ConsumerRecord<String, String> consumerRecord = new ConsumerRecord<>(topic, 0, offset, messageKey, messageValue);
    StompStringExporter stompStringExporter = new StompStringExporter(mockTemplate, null, unfilteredTopic);

    // Act
    stompStringExporter.listenUnfiltered(consumerRecord);

    // Assert
    verify(mockTemplate, times(1)).convertAndSend(eq(unfilteredTopic), any(StompContent.class));
  }

  @Test
  void testListenFiltered() {
    // Arrange
    String filteredTopic = "/topic/filtered";
    String topic = "testTopic";
    String messageKey = "key";
    String messageValue = "testMessage";
    long offset = 10;
    ConsumerRecord<String, String> consumerRecord = new ConsumerRecord<>(topic, 0, offset, messageKey, messageValue);
    StompStringExporter stompStringExporter = new StompStringExporter(mockTemplate, null, filteredTopic);

    // Act
    stompStringExporter.listenUnfiltered(consumerRecord);

    // Assert
    verify(mockTemplate, times(1)).convertAndSend(eq(filteredTopic), any(StompContent.class));
  }
}
