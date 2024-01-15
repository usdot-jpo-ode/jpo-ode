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

import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import mockit.Mocked;
import mockit.Verifications;

import static org.mockito.Mockito.*;

class WebSocketConfigTest {

    // mock the webSocketConfig class
    @Mocked
    WebSocketConfig webSocketConfig;

    @Test
    void configureMessageBroker_shouldConfigureMessageBrokerRegistry() {
        webSocketConfig = new WebSocketConfig();
        MessageBrokerRegistry config = mock(MessageBrokerRegistry.class);
        webSocketConfig.configureMessageBroker(config);

        new Verifications() {{
            config.enableSimpleBroker(anyString);
            times = 1;
            config.setApplicationDestinationPrefixes(anyString);
            times = 1;
        }};
    }

    @Test
    void registerStompEndpoints_shouldRegisterStompEndpointRegistry() {
        webSocketConfig = new WebSocketConfig();
        StompEndpointRegistry registry = mock(StompEndpointRegistry.class);
        webSocketConfig.registerStompEndpoints(registry);

        new Verifications() {{
            registry.addEndpoint(anyString);
        }};
    }
}
