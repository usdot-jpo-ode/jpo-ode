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
import org.springframework.messaging.simp.SimpMessagingTemplate;

import us.dot.its.jpo.ode.stomp.StompContent;
import us.dot.its.jpo.ode.wrapper.AbstractSubscriberProcessor;

public class StompStringMessageDistributor extends AbstractSubscriberProcessor<String, String> {
   private static Logger logger = LoggerFactory.getLogger(StompStringMessageDistributor.class);

   private SimpMessagingTemplate template;
   private String topic;

   public StompStringMessageDistributor(SimpMessagingTemplate template, String topic) {
      this.template = template;
      this.topic = topic;
      logger.info("Distributing messages to API layer topic {}", topic);
   }

   @Override
   protected Object process(String consumedData) {
      template.convertAndSend(topic, new StompContent(consumedData));
      return null;
   }

}
