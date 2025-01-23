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
package us.dot.its.jpo.ode.exporter;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;

/**
 * Created by anthonychen on 10/16/17.
 */
public class StompStringExporter extends Exporter{

    private final SimpMessagingTemplate template;
    private final String odeTopic;
    private final String brokers;

    public StompStringExporter(
            String stompTopic,
            SimpMessagingTemplate template,
            String odeTopic,
            String brokers) {
        super(stompTopic);
        this.brokers = brokers;
        this.template = template;
        this.odeTopic = odeTopic;
    }

    @Override
    protected void subscribe() {
        setConsumer(MessageConsumer.defaultStringMessageConsumer(this.brokers,
                AppContext.getInstance().getHostId() + this.getClass().getSimpleName(),
                new StompStringMessageDistributor(template, getTopic())));

        getConsumer().setName(this.getClass().getSimpleName());
        getConsumer().subscribe(odeTopic);
    }
}
