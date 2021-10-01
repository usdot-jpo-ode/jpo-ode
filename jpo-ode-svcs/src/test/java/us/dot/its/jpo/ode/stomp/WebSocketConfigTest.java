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
package us.dot.its.jpo.ode.stomp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
//import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.stomp.WebSocketConfig;

//@RunWith(JMockit.class)
public class WebSocketConfigTest {

//    @Mocked
//    WebSocketConfig testWebSocketConfig;

    @Test
    public void test(@Mocked MessageBrokerRegistry mockMessageBrokerRegistry) {
    	WebSocketConfig testWebSocketConfig = new WebSocketConfig();
        testWebSocketConfig.configureMessageBroker(mockMessageBrokerRegistry);

        new Verifications() {
            {
                mockMessageBrokerRegistry.enableSimpleBroker(anyString);
                times = 1;
                mockMessageBrokerRegistry.setApplicationDestinationPrefixes(anyString);
                times = 1;
            }
        };
    }

    @Test
    public void testRegisterStompEndpoints(@Mocked StompEndpointRegistry mockStompEndpointRegistry) {
    	WebSocketConfig testWebSocketConfig = new WebSocketConfig();
        testWebSocketConfig.registerStompEndpoints(mockStompEndpointRegistry);

        new Verifications() {
            {
                mockStompEndpointRegistry.addEndpoint(anyString);
            }
        };
    }

}
