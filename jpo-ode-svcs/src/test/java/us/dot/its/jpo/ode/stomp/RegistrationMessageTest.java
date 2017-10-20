package us.dot.its.jpo.ode.stomp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import us.dot.its.jpo.ode.stomp.RegistrationMessage;

public class RegistrationMessageTest {

    @Test
    public void testConstructor() {

        RegistrationMessage testRegistrationMessage = new RegistrationMessage("testMessage");
    }

    @Test
    public void testSettersAndGetters() {

        RegistrationMessage testRegistrationMessage = new RegistrationMessage("testMessage");

        testRegistrationMessage.setName("newMessage");
        assertEquals("newMessage", testRegistrationMessage.getName());

    }

}
