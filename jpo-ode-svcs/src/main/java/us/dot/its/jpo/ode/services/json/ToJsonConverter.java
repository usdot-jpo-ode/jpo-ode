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

import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.wrapper.AbstractSubPubTransformer;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

/* 
 * The MessageProcessor value type is String 
 */
public class ToJsonConverter<V> extends AbstractSubPubTransformer<String, V, String> {

    private final boolean verbose;

    public ToJsonConverter(OdeKafkaProperties odeKafkaProperties, boolean verbose, String outTopic) {
        super(MessageProducer.defaultStringMessageProducer(
           odeKafkaProperties.getBrokers(),
           odeKafkaProperties.getKafkaType(),
           odeKafkaProperties.getDisabledTopics()), outTopic);
        this.verbose = verbose;
    }

    @Override
    protected String process(V consumedData) {
        return JsonUtils.toJson(consumedData, verbose);
    }
}
