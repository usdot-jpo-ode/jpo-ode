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
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class OdeStringPublisherTest {

   @Tested
   OdeStringPublisher testOdeStringPublisher;

   @Injectable
   OdeProperties injectableOdeProperties;

   @Capturing
   MessageProducer<String, String> capturingMessageProducer;

   @Test
   public void publishShouldCallMessageProducer() {
      new Expectations() {
         {
            capturingMessageProducer.send(anyString, null, anyString);
            times = 1;
         }
      };

      testOdeStringPublisher.publish(new OdeData(), "testTopic");
   }
}
