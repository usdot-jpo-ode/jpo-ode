package us.dot.its.jpo.ode.subscriber;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

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
