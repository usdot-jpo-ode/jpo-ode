package us.dot.its.jpo.ode.subscriber;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class SubscriberControllerTest {
    
    @Tested
    SubscriberController testSubscriberController;
    @Injectable
    SimpMessagingTemplate template;

    @Test
    public void testGreeting(@Mocked RegistrationMessage mockRegistrationMessage) {
        try {
            testSubscriberController.greeting(mockRegistrationMessage);
        } catch (InterruptedException e) {
            fail("Unexpected exception testing greeting method: " + e);
        }
        
        try {
            new Verifications() {{
                mockRegistrationMessage.getName();
                times = 1;
                Thread.sleep(anyLong);
            }};
        } catch (InterruptedException e) {
            fail("Unexpected exception in verifications block: " + e);
        }
    }
    
    @Test
    public void testMessages() {
        assertEquals("{\"success\": true}", testSubscriberController.messages());
        
        new Verifications() {{
            template.convertAndSend(anyString, (Subscriber) any);
        }};
    }
    
    @Test
    public void testTest() {
        assertEquals("index", testSubscriberController.test());
    }

}
