package us.dot.its.jpo.ode.stomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.stomp.RegistrationMessage;
import us.dot.its.jpo.ode.stomp.StompContent;
import us.dot.its.jpo.ode.stomp.StompController;

@RunWith(JMockit.class)
public class StompControllerTest {
    
    @Tested
    StompController testStompController;
    @Injectable
    SimpMessagingTemplate template;

    @Test
    public void testGreeting(@Mocked Thread unused, @Mocked RegistrationMessage mockRegistrationMessage) {
        try {
            testStompController.greeting(mockRegistrationMessage);
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
        assertEquals("{\"success\": true}", testStompController.messages());
        
        new Verifications() {{
            template.convertAndSend(anyString, (StompContent) any);
        }};
    }
    
    @Test
    public void testTest() {
        assertEquals("index", testStompController.test());
    }

}
