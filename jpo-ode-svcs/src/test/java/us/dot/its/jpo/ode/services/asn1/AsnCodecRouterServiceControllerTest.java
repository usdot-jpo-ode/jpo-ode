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

import mockit.Expectations;
import mockit.Injectable;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.kafka.*;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;

import static org.junit.Assert.assertNotNull;

class AsnCodecRouterServiceControllerTest {

    @Injectable
    OdeProperties injectableOdeProperties;
    @Injectable
    OdeKafkaProperties odeKafkaProperties;
    @Injectable
    JsonTopics jsonTopics;
    @Injectable
    PojoTopics pojoTopics;
    @Injectable
    Asn1CoderTopics asn1CoderTopics;
    @Injectable
    SDXDepositorTopics sdxDepositorTopics;

    @Test
    @Disabled
    void shouldStartTwoConsumers() {

        new Expectations() {
            {
                MessageConsumer.defaultStringMessageConsumer(anyString, anyString, (Asn1DecodedDataRouter) any);
                times = 2;
            }
        };

        assertNotNull(new AsnCodecRouterServiceController(injectableOdeProperties, odeKafkaProperties, jsonTopics, pojoTopics, asn1CoderTopics, sdxDepositorTopics));
    }
}
