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
package us.dot.its.jpo.ode.exporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.wrapper.MessageConsumer;

public abstract class Exporter implements Runnable {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private MessageConsumer<?, ?> consumer;
    private String topic;

    public Exporter(String topic) {
        this.topic = topic;
    }

    public Exporter(String topic, MessageConsumer<?, ?> consumer) {
        this.topic = topic;
        this.consumer = consumer;
    }

    @Override
    public void run() {
        logger.info("Subscribing to {}", topic);
        subscribe();

        shutDown();
    }

    protected abstract void subscribe();

    public void shutDown() {
        logger.info("Shutting down Exporter to topic {}", topic);
        if (consumer != null)
            consumer.close();
    }

    @SuppressWarnings("rawtypes")
   public MessageConsumer getConsumer() {
        return consumer;
    }

    public void setConsumer(MessageConsumer<?, ?> consumer) {
        this.consumer = consumer;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

}
