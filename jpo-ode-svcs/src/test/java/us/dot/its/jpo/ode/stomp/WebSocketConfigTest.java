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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

class WebSocketConfigTest {

    @Test
    void configureMessageBroker_shouldConfigureMessageBrokerRegistry() {
        WebSocketConfig webSocketConfig = new WebSocketConfig();
        MessageBrokerRegistry config = mock(MessageBrokerRegistry.class, Mockito.RETURNS_DEEP_STUBS);
        webSocketConfig.configureMessageBroker(config);

        verify(config, times(1)).enableSimpleBroker(anyString());
        verify(config, times(1)).setApplicationDestinationPrefixes(anyString());
    }

    @Test
    void registerStompEndpoints_shouldRegisterStompEndpointRegistry() {
        WebSocketConfig webSocketConfig = new WebSocketConfig();
        StompEndpointRegistry registry = mock(StompEndpointRegistry.class, Mockito.RETURNS_DEEP_STUBS);
        webSocketConfig.registerStompEndpoints(registry);

        verify(registry).addEndpoint(anyString());
    }
}
