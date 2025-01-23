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

import org.junit.jupiter.api.Test;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class ByteArrayPublisherTest {

   @Tested
   ByteArrayPublisher testMessagePublisher;

   @Injectable
   OdeKafkaProperties injectableOdeKafkaProperties;
   @Injectable
   String testSerializer;
   @Mocked
   byte[] mockOdeBsmData;

   @Capturing
   MessageProducer<String, byte[]> capturingMessageProducer;

   @Test
   public void shouldPublishTwice() {

      new Expectations() {
         {
            capturingMessageProducer.send(anyString, null, (byte[]) any);
            times = 1;
         }
      };

      testMessagePublisher.publish("topic", mockOdeBsmData);
   }
}
