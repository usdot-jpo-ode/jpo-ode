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

import lombok.extern.slf4j.Slf4j;

import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

import java.util.Set;

@Slf4j
public class OdeDataPublisher implements MessagePublisher<OdeData> {

   protected MessageProducer<String, OdeObject> objectProducer;

   public OdeDataPublisher(String producerType, String brokers, Set<String> disabledTopics, String serializer) {
      this.objectProducer = new MessageProducer<>(brokers,
            producerType,
            null, serializer, 
            disabledTopics);
   }

   public void publish(String topic, OdeData msg) {
      log.debug("Publishing to {}: {}", topic, msg);
      objectProducer.send(topic, null, msg);
   }

}
