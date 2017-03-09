package us.dot.its.jpo.ode.heartbeat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Vector;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.event.ResponseEvent;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class RsuSnmpTest {
    
    @Test
    public void shouldThrowExceptionNullParameter() {
        try {
            RsuSnmp.sendSnmpV3Request(null, null, null);
        } catch (Exception e) {
            assertEquals("Incorrect exception thrown.", IllegalArgumentException.class, e.getClass());
            assertTrue("Incorrect exception message", ("Invalid SNMP request parameter").equals(e.getMessage()) );
        }
    }

    @Test
    public void sendShouldCatchException(@Injectable Snmp mockSnmp) {
        
        try {
            new Expectations() {{
                mockSnmp.send((PDU)any, (Target)any);
                //result = null;
                result = new IOException("testException123");
                //mockSnmp.close();
            }};
        } catch (IOException e) {
            fail("Unexpected exception in expectations block" + e);
        }
        
        RsuSnmp.sendSnmpV3Request("127.0.0.1", "1.1", mockSnmp); 
    }
    
    @Test
    public void sendShouldReturnConnectionError(@Injectable Snmp mockSnmp) {
        
        String expectedMessage = "[ERROR] SNMP connection error";
        
        try {
            new Expectations() {{
                mockSnmp.send((PDU)any, (Target)any);
                result = null;
                //result = new IOException("testException123");
                mockSnmp.close();
            }};
        } catch (IOException e) {
            fail("Unexpected exception in expectations block" + e);
        }
        
       String  actualMessage = RsuSnmp.sendSnmpV3Request("127.0.0.1", "1.1", mockSnmp); 
       
       assertEquals(expectedMessage, actualMessage);
    }
    
    @Test
    public void shouldReturnEmptyResponse(@Injectable Snmp mockSnmp, @Injectable ResponseEvent mockResponseEvent) {
        
        String expectedMessage = "[ERROR] Empty SNMP response";
        
        try {
            new Expectations() {{
                mockSnmp.send((PDU)any, (Target)any);
                result = mockResponseEvent;
                //result = new IOException("testException123");
                mockSnmp.close();
                
                mockResponseEvent.getResponse();
                result = null;
            }};
        } catch (IOException e) {
            fail("Unexpected exception in expectations block" + e);
        }
        
       String  actualMessage = RsuSnmp.sendSnmpV3Request("127.0.0.1", "1.1", mockSnmp); 
       
       assertEquals(expectedMessage, actualMessage);
    }
    
    @Test
    public void shouldReturnVariableBindings(@Injectable Snmp mockSnmp, @Injectable ResponseEvent mockResponseEvent, @Injectable PDU mockPDU, @Mocked Vector mockVector) {
        
        String inputMessage = "test_rsu_message_1";
        String expectedMessage = "test_rsu_message_1";
        
        try {
            new Expectations() {{
                mockSnmp.send((PDU)any, (Target)any);
                result = mockResponseEvent;
                //result = new IOException("testException123");
                mockSnmp.close();
                
                mockResponseEvent.getResponse();
                result = mockPDU;
                mockPDU.getVariableBindings();
                result = mockVector;
                mockVector.toString();
                result = inputMessage;
            }};
        } catch (IOException e) {
            fail("Unexpected exception in expectations block" + e);
        }
        
       String  actualMessage = RsuSnmp.sendSnmpV3Request("127.0.0.1", "1.1", mockSnmp); 
       
       assertEquals(expectedMessage, actualMessage);
    }

}
