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

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.ode.stomp.StompContent;

/**
 * The StompStringExporter is responsible for consuming Kafka messages from specified topics
 * and forwarding these messages to a WebSocket topic using <a href="https://stomp.github.io/stomp-specification-1.2.html">STOMP Protocol</a>.
 *
 * <p>The component leverages Spring's {@link KafkaListener} for message consumption and {@link SimpMessagingTemplate}
 * for publishing WebSocket messages.</p>
 *
 * <p>This component is exclusively used to support the Demo Console.
 * It can be disabled to reduce resource usage when running the application in production</p>
 */
@Slf4j
@Component
@ConditionalOnExpression("${ode.stomp-exporter.enabled:true}")
public class StompStringExporter {

  private final SimpMessagingTemplate template;
  private final String filteredTopic;
  private final String unfilteredTopic;

  /**
   * Constructs an instance of the {@code StompStringExporter}.
   * This constructor initializes the instance with the given message template
   * for sending WebSocket messages and the specified filtered and unfiltered Kafka topics.
   *
   * @param template        the {@link SimpMessagingTemplate} used for sending messages via STOMP
   * @param filteredTopic   the Kafka topic to be used for filtered messages
   * @param unfilteredTopic the Kafka topic to be used for unfiltered messages
   */
  public StompStringExporter(
      SimpMessagingTemplate template,
      @Value("${ode.stomp-exporter.channels.filtered-output}") String filteredTopic,
      @Value("${ode.stomp-exporter.channels.unfiltered-output}") String unfilteredTopic
  ) {
    this.template = template;
    this.filteredTopic = filteredTopic;
    this.unfilteredTopic = unfilteredTopic;
  }


  /**
   * Consumes Kafka messages from a set of specified topics
   * and publishes the message to the unfiltered WebSocket topic using the STOMP protocol.
   *
   * @param consumerRecord the Kafka {@link ConsumerRecord} containing the message key, value, topic,
   *                       partition, and offset.
   */
  @KafkaListener(id = "StompStringUnfilteredExporter", topics = {
      "${ode.kafka.topics.json.bsm}",
      "${ode.kafka.topics.json.tim}",
      "${ode.kafka.topics.json.spat}",
      "${ode.kafka.topics.json.map}",
      "${ode.kafka.topics.json.ssm}",
      "${ode.kafka.topics.json.srm}",
      "${ode.kafka.topics.json.driver-alert}",
      "${ode.kafka.topics.json.tim-broadcast}"
  })
  public void listenUnfiltered(ConsumerRecord<String, String> consumerRecord) {
    log.debug("Received message on topic {} with offset {}. Publishing to unfiltered topic: {}",
        consumerRecord.topic(), consumerRecord.offset(), unfilteredTopic);
    template.convertAndSend(unfilteredTopic, new StompContent(consumerRecord.value()));
  }

  /**
   * Consumes Kafka messages from specified filtered topics and publishes these messages
   * to the corresponding filtered WebSocket destination using the STOMP protocol.
   *
   * @param consumerRecord the Kafka {@link ConsumerRecord} containing the message
   *                       key, value, and metadata such as topic, partition, and offset.
   */
  @KafkaListener(id = "StompStringFilteredExporter", topics = {
      "${ode.kafka.topics.json.tim-filtered}", "${ode.kafka.topics.json.bsm-filtered}"
  })
  public void listenFiltered(ConsumerRecord<String, String> consumerRecord) {
    log.debug("Received message on topic {} with offset {}. Publishing to filtered topic: {}",
        consumerRecord.topic(), consumerRecord.offset(), filteredTopic);
    template.convertAndSend(filteredTopic, new StompContent(consumerRecord.value()));
  }
}
