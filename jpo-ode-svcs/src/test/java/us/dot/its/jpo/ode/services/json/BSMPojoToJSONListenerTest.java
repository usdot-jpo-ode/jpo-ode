/*============================================================================
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

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import us.dot.its.jpo.ode.config.SerializationConfig;
import us.dot.its.jpo.ode.kafka.KafkaConsumerConfig;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.kafka.producer.KafkaProducerConfig;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.kafka.topics.PojoTopics;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.test.utilities.EmbeddedKafkaHolder;

@SpringBootTest(classes = {
    BSMPojoToJSONListener.class,
    OdeKafkaProperties.class,
    KafkaProducerConfig.class,
    KafkaConsumerConfig.class,
    KafkaProperties.class,
    SerializationConfig.class,
}, properties = {
    "ode.kafka.topics.pojo.bsm=topic.BSMPojo",
    "ode.kafka.topics.json.bsm=topic.BSMJson"
})
@EnableConfigurationProperties({KafkaProperties.class, OdeKafkaProperties.class, JsonTopics.class, PojoTopics.class})
class BSMPojoToJSONListenerTest {

  @Autowired
  JsonTopics jsonTopics;
  @Autowired
  PojoTopics pojoTopics;
  @Autowired
  KafkaTemplate<String, OdeBsmData> bsmDataKafkaTemplate;

  EmbeddedKafkaBroker embeddedKafkaBroker = EmbeddedKafkaHolder.getEmbeddedKafka();

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void canConvertPojoToJSON() throws JsonProcessingException {
    EmbeddedKafkaHolder.addTopics(jsonTopics.getBsm(), pojoTopics.getBsm());

    var consumerProps = KafkaTestUtils.consumerProps(
        "bsmDecoderTest", "false", embeddedKafkaBroker);
    var consumerFactory = new DefaultKafkaConsumerFactory<String, String>(consumerProps);
    consumerFactory.setKeyDeserializer(new StringDeserializer());
    consumerFactory.setValueDeserializer(new StringDeserializer());
    var testConsumer = consumerFactory.createConsumer();
    embeddedKafkaBroker.consumeFromAnEmbeddedTopic(testConsumer, jsonTopics.getBsm());

    var bsmPojo = loadFromResource("us/dot/its/jpo/ode/services/json/to-json-converter-bsm-input.json");
    var bsmData = objectMapper.readValue(bsmPojo, OdeBsmData.class);
    bsmDataKafkaTemplate.send(pojoTopics.getBsm(), bsmData);

    var actualBsmJson = KafkaTestUtils.getSingleRecord(testConsumer, jsonTopics.getBsm());
    var expectedBsmJson = loadFromResource("us/dot/its/jpo/ode/services/json/to-json-converter-bsm-output.json");
    var expectedBsm = objectMapper.readValue(expectedBsmJson, OdeBsmData.class);
    var actualBsm = objectMapper.readValue(actualBsmJson.value(), OdeBsmData.class);
    assertEquals(expectedBsm, actualBsm);
    testConsumer.close();
  }


  private String loadFromResource(String resourcePath) {
    String baseTestData;
    try (InputStream inputStream = getClass().getClassLoader()
        .getResourceAsStream(resourcePath)) {
      if (inputStream == null) {
        throw new FileNotFoundException("Resource not found: " + resourcePath);
      }
      baseTestData = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load test data", e);
    }
    return baseTestData;
  }
}
