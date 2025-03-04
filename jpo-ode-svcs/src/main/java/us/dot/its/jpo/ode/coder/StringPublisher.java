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
import us.dot.its.jpo.ode.wrapper.MessageProducer;

import java.util.Set;

@Slf4j
public class StringPublisher implements MessagePublisher<String> {

    protected MessageProducer<String, String> stringProducer;

    public StringPublisher(String broker, String producerType, Set<String> disabledTopics) {
        this.stringProducer = MessageProducer.defaultStringMessageProducer(
                broker,
                producerType,
                disabledTopics
        );
    }

    public void publish(String topic, String msg) {
        log.debug("Publishing String data to {}", topic);
        stringProducer.send(topic, null, msg);
    }
}
