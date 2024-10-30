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
package us.dot.its.jpo.ode.coder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class OdeDataPublisher extends MessagePublisher {

   private static final Logger logger = LoggerFactory.getLogger(OdeDataPublisher.class);
   protected MessageProducer<String, OdeObject> objectProducer;

   public OdeDataPublisher(OdeProperties odeProperties, OdeKafkaProperties odeKafkaProperties, String serializer) {
      super(odeProperties, odeKafkaProperties);
      this.objectProducer = new MessageProducer<>(this.odeKafkaProperties.getBrokers(),
            this.odeKafkaProperties.getProducerType(),
            null, serializer, 
            this.odeKafkaProperties.getDisabledTopics());
   }

   public void publish(OdeData msg, String topic) {
      logger.debug("Publishing to {}: {}", topic, msg);
      objectProducer.send(topic, null, msg);
   }

}
