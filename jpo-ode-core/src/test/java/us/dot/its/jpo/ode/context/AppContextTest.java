package us.dot.its.jpo.ode.context;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppContextTest {

    @Test
    void getInstance() {
        assertNotNull(AppContext.getInstance());
    }

    @Test
    void getHostId() {
        // The hostID is specific to the machine running the test, so it will be different for each machine.
        // Therefore, we can only consistently check that it is not null and not an empty string.
        String hostID = AppContext.getInstance().getHostId();
        assertNotNull(hostID);
        assertNotEquals("", hostID);
    }
}