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

import org.junit.jupiter.api.Test;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class ToJsonConverterTest {
   
   @Tested
   ToJsonConverter<String> testToJsonConverter;

   @Injectable
   OdeKafkaProperties injectableOdeKafkaProperties;
   @Injectable
   boolean mockVerbose;
   @Injectable
   String mockTopic;
   
   @Test
   public void shouldCalltoJson(@Mocked final MessageProducer<String, String> mockMessageProducer) {
      new Expectations() {{
         JsonUtils.toJson(any, anyBoolean);
      }};
      testToJsonConverter.process(new String());
   }


}
