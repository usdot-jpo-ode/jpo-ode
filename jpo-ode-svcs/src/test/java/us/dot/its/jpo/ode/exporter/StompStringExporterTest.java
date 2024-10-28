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

import mockit.*;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;
import us.dot.its.jpo.ode.wrapper.MessageProcessor;

public class StompStringExporterTest {

   @Tested
   StompStringExporter testStompExporter;

   @Injectable
   OdeProperties injectableOdeProperties;
   @Injectable
   OdeKafkaProperties injectableOdeKafkaProperties;
   @Injectable
   String stompTopic = "testTopic";
   @Injectable
   SimpMessagingTemplate simpMessagingTemplate;
   @Injectable
   String odeTopic;

   @SuppressWarnings({ "rawtypes", "unchecked" })
   @Test
   public void testSubscribe(@Capturing MessageConsumer capturingMessageConsumer, @Mocked MessageConsumer mockMessageConsumer) {
      new Expectations() {{
         MessageConsumer.defaultStringMessageConsumer(anyString, anyString, (MessageProcessor) any);
         result = mockMessageConsumer;
         
         mockMessageConsumer.setName(anyString);
         times = 1;
         mockMessageConsumer.subscribe(anyString);
         times = 1;
      }};
      testStompExporter.subscribe();
   }
}
