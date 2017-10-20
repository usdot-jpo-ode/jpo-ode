package us.dot.its.jpo.ode.exporter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import us.dot.its.jpo.ode.stomp.StompContent;

public class StompContentTest {

    @Test
    public void testGetter() {
        StompContent testSubscriber = new StompContent("testContent");

        assertEquals("testContent", testSubscriber.getContent());
    }

}
