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

import mockit.Capturing;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import org.junit.jupiter.api.Test;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

import java.util.Set;


class OdeDataPublisherTest {

    @Tested
    OdeDataPublisher testMessagePublisher = new OdeDataPublisher("sync",
            "localhost:9093",
            Set.of(),
            "us.dot.its.jpo.ode.wrapper.serdes.OdeBsmSerializer");
    @Mocked
    OdeBsmData mockOdeBsmData;


    @Capturing
    MessageProducer<String, OdeData> capturingMessageProducer;

    @Test
    void shouldPublishOnce() {

        new Expectations() {
            {
                capturingMessageProducer.send(anyString, null, (OdeData) any);
                times = 1;
            }
        };

        testMessagePublisher.publish("topic", mockOdeBsmData);
    }
}
