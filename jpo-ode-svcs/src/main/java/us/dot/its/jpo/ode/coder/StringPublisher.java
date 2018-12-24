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

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class StringPublisher extends MessagePublisher {

   private static final Logger logger = LoggerFactory.getLogger(StringPublisher.class);
   protected MessageProducer<String, String> stringProducer;

   public StringPublisher(OdeProperties odeProps) {
      super(odeProps);
      this.stringProducer = MessageProducer.defaultStringMessageProducer(
         odeProperties.getKafkaBrokers(), odeProperties.getKafkaProducerType(), 
         odeProperties.getKafkaTopicsDisabledSet());

   }

   public void publish(String msg, String topic) {
    logger.debug("Publishing String data to {}", topic);
    stringProducer.send(topic, null, msg);
   }

}
