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
package us.dot.its.jpo.ode.services.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;
import us.dot.its.jpo.ode.wrapper.serdes.OdeBsmDeserializer;

/**
 * Launches ToJsonConverter service
 */
@Controller
public class ToJsonServiceController {

   private static final Logger logger = LoggerFactory.getLogger(ToJsonServiceController.class);

   private OdeProperties odeProps;
   private OdeKafkaProperties odeKafkaProperties;

   @Autowired
   public ToJsonServiceController(OdeProperties odeProperties, OdeKafkaProperties odeKafkaProperties) {
      super();

      this.odeKafkaProperties = odeKafkaProperties;
      this.odeProps = odeProperties;

      // BSM POJO --> JSON converter
      launchConverter(odeProps.getKafkaTopicOdeBsmPojo(), OdeBsmDeserializer.class.getName(),
            new ToJsonConverter<>(odeProps, odeKafkaProperties, false, odeProps.getKafkaTopicOdeBsmJson()));
   }

   private <V> void launchConverter(String fromTopic, String serializerFQN, ToJsonConverter<V> jsonConverter) {
      logger.info("Starting JSON converter, converting records from topic {} and publishing to topic {} ", fromTopic,
            jsonConverter.getOutputTopic());

      MessageConsumer<String, V> consumer = new MessageConsumer<>(odeKafkaProperties.getBrokers(),
            this.getClass().getSimpleName(), jsonConverter, serializerFQN);

      consumer.setName(this.getClass().getName() + fromTopic + "Consumer");
      jsonConverter.start(consumer, fromTopic);
   }
}
