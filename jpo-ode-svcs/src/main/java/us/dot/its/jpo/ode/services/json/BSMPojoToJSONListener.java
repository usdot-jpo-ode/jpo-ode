/*===========================================================================
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

package us.dot.its.jpo.ode.services.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.ode.model.OdeBsmData;


/**
 * The BSMPojoToJSONListener class is a Kafka listener that processes messages containing
 * {@link OdeBsmData} objects. It listens to a specified Kafka topic, converts the received
 * messages to JSON format, and publishes the resulting JSON strings to a different Kafka topic.
 */
@Component
@Slf4j
public class BSMPojoToJSONListener {

  private final String produceTopic;
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper mapper;

  /**
   * Constructs a BSMPojoToJSONListener instance, which listens to Kafka topics for OdeBsmData
   * objects, converts them to their JSON representation, and sends the resulting message to another Kafka topic.
   *
   * @param kafkaTemplate the KafkaTemplate used to send JSON messages to the specified topic
   * @param produceTopic the Kafka topic to which JSON messages will be sent
   * @param mapper the ObjectMapper used to convert OdeBsmData objects to their JSON representation
   */
  @Autowired
  public BSMPojoToJSONListener(KafkaTemplate<String, String> kafkaTemplate,
                               @Value("${ode.kafka.topics.json.bsm}") String produceTopic,
                               ObjectMapper mapper) {
    super();
    this.kafkaTemplate = kafkaTemplate;
    this.produceTopic = produceTopic;
    this.mapper = mapper;
  }

  /**
   * Listens for Kafka messages of type {@link ConsumerRecord} containing keys of type String
   * and values of type {@link OdeBsmData}. Converts the value to its JSON representation and
   * sends it to a specified Kafka topic.
   *
   * @param consumerRecord            the Kafka message received, containing a key-value pair where
   *                                    the key is a String and the value is of type {@link OdeBsmData}
   * @throws JsonProcessingException  if an error occurs during JSON serialization of the value
   */
  @KafkaListener(
      id = "BSMPojoToJSONListener",
      topics = "${ode.kafka.topics.pojo.bsm}",
      containerFactory = "odeBsmDataConsumerListenerContainerFactory"
  )
  public void listen(ConsumerRecord<String, OdeBsmData> consumerRecord) throws JsonProcessingException {
    log.debug("Received record on topic: {} with key: {}", consumerRecord.topic(), consumerRecord.key());
    kafkaTemplate.send(produceTopic, consumerRecord.key(), mapper.writeValueAsString(consumerRecord.value()));
  }
}
