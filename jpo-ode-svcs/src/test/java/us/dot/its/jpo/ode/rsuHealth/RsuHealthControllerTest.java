package us.dot.its.jpo.ode.rsuHealth;

import static org.junit.Assert.*;

import org.junit.Test;

public class RsuHealthControllerTest {

    @Test
    public void shouldRefuseConnectionNullIp() {
        
        String testIp = null;
        String testOid = "1.1";
        
        String response = null;
        
        try {
            response = RsuHealthController.heartBeat(testIp, testOid);
            fail("Expected IllegalArgumentException");
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    @Test
    public void shouldRefuseConnectionNullOid() {
        
        
        String testIp = "127.0.0.1";
        String testOid = null;
        
        String response = null;
        
        try {
            response = RsuHealthController.heartBeat(testIp, testOid);
            fail("Expected IllegalArgumentException");
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
    }

}
