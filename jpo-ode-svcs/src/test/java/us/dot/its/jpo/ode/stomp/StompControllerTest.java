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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
//import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.stomp.RegistrationMessage;
import us.dot.its.jpo.ode.stomp.StompContent;
import us.dot.its.jpo.ode.stomp.StompController;

//@RunWith(JMockit.class)
public class StompControllerTest {
    
    @Tested
    StompController testStompController;
    @Injectable
    SimpMessagingTemplate template;

    @Test
    public void testGreeting(@Mocked RegistrationMessage mockRegistrationMessage) {
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
