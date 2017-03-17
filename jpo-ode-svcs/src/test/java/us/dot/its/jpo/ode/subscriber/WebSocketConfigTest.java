package us.dot.its.jpo.ode.subscriber;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class WebSocketConfigTest {

    @Tested
    WebSocketConfig testWebSocketConfig;

    @Test
    public void test(@Mocked MessageBrokerRegistry mockMessageBrokerRegistry) {
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
        testWebSocketConfig.registerStompEndpoints(mockStompEndpointRegistry);

        new Verifications() {
            {
                mockStompEndpointRegistry.addEndpoint(anyString);
            }
        };
    }

}
