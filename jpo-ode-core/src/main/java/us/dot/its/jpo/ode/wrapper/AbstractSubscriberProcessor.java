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
package us.dot.its.jpo.ode.wrapper;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.concurrent.Executors;

/**
 * @author 572682
 *
 *         This abstract class provides a basic service that subscribes to one
 *         or more topics and process the received messages in the derived
 *         classes.
 * 
 * @param <K>
 *           Message Key type
 * @param <S>
 *           Received Message Value Type
 */
@Slf4j
public abstract class AbstractSubscriberProcessor<K, S> extends MessageProcessor<K, S> {

   protected int messagesConsumed = 0;

   /**
    * Starts a Kafka listener that runs call() every time a new msg arrives
    * 
    * @param consumer
    * @param inputTopics
    */
   public void start(MessageConsumer<K, S> consumer, String... inputTopics) {
      log.info("Subscribing to {}", Arrays.asList(inputTopics));

      Executors.newSingleThreadExecutor().submit(() -> consumer.subscribe(inputTopics));
   }

   @Override
   public Object call() {
      messagesConsumed++;

      S consumedData = getRecord().value();

      return process(consumedData);
   }

   protected abstract Object process(S consumedData);

}
