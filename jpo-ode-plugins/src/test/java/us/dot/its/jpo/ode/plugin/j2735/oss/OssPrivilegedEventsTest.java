package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import com.oss.asn1.BitString;

import us.dot.its.jpo.ode.j2735.dsrc.PrivilegedEventFlags;
import us.dot.its.jpo.ode.j2735.dsrc.PrivilegedEvents;
import us.dot.its.jpo.ode.j2735.dsrc.SSPindex;

/**
 * -- Summary --
 * JUnit test class for OssPrivilegedEvents
 * 
 * Verifies correct conversion from generic PrivilegedEvents to compliant-J2735PrivilegedEvents
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
 *          peEmergencyLightsActive (2),
 *          peEmergencySoundActive (3),
 *          peNonEmergencyLightsActive (4),
 *          peNonEmergencySoundActive (5)
 *          -- this list is likely to grow with further peer review
 *          } (SIZE (16))
 */
public class OssPrivilegedEventsTest {
    
    
    @Ignore // Test case depends on complete implementation of OssBitString
    @Test
    public void shouldCreatePriviledgedEvents() {
        
        // Set up SSPIndex
        Integer testInputSSPIndex = -1;
        SSPindex testSSPIndex = new SSPindex(testInputSSPIndex);
        
        // Set up PrivilegedEventFlag
        Integer testPrivilegedEventsFlagBitStringInteger = 0b00;
        BitString testPrivilegedEventFlagBitString = new BitString();
        
        PrivilegedEventFlags testPrivilegedEventFlags = new PrivilegedEventFlags();
        
        PrivilegedEvents testEvents = new PrivilegedEvents(testSSPIndex, testPrivilegedEventFlags);
        
        assertEquals(1, testEvents.sspRights.intValue());
    }

}
