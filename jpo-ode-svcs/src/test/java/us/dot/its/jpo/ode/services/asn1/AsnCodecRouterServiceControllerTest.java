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
package us.dot.its.jpo.ode.services.asn1;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import mockit.Expectations;
import mockit.Injectable;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;

public class AsnCodecRouterServiceControllerTest {

//   @Capturing
//   MessageConsumer<?, ?> capturingMessageConsumer;
//
//   @Capturing
//   Asn1DecodedDataRouter capturingAsn1DecodedDataRouter;
//   
//   @Capturing 
//   Asn1EncodedDataRouter capturingAsn1EncodedDataRouter;

   @Injectable
   OdeProperties injectableOdeProperties;
   @Injectable
   OdeKafkaProperties odeKafkaProperties;

   @Test @Disabled
   public void shouldStartTwoConsumers() {

      new Expectations() {
         {
            MessageConsumer.defaultStringMessageConsumer(anyString, anyString, (Asn1DecodedDataRouter) any);
            times = 2;
         }
      };

      assertNotNull(new AsnCodecRouterServiceController(injectableOdeProperties, odeKafkaProperties));
   }

}
