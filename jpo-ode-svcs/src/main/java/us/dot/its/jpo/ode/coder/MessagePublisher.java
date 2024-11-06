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

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.OdeProperties;

public abstract class MessagePublisher {

   private final Logger logger = LoggerFactory.getLogger(this.getClass());

   @Getter
   protected OdeKafkaProperties odeKafkaProperties;
   // TODO(Matt): remove once all kafka properties are migrated to ODEKafkaProperties and the delegates
   @Getter
   protected OdeProperties odeProperties;

   public MessagePublisher(OdeProperties odeProperties, OdeKafkaProperties odeKafkaProperties) {
      this.odeProperties = odeProperties;
      this.odeKafkaProperties = odeKafkaProperties;
      logger.info("Using Brokers: {} of Type: {}", this.odeKafkaProperties.getBrokers(), this.odeKafkaProperties.getProducerType());
   }

}
