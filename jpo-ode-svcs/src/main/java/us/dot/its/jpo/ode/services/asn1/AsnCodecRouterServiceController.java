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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.kafka.topics.Asn1CoderTopics;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.kafka.topics.SDXDepositorTopics;
import us.dot.its.jpo.ode.rsu.RsuProperties;
import us.dot.its.jpo.ode.security.SecurityServicesProperties;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;

@Controller
@Slf4j
public class AsnCodecRouterServiceController {

    @Autowired
    public AsnCodecRouterServiceController(OdeKafkaProperties odeKafkaProperties,
                                           JsonTopics jsonTopics,
                                           Asn1CoderTopics asn1CoderTopics,
                                           SDXDepositorTopics sdxDepositorTopics,
                                           RsuProperties rsuProperties,
                                           SecurityServicesProperties securityServicesProperties) {
        super();

        log.info("Starting {}", this.getClass().getSimpleName());

        // asn1_codec Encoder Routing
        log.info("Routing ENCODED data received ASN.1 Encoder");

        Asn1EncodedDataRouter encoderRouter = new Asn1EncodedDataRouter(
                odeKafkaProperties,
                asn1CoderTopics,
                jsonTopics,
                sdxDepositorTopics,
                rsuProperties,
                securityServicesProperties);

        MessageConsumer<String, String> encoderConsumer = MessageConsumer.defaultStringMessageConsumer(
                odeKafkaProperties.getBrokers(), this.getClass().getSimpleName(), encoderRouter);

        encoderConsumer.setName("Asn1EncoderConsumer");
        encoderRouter.start(encoderConsumer, asn1CoderTopics.getEncoderOutput());
    }
}
