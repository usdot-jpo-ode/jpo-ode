package us.dot.its.jpo.ode.rsuHealth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.OID;

public class RsuSnmpTest {
    
    @Mock private Snmp mockSnmp;

    @Before
    public void setUpSnmp() throws IOException {
        
        //MockitoAnnotations.initMocks(this);
        
        mockSnmp = mock(Snmp.class);    
    }

    @Test
    public void shouldCreateSnmpV3Request() throws IOException {
        
        String username = "fakeUser";
        String password = "fakePass";
        
        Address targetAddress = null;
        OID targetOid = null;
        
        try {
            ResponseEvent response = RsuSnmp.sendSnmpV3(username, password, targetAddress, targetOid, mockSnmp);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
    }
    

}
