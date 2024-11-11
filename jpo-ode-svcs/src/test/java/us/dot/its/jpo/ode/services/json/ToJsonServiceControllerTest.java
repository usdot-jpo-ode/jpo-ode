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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import mockit.Expectations;
import mockit.Injectable;
import us.dot.its.jpo.ode.kafka.JsonTopics;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.kafka.PojoTopics;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;
import us.dot.its.jpo.ode.wrapper.MessageProcessor;

public class ToJsonServiceControllerTest {

   @Injectable
   OdeKafkaProperties mockOdeKafkaProperties;
   @Injectable
   JsonTopics jsonTopics;
   @Injectable
   PojoTopics pojoTopics;

//   @Capturing
//   ToJsonConverter<?> capturingToJsonConverter;
//   @Capturing
//   MessageConsumer<?, ?> capturingMessageConsumer;
   
   @Test @Disabled
   public void test() {
      new Expectations() {
         {
            new ToJsonConverter<>((OdeKafkaProperties) any, anyBoolean, anyString);
            times = 1;

            new MessageConsumer<>(anyString, anyString, (MessageProcessor<?, ?>) any, anyString);
            times = 1;

         }
      };
      new ToJsonServiceController(mockOdeKafkaProperties, jsonTopics, pojoTopics);
   }

}
