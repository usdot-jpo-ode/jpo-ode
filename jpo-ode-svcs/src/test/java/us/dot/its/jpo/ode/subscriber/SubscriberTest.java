package us.dot.its.jpo.ode.subscriber;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SubscriberTest {

    @Test
    public void testGetter() {
        Subscriber testSubscriber = new Subscriber("testContent");

        assertEquals("testContent", testSubscriber.getContent());
    }

}
