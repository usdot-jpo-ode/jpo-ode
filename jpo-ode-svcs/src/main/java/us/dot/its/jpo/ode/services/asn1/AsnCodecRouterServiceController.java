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

import org.apache.kafka.common.serialization.Serdes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;

/**
 * Launches ToJsonConverter service
 */
@Controller
public class AsnCodecRouterServiceController {

   private static final Logger logger = LoggerFactory.getLogger(AsnCodecRouterServiceController.class);
    Serdes bas;

   @Autowired
   public AsnCodecRouterServiceController(@Qualifier("ode-us.dot.its.jpo.ode.OdeProperties") OdeProperties odeProps, OdeKafkaProperties odeKafkaProperties) {
      super();

      logger.info("Starting {}", this.getClass().getSimpleName());

      // asn1_codec Decoder Routing
      logger.info("Routing DECODED data received ASN.1 Decoder");

       Asn1DecodedDataRouter decoderRouter = new Asn1DecodedDataRouter(odeProps, odeKafkaProperties);

      MessageConsumer<String, String> asn1DecoderConsumer = MessageConsumer.defaultStringMessageConsumer(
              odeKafkaProperties.getBrokers(), this.getClass().getSimpleName(), decoderRouter);

      asn1DecoderConsumer.setName("Asn1DecoderConsumer");
      decoderRouter.start(asn1DecoderConsumer, odeProps.getKafkaTopicAsn1DecoderOutput());

      // asn1_codec Encoder Routing
      logger.info("Routing ENCODED data received ASN.1 Encoder");

       Asn1EncodedDataRouter encoderRouter = new Asn1EncodedDataRouter(odeProps, odeKafkaProperties);

      MessageConsumer<String, String> encoderConsumer = MessageConsumer.defaultStringMessageConsumer(
              odeKafkaProperties.getBrokers(), this.getClass().getSimpleName(), encoderRouter);

      encoderConsumer.setName("Asn1EncoderConsumer");
      encoderRouter.start(encoderConsumer, odeProps.getKafkaTopicAsn1EncoderOutput());
   }
}
