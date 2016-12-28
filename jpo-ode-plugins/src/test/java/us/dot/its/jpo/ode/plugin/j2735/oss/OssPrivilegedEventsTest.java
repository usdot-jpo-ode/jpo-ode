package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.oss.asn1.BitString;

import us.dot.its.jpo.ode.j2735.dsrc.PrivilegedEventFlags;
import us.dot.its.jpo.ode.j2735.dsrc.PrivilegedEvents;
import us.dot.its.jpo.ode.j2735.dsrc.SSPindex;
import us.dot.its.jpo.ode.plugin.j2735.J2735PrivilegedEvents;

/**
 * -- Summary --
 * JUnit test class for OssPrivilegedEvents
 * 
 * Verifies correct conversion from generic PrivilegedEvents to compliant-J2735PrivilegedEvents.
 * 
 * -- Documentation --
 * Data Frame: DF_PrivilegedEvents
 * Use: The DF_PrivilegedEvents data frame provides a means to describe various public safety events. The information 
 * in this data frame (along with the BSM message in which it is sent) can be used to determine various aspects about 
 * the sender.
 * ASN.1 Representation:
 *    PrivilegedEvents ::= SEQUENCE {
 *       -- CERT SSP Privilege Details
 *       sspRights SSPindex,
 *       -- The active event list
 *       event PrivilegedEventFlags,
 *       ...
 *    }
 * 
 * Data Element: DE_SSPindex
 * Use: The SSP index is used to control the data elements that follow the occurrence of the index. The index relates 
 * back' to the SSP contents in the CERT used to declare what content is allowed by that CERT. In the absence of a 
 * matching index in the message sender’s CERT, the message contents are not valid.
 * ASN.1 Representation:
 *    SSPindex ::= INTEGER (0..31)
 *    
 * Data Element: DE_PrivilegedEventFlags
 * Use: The PrivilegedEventFlags data element conveys various states of the sender (typically a DSRC-equipped vehicle)
 * and is most often used by various types of public safety vehicles in response to a service call. These flags are not
 * required by common light duty passenger vehicles. This data element (more correctly the data frame in which it is 
 * used) required the presence of an SSP index element to confirm the sender's right to use it. The SSP index element 
 * matches to a bit sequence in the SSP of the CERT used in sending the message to confirm the privilege of sending 
 * this data.
 * ASN.1 Representation:
 *    PrivilegedEventFlags ::= BIT STRING {
 *       -- These values require a suitable SSP to be sent
 *       peUnavailable (0), -- Not Equipped or unavailable
 *       peEmergencyResponse (1),
 *          -- The vehicle is a properly authorized public safety vehicle,
 *          -- is engaged in a service call, and is currently moving
 *          -- or is within the roadway. Note that lights and sirens
 *          -- may not be evident during any given response call 
 *          -- Emergency and Non Emergency Lights related
 *       peEmergencyLightsActive (2),
 *       peEmergencySoundActive (3),
 *       peNonEmergencyLightsActive (4),
 *       peNonEmergencySoundActive (5)
 *       -- this list is likely to grow with further peer review
 *       } (SIZE (16))
 */
public class OssPrivilegedEventsTest {
    
    // SSPindex tests
    
    /**
     * Test that the minimum ssp index value 0 returns 0
     */
    @Test
    public void shouldReturnMinimumSSPindex() {
        
        Integer testInput = 0;
        Integer expectedValue = 0;
        
        SSPindex testSSPindex = new SSPindex(testInput);
        
        Integer testEventBitString = 0b000000;
        byte[] testEventsBytes = {testEventBitString.byteValue()};
        
        PrivilegedEventFlags testPrivilegedEventsFlags = new PrivilegedEventFlags(testEventsBytes);
        PrivilegedEvents testEvents = new PrivilegedEvents(testSSPindex, testPrivilegedEventsFlags);
        
        J2735PrivilegedEvents actualEvents = OssPrivilegedEvents.genericPrivilegedEvents(testEvents);
        
        assertEquals(expectedValue, actualEvents.sspRights);
        
    }
    
    /**
     * Test that a corner case minimum ssp index value 1 returns 1
     */
    @Test
    public void shouldReturnCornerCaseMinimumSSPindex() {

        Integer testInput = 1;
        Integer expectedValue = 1;
        
        SSPindex testSSPindex = new SSPindex(testInput);
        
        Integer testEventBitString = 0b000000;
        byte[] testEventsBytes = {testEventBitString.byteValue()};
        
        PrivilegedEventFlags testPrivilegedEventsFlags = new PrivilegedEventFlags(testEventsBytes);
        PrivilegedEvents testEvents = new PrivilegedEvents(testSSPindex, testPrivilegedEventsFlags);
        
        J2735PrivilegedEvents actualEvents = OssPrivilegedEvents.genericPrivilegedEvents(testEvents);
        
        assertEquals(expectedValue, actualEvents.sspRights);
    }
    
    /**
     * Test that a middle ssp index value 15 returns 15
     */
    @Test
    public void shouldReturnMiddleSSPindex() {

        Integer testInput = 15;
        Integer expectedValue = 15;
        
        SSPindex testSSPindex = new SSPindex(testInput);
        
        Integer testEventBitString = 0b000000;
        byte[] testEventsBytes = {testEventBitString.byteValue()};
        
        PrivilegedEventFlags testPrivilegedEventsFlags = new PrivilegedEventFlags(testEventsBytes);
        PrivilegedEvents testEvents = new PrivilegedEvents(testSSPindex, testPrivilegedEventsFlags);
        
        J2735PrivilegedEvents actualEvents = OssPrivilegedEvents.genericPrivilegedEvents(testEvents);
        
        assertEquals(expectedValue, actualEvents.sspRights);
    }
    
    /**
     * Test that a corner case maximum ssp index value 30 returns 30
     */
    @Test
    public void shouldReturnCornerCaseMaximumSSPindex() {

        Integer testInput = 30;
        Integer expectedValue = 30;
        
        SSPindex testSSPindex = new SSPindex(testInput);
        
        Integer testEventBitString = 0b000000;
        byte[] testEventsBytes = {testEventBitString.byteValue()};
        
        PrivilegedEventFlags testPrivilegedEventsFlags = new PrivilegedEventFlags(testEventsBytes);
        PrivilegedEvents testEvents = new PrivilegedEvents(testSSPindex, testPrivilegedEventsFlags);
        
        J2735PrivilegedEvents actualEvents = OssPrivilegedEvents.genericPrivilegedEvents(testEvents);
        
        assertEquals(expectedValue, actualEvents.sspRights);
    }
    
    /**
     * Test that the maximum ssp index value 31 returns 31
     */
    @Test
    public void shouldReturnMaximumSSPindex() {

        Integer testInput = 31;
        Integer expectedValue = 31;
        
        SSPindex testSSPindex = new SSPindex(testInput);
        
        Integer testEventBitString = 0b000000;
        byte[] testEventsBytes = {testEventBitString.byteValue()};
        
        PrivilegedEventFlags testPrivilegedEventsFlags = new PrivilegedEventFlags(testEventsBytes);
        PrivilegedEvents testEvents = new PrivilegedEvents(testSSPindex, testPrivilegedEventsFlags);
        
        J2735PrivilegedEvents actualEvents = OssPrivilegedEvents.genericPrivilegedEvents(testEvents);
        
        assertEquals(expectedValue, actualEvents.sspRights);
    }
    
    /**
     * Test that an ssp index value (-1) below the lower bound (0) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionSSPindexBelowLowerBound() {

        Integer testInput = -1;
        
        SSPindex testSSPindex = new SSPindex(testInput);
        
        Integer testEventBitString = 0b000000;
        byte[] testEventsBytes = {testEventBitString.byteValue()};
        
        PrivilegedEventFlags testPrivilegedEventsFlags = new PrivilegedEventFlags(testEventsBytes);
        PrivilegedEvents testEvents = new PrivilegedEvents(testSSPindex, testPrivilegedEventsFlags);
        
        try {
            J2735PrivilegedEvents actualEvents = OssPrivilegedEvents.genericPrivilegedEvents(testEvents);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    /**
     * Test that an ssp index value (32) above the upper bound (31) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionSSPindexAboveUpperBound() {

        Integer testInput = 32;
        
        SSPindex testSSPindex = new SSPindex(testInput);
        
        Integer testEventBitString = 0b000000;
        byte[] testEventsBytes = {testEventBitString.byteValue()};
        
        PrivilegedEventFlags testPrivilegedEventsFlags = new PrivilegedEventFlags(testEventsBytes);
        PrivilegedEvents testEvents = new PrivilegedEvents(testSSPindex, testPrivilegedEventsFlags);
        
        try {
            J2735PrivilegedEvents actualEvents = OssPrivilegedEvents.genericPrivilegedEvents(testEvents);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    // Event flags tests
    
    /**
     * Test input bit string "000000" returns "false" for all values
     */
    @Test
    public void shouldCreateAllOffPrivilegedEvents() {
        
        Integer testEventBitString = 0b000000;
        
        SSPindex testSSPindex = new SSPindex(0);
        
        byte[] testEventsBytes = {testEventBitString.byteValue()};
        
        PrivilegedEventFlags testPrivilegedEventsFlags = new PrivilegedEventFlags(testEventsBytes);
        PrivilegedEvents testEvents = new PrivilegedEvents(testSSPindex, testPrivilegedEventsFlags);
        
        J2735PrivilegedEvents actualEvents = OssPrivilegedEvents.genericPrivilegedEvents(testEvents);
        
        for (Map.Entry<String, Boolean> entry : actualEvents.event.entrySet()) {
            
            assertFalse("Expected " + entry.getKey() + " to be false", entry.getValue());
            
        }
        
    }
    
    /**
     * Test input bit string "111111" returns "true" for all values
     */
    @Test
    public void shouldCreateAllOnPrivilegedEvents() {
        
        Integer testEventBitString = 0b111111;
        
        SSPindex testSSPindex = new SSPindex(0);
        
        byte[] testEventsBytes = {testEventBitString.byteValue()};
        
        PrivilegedEventFlags testPrivilegedEventsFlags = new PrivilegedEventFlags(testEventsBytes);
        PrivilegedEvents testEvents = new PrivilegedEvents(testSSPindex, testPrivilegedEventsFlags);
        
        J2735PrivilegedEvents actualEvents = OssPrivilegedEvents.genericPrivilegedEvents(testEvents);
        
        for (Map.Entry<String, Boolean> entry : actualEvents.event.entrySet()) {
            assertTrue("Expected " + entry.getKey() + " to be true", entry.getValue());
        }
        
    }
    
    /**
     * Test input bit string "000001" (bit index #0 active) returns "true" for "peUnavailable" only
     */
    @Test
    public void shouldCreateUnavailablePrivilegedEvent() {
        
        Integer testEventBitString = 0b000001;
        String elementTested = "peUnavailable";
        
        SSPindex testSSPindex = new SSPindex(0);
        
        byte[] testEventsBytes = {testEventBitString.byteValue()};
        
        PrivilegedEventFlags testPrivilegedEventsFlags = new PrivilegedEventFlags(testEventsBytes);
        PrivilegedEvents testEvents = new PrivilegedEvents(testSSPindex, testPrivilegedEventsFlags);
        
        J2735PrivilegedEvents actualEvents = OssPrivilegedEvents.genericPrivilegedEvents(testEvents);
        
        for (Map.Entry<String, Boolean> curVal : actualEvents.event.entrySet()) {
            if(curVal.getKey() == elementTested) {
                assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
            } else {
                assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
            }
        }
        
    }
    
    /**
     * Test input bit string "000010" (bit index #1 active) returns "true" for "peEmergencyResponse" only
     */
    @Test
    public void shouldCreateEmergencyResponsePrivilegedEvent() {
        
        Integer testEventBitString = 0b000010;
        String elementTested = "peEmergencyResponse";
        
        SSPindex testSSPindex = new SSPindex(0);
        
        byte[] testEventsBytes = {testEventBitString.byteValue()};
        
        PrivilegedEventFlags testPrivilegedEventsFlags = new PrivilegedEventFlags(testEventsBytes);
        PrivilegedEvents testEvents = new PrivilegedEvents(testSSPindex, testPrivilegedEventsFlags);
        
        J2735PrivilegedEvents actualEvents = OssPrivilegedEvents.genericPrivilegedEvents(testEvents);
        
        for (Map.Entry<String, Boolean> curVal : actualEvents.event.entrySet()) {
            if(curVal.getKey() == elementTested) {
                assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
            } else {
                assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
            }
        }   
        
    }
    
    /**
     * Test input bit string "010000" (bit index #4 active) returns "true" for "peNonEmergencyLightsActive" only
     */
    @Test
    public void shouldCreateEmergencyLightsActivePrivilegedEvent() {
        
        Integer testEventBitString = 0b010000;
        String elementTested = "peNonEmergencyLightsActive";
        
        SSPindex testSSPindex = new SSPindex(0);
        
        byte[] testEventsBytes = {testEventBitString.byteValue()};
        
        PrivilegedEventFlags testPrivilegedEventsFlags = new PrivilegedEventFlags(testEventsBytes);
        PrivilegedEvents testEvents = new PrivilegedEvents(testSSPindex, testPrivilegedEventsFlags);
        
        J2735PrivilegedEvents actualEvents = OssPrivilegedEvents.genericPrivilegedEvents(testEvents);
        
        for (Map.Entry<String, Boolean> curVal : actualEvents.event.entrySet()) {
            if(curVal.getKey() == elementTested) {
                assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
            } else {
                assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
            }
        }  
        
    }
    
    /**
     * Test input bit string "100000" (bit index #5 active) returns "true" for "peNonEmergencySoundActive" only
     */
    @Test
    public void shouldCreateNonEmergencySoundActivePrivilegedEvent() {
        
        Integer testEventBitString = 0b100000;
        String elementTested = "peNonEmergencySoundActive";
        
        SSPindex testSSPindex = new SSPindex(0);
        
        byte[] testEventsBytes = {testEventBitString.byteValue()};
        
        PrivilegedEventFlags testPrivilegedEventsFlags = new PrivilegedEventFlags(testEventsBytes);
        PrivilegedEvents testEvents = new PrivilegedEvents(testSSPindex, testPrivilegedEventsFlags);
        
        J2735PrivilegedEvents actualEvents = OssPrivilegedEvents.genericPrivilegedEvents(testEvents);
        
        for (Map.Entry<String, Boolean> curVal : actualEvents.event.entrySet()) {
            if(curVal.getKey() == elementTested) {
                assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
            } else {
                assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
            }
        }
        
    }
    
    /**
     * Test input bit string "100001" returns "true" for both "peNonEmergencySoundActive" and "peUnavailable"
     * and false for all other values
     */
    @Test
    public void shouldCreateTwoPrivilegedEvents() {
        
        Integer testEventBitString = 0b100001;
        String elementTested1 = "peNonEmergencySoundActive";
        String elementTested2 = "peUnavailable";
        
        SSPindex testSSPindex = new SSPindex(0);
        
        byte[] testEventsBytes = {testEventBitString.byteValue()};
        
        PrivilegedEventFlags testPrivilegedEventsFlags = new PrivilegedEventFlags(testEventsBytes);
        PrivilegedEvents testEvents = new PrivilegedEvents(testSSPindex, testPrivilegedEventsFlags);
        
        J2735PrivilegedEvents actualEvents = OssPrivilegedEvents.genericPrivilegedEvents(testEvents);
        
        for (Map.Entry<String, Boolean> curVal : actualEvents.event.entrySet()) {
            if(curVal.getKey() == elementTested1 || curVal.getKey() == elementTested2) {
                assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
            } else {
                assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
            }
        }
    }

}
