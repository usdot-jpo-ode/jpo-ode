package us.dot.its.jpo.ode.rsuHealth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.snmp4j.Snmp;

public class RsuSnmpTest {
    
    @Mock private Snmp mockSnmp;

    @Before
    public void setUpSnmp() throws IOException {
        
        //MockitoAnnotations.initMocks(this);
        
        mockSnmp = mock(Snmp.class);    
    }

    @Test
    public void shouldCreateSnmpV3Request() throws IOException {
        
        String targetAddress = null;
        String targetOid = null;
        
        try {
            String response = RsuSnmp.sendSnmpV3Request(targetAddress, targetOid, mockSnmp);
            fail("Expected IllegalArgumentException");
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
    }
    

}
