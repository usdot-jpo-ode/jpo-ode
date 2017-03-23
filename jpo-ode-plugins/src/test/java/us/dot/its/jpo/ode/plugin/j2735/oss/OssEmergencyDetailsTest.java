package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.EmergencyDetails;
import us.dot.its.jpo.ode.j2735.dsrc.LightbarInUse;
import us.dot.its.jpo.ode.j2735.dsrc.MultiVehicleResponse;
import us.dot.its.jpo.ode.j2735.dsrc.ResponseType;
import us.dot.its.jpo.ode.j2735.dsrc.SSPindex;
import us.dot.its.jpo.ode.j2735.dsrc.SirenInUse;

/**
 * -- Summary --
 * JUnit test class for OssEmergencyDetails
 * 
 * Verifies correct conversion from generic EmergencyDetails object to compliant-J2735EmergencyDetails
 * 
 * Note that PrivilegedEvents is tested by OssPrivilegedEventsTest
 * 
 * -- Documentation --
 * Data Frame: DF_EmergencyDetails
 * Use: The EmergencyDetails data element combines several bit level items into a structure for efficient 
 * transmission about the vehicle during a response call.
 * ASN.1 Representation:
 *    EmergencyDetails ::= SEQUENCE { -- CERT SSP Privilege Details
 *       sspRights SSPindex, -- index set by CERT
 *       sirenUse SirenInUse,
 *       lightsUse LightbarInUse,
 *       multi MultiVehicleResponse,
 *       events PrivilegedEvents OPTIONAL,
 *       responseType ResponseType OPTIONAL,
 *       ...
 *       }
 * 
 * Data Element: DE_SSPindex
 * Use: The SSP index is used to control the data elements that follow the occurrence of the index. The 
 * index relates back to the SSP contents in the CERT used to declare what content is allowed by that CERT. 
 * In the absence of a matching index in the message sender’s CERT, the message contents are not valid.
 * ASN.1 Representation:
 *    SSPindex ::= INTEGER (0..31)
 * 
 * Data Element: DE_SirenInUse
 * Use: A data element which is set if any sort of audible alarm is being emitted from the vehicle. This includes 
 * various common sirens as well as backup beepers and other slow speed maneuvering alerts.
 * Used to reflect any type or style of audio alerting when a vehicle is progressing and transmitting DSRC messages 
 * to others about its path. Intended to be used as part of the DSRC safety message for public safety vehicles 
 * (and others which alert during maneuvers) operating in the area.
 * ASN.1 Representation:
 *    SirenInUse ::= ENUMERATED {
 *       unavailable (0), -- Not Equipped or unavailable
 *       notInUse (1),
 *       inUse (2),
 *       reserved (3) -- for future use
 *       }
 * 
 * Data Element: DE_LightbarInUse
 * Use: The DE_LightbarInUse is a data element in which the named bits are set to one if any sort of additional 
 * visible lighting-alerting system is currently in use by a vehicle. This includes light bars and the various 
 * symbols they can indicate as well as arrow boards, flashing lights (including back up alerts), and any other 
 * form of lighting not found on normal vehicles of this type or related to safety systems. Used to reflect any 
 * type or style of visual alerting when a vehicle is progressing and transmitting DSRC messages to other nearby 
 * vehicles about its path.
 * ASN.1 Representation:
 *    LightbarInUse ::= ENUMERATED {
 *       unavailable (0), -- Not Equipped or unavailable
 *       notInUse (1), -- none active
 *       inUse (2),
 *       yellowCautionLights (3),
 *       schooldBusLights (4),
 *       arrowSignsActive (5),
 *       slowMovingVehicle (6),
 *       freqStops (7)
 *       }
 * 
 * Data Element: DE_MultiVehicleResponse
 * Use: DE_MultiVehicleResponse is a data element which is set if the vehicle transmitting believes that more 
 * than one vehicle (regardless of the dispatch or command and control organization of those vehicles or their 
 * agency) are currently en-route or involved in the response to the event. When received in a message by another 
 * vehicle OBU, this data element indicates to other vehicles that additional response vehicles may be converging
 * to the same location and that additional caution is warranted.
 * Used to indicate that more that one vehicle is responding and traveling in a closely aligned fashion (one after 
 * the other in a loose platoon formation). This DE is intended to be used with the DSRC “public safety vehicle 
 * operating in the area” use case.
 * ASN.1 Representation:
 *    MultiVehicleResponse ::= ENUMERATED {
 *       unavailable (0), -- Not Equipped or unavailable
 *       singleVehicle (1),
 *       multiVehicle (2),
 *       reserved (3) -- for future use
 *       }
 * 
 */
public class OssEmergencyDetailsTest {
    
    // sspRights tests
    /**
     * Test minimum ssp rights value (0) returns (0)
     */
    @Test
    public void shouldReturnMinimumSspRights() {
        
        Integer testInput = 0;
        Integer expectedValue = 0;
        
        SSPindex testSSPindex = new SSPindex(testInput);
        
        EmergencyDetails testEmergencyDetails = new EmergencyDetails();
        testEmergencyDetails.setSspRights(testSSPindex);
        testEmergencyDetails.setSirenUse(new SirenInUse(1));
        testEmergencyDetails.setLightsUse(new LightbarInUse(1));
        testEmergencyDetails.setMulti(new MultiVehicleResponse(1));
        testEmergencyDetails.setResponseType(new ResponseType(1));
        
        Integer actualValue = OssEmergencyDetails
                .genericEmergencyDetails(testEmergencyDetails)
                .getSspRights().intValue();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test known, middle ssp rights value (15) returns (15)
     */
    @Test
    public void shouldReturnMiddleSspRights() {
        
        Integer testInput = 15;
        Integer expectedValue = 15;
        
        SSPindex testSSPindex = new SSPindex(testInput);
        
        EmergencyDetails testEmergencyDetails = new EmergencyDetails();
        testEmergencyDetails.setSspRights(testSSPindex);
        testEmergencyDetails.setSirenUse(new SirenInUse(1));
        testEmergencyDetails.setLightsUse(new LightbarInUse(1));
        testEmergencyDetails.setMulti(new MultiVehicleResponse(1));
        testEmergencyDetails.setResponseType(new ResponseType(1));
        
        Integer actualValue = OssEmergencyDetails
                .genericEmergencyDetails(testEmergencyDetails)
                .getSspRights().intValue();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test maximum ssp rights value (31) returns (31)
     */
    @Test
    public void shouldReturnMaximumSspRights() {
        
        Integer testInput = 31;
        Integer expectedValue = 31;
        
        SSPindex testSSPindex = new SSPindex(testInput);
        
        EmergencyDetails testEmergencyDetails = new EmergencyDetails();
        testEmergencyDetails.setSspRights(testSSPindex);
        testEmergencyDetails.setSirenUse(new SirenInUse(1));
        testEmergencyDetails.setLightsUse(new LightbarInUse(1));
        testEmergencyDetails.setMulti(new MultiVehicleResponse(1));
        testEmergencyDetails.setResponseType(new ResponseType(1));
        
        Integer actualValue = OssEmergencyDetails
                .genericEmergencyDetails(testEmergencyDetails)
                .getSspRights().intValue();
        
        assertEquals(expectedValue, actualValue);
    }
    
    // sirenUse tests
    /**
     * Test siren use value (0) returns ("unavailable")
     */
    @Test
    public void shouldReturnSirenUseUnavailable() {
        
        Integer testInput = 0;
        String expectedValue = "unavailable";
        
        SirenInUse testSirenInUse = new SirenInUse(testInput);
        
        EmergencyDetails testEmergencyDetails = new EmergencyDetails();
        testEmergencyDetails.setSspRights(new SSPindex(1));
        testEmergencyDetails.setSirenUse(testSirenInUse);
        testEmergencyDetails.setLightsUse(new LightbarInUse(1));
        testEmergencyDetails.setMulti(new MultiVehicleResponse(1));
        testEmergencyDetails.setResponseType(new ResponseType(1));
        
        String actualValue = OssEmergencyDetails
                .genericEmergencyDetails(testEmergencyDetails)
                .getSirenUse().name();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test siren use value (1) returns ("notInUse")
     */
    @Test
    public void shouldReturnSirenUseNotInUse() {
        
        Integer testInput = 1;
        String expectedValue = "notInUse";
        
        SirenInUse testSirenInUse = new SirenInUse(testInput);
        
        EmergencyDetails testEmergencyDetails = new EmergencyDetails();
        testEmergencyDetails.setSspRights(new SSPindex(1));
        testEmergencyDetails.setSirenUse(testSirenInUse);
        testEmergencyDetails.setLightsUse(new LightbarInUse(1));
        testEmergencyDetails.setMulti(new MultiVehicleResponse(1));
        testEmergencyDetails.setResponseType(new ResponseType(1));
        
        String actualValue = OssEmergencyDetails
                .genericEmergencyDetails(testEmergencyDetails)
                .getSirenUse().name();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test siren use value (2) returns ("inUse")
     */
    @Test
    public void shouldReturnSirenUseInUse() {
        
        Integer testInput = 2;
        String expectedValue = "inUse";
        
        SirenInUse testSirenInUse = new SirenInUse(testInput);
        
        EmergencyDetails testEmergencyDetails = new EmergencyDetails();
        testEmergencyDetails.setSspRights(new SSPindex(1));
        testEmergencyDetails.setSirenUse(testSirenInUse);
        testEmergencyDetails.setLightsUse(new LightbarInUse(1));
        testEmergencyDetails.setMulti(new MultiVehicleResponse(1));
        testEmergencyDetails.setResponseType(new ResponseType(1));
        
        String actualValue = OssEmergencyDetails
                .genericEmergencyDetails(testEmergencyDetails)
                .getSirenUse().name();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test siren use value (3) returns ("reserved")
     */
    @Test
    public void shouldReturnSirenUseReserved() {
        
        Integer testInput = 3;
        String expectedValue = "reserved";
        
        SirenInUse testSirenInUse = new SirenInUse(testInput);
        
        EmergencyDetails testEmergencyDetails = new EmergencyDetails();
        testEmergencyDetails.setSspRights(new SSPindex(1));
        testEmergencyDetails.setSirenUse(testSirenInUse);
        testEmergencyDetails.setLightsUse(new LightbarInUse(1));
        testEmergencyDetails.setMulti(new MultiVehicleResponse(1));
        testEmergencyDetails.setResponseType(new ResponseType(1));
        
        String actualValue = OssEmergencyDetails
                .genericEmergencyDetails(testEmergencyDetails)
                .getSirenUse().name();
        
        assertEquals(expectedValue, actualValue);
    }
    
    // lightsUse tests
    /**
     * Test minimum lightbar in use value (0) returns ("unavailable")
     */
    @Test
    public void shouldReturnMinimumLightbarInUse() {
        
        Integer testInput = 0;
        String expectedValue = "UNAVAILABLE";
        
        LightbarInUse testLightbarInUse = new LightbarInUse(testInput);
        
        EmergencyDetails testEmergencyDetails = new EmergencyDetails();
        testEmergencyDetails.setSspRights(new SSPindex(1));
        testEmergencyDetails.setSirenUse(new SirenInUse(1));
        testEmergencyDetails.setLightsUse(testLightbarInUse);
        testEmergencyDetails.setMulti(new MultiVehicleResponse(1));
        testEmergencyDetails.setResponseType(new ResponseType(1));
        
        String actualValue = OssEmergencyDetails
                .genericEmergencyDetails(testEmergencyDetails)
                .getLightsUse().name();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case minimum lightbar in use value (1) returns ("notInUse")
     */
    @Test
    public void shouldReturnCornerCaseMinimumLightbarInUse() {
        
        Integer testInput = 1;
        String expectedValue = "NOTINUSE";
        
        LightbarInUse testLightbarInUse = new LightbarInUse(testInput);
        
        EmergencyDetails testEmergencyDetails = new EmergencyDetails();
        testEmergencyDetails.setSspRights(new SSPindex(1));
        testEmergencyDetails.setSirenUse(new SirenInUse(1));
        testEmergencyDetails.setLightsUse(testLightbarInUse);
        testEmergencyDetails.setMulti(new MultiVehicleResponse(1));
        testEmergencyDetails.setResponseType(new ResponseType(1));
        
        String actualValue = OssEmergencyDetails
                .genericEmergencyDetails(testEmergencyDetails)
                .getLightsUse().name();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test known, middle lightbar in use value (3) returns ("yellowCautionLights")
     */
    @Test
    public void shouldReturnMiddleLightbarInUse() {
        
        Integer testInput = 3;
        String expectedValue = "YELLOWCAUTIONLIGHTS";
        
        LightbarInUse testLightbarInUse = new LightbarInUse(testInput);
        
        EmergencyDetails testEmergencyDetails = new EmergencyDetails();
        testEmergencyDetails.setSspRights(new SSPindex(1));
        testEmergencyDetails.setSirenUse(new SirenInUse(1));
        testEmergencyDetails.setLightsUse(testLightbarInUse);
        testEmergencyDetails.setMulti(new MultiVehicleResponse(1));
        testEmergencyDetails.setResponseType(new ResponseType(1));
        
        String actualValue = OssEmergencyDetails
                .genericEmergencyDetails(testEmergencyDetails)
                .getLightsUse().name();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case maximum lightbar in use value (6) returns ("slowMovingVehicle")
     */
    @Test
    public void shouldReturnCornerCaseMaximumLightbarInUse() {
        
        Integer testInput = 6;
        String expectedValue = "SLOWMOVINGVEHICLE";
        
        LightbarInUse testLightbarInUse = new LightbarInUse(testInput);
        
        EmergencyDetails testEmergencyDetails = new EmergencyDetails();
        testEmergencyDetails.setSspRights(new SSPindex(1));
        testEmergencyDetails.setSirenUse(new SirenInUse(1));
        testEmergencyDetails.setLightsUse(testLightbarInUse);
        testEmergencyDetails.setMulti(new MultiVehicleResponse(1));
        testEmergencyDetails.setResponseType(new ResponseType(1));
        
        String actualValue = OssEmergencyDetails
                .genericEmergencyDetails(testEmergencyDetails)
                .getLightsUse().name();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test maximum lightbar in use value (7) returns ("freqStops")
     */
    @Test
    public void shouldReturnMaximumLightbarInUse() {
        
        Integer testInput = 7;
        String expectedValue = "FREQSTOPS";
        
        LightbarInUse testLightbarInUse = new LightbarInUse(testInput);
        
        EmergencyDetails testEmergencyDetails = new EmergencyDetails();
        testEmergencyDetails.setSspRights(new SSPindex(1));
        testEmergencyDetails.setSirenUse(new SirenInUse(1));
        testEmergencyDetails.setLightsUse(testLightbarInUse);
        testEmergencyDetails.setMulti(new MultiVehicleResponse(1));
        testEmergencyDetails.setResponseType(new ResponseType(1));
        
        String actualValue = OssEmergencyDetails
                .genericEmergencyDetails(testEmergencyDetails)
                .getLightsUse().name();
        
        assertEquals(expectedValue, actualValue);
    }

    // multi tests
    /**
     * Test multi vehicle response value (0) returns ("unavailable")
     */
    @Test
    public void shouldReturnMultiVehicleResponseUnavailable() {
        
        Integer testInput = 0;
        String expectedValue = "UNAVAILABLE";
        
        MultiVehicleResponse testMultiVehicleResponse = new MultiVehicleResponse(testInput);
        
        EmergencyDetails testEmergencyDetails = new EmergencyDetails();
        testEmergencyDetails.setSspRights(new SSPindex(1));
        testEmergencyDetails.setSirenUse(new SirenInUse(1));
        testEmergencyDetails.setLightsUse(new LightbarInUse(1));
        testEmergencyDetails.setMulti(testMultiVehicleResponse);
        testEmergencyDetails.setResponseType(new ResponseType(1));
        
        String actualValue = OssEmergencyDetails
                .genericEmergencyDetails(testEmergencyDetails)
                .getMulti().name();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test multi vehicle response value (1) returns ("singleVehicle")
     */
    @Test
    public void shouldReturnMultiVehicleResponseSingleVehicle() {
        
        Integer testInput = 1;
        String expectedValue = "SINGLEVEHICLE";
        
        MultiVehicleResponse testMultiVehicleResponse = new MultiVehicleResponse(testInput);
        
        EmergencyDetails testEmergencyDetails = new EmergencyDetails();
        testEmergencyDetails.setSspRights(new SSPindex(1));
        testEmergencyDetails.setSirenUse(new SirenInUse(1));
        testEmergencyDetails.setLightsUse(new LightbarInUse(1));
        testEmergencyDetails.setMulti(testMultiVehicleResponse);
        testEmergencyDetails.setResponseType(new ResponseType(1));
        
        String actualValue = OssEmergencyDetails
                .genericEmergencyDetails(testEmergencyDetails)
                .getMulti().name();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test multi vehicle response value (2) returns ("multiVehicle")
     */
    @Test
    public void shouldReturnMultiVehicleResponseMultiVehicle() {
        
        Integer testInput = 2;
        String expectedValue = "MULTIVEHICLE";
        
        MultiVehicleResponse testMultiVehicleResponse = new MultiVehicleResponse(testInput);
        
        EmergencyDetails testEmergencyDetails = new EmergencyDetails();
        testEmergencyDetails.setSspRights(new SSPindex(1));
        testEmergencyDetails.setSirenUse(new SirenInUse(1));
        testEmergencyDetails.setLightsUse(new LightbarInUse(1));
        testEmergencyDetails.setMulti(testMultiVehicleResponse);
        testEmergencyDetails.setResponseType(new ResponseType(1));
        
        String actualValue = OssEmergencyDetails
                .genericEmergencyDetails(testEmergencyDetails)
                .getMulti().name();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test multi vehicle response value (3) returns ("reserved")
     */
    @Test
    public void shouldReturnMultiVehicleResponseReserved() {
        
        Integer testInput = 3;
        String expectedValue = "RESERVED";
        
        MultiVehicleResponse testMultiVehicleResponse = new MultiVehicleResponse(testInput);
        
        EmergencyDetails testEmergencyDetails = new EmergencyDetails();
        testEmergencyDetails.setSspRights(new SSPindex(1));
        testEmergencyDetails.setSirenUse(new SirenInUse(1));
        testEmergencyDetails.setLightsUse(new LightbarInUse(1));
        testEmergencyDetails.setMulti(testMultiVehicleResponse);
        testEmergencyDetails.setResponseType(new ResponseType(1));
        
        String actualValue = OssEmergencyDetails
                .genericEmergencyDetails(testEmergencyDetails)
                .getMulti().name();
        
        assertEquals(expectedValue, actualValue);
    }
    
    // responseType tests
    /**
     * Test minimum response type value (0) returns ("notInUseOrNotEquipped")
     */
    @Test
    public void shouldReturnMinimumResponseType() {
        
        Integer testInput = 0;
        String expectedValue = "notInUseOrNotEquipped";
        
        ResponseType testResponseType = new ResponseType(testInput);
        
        EmergencyDetails testEmergencyDetails = new EmergencyDetails();
        testEmergencyDetails.setSspRights(new SSPindex(1));
        testEmergencyDetails.setSirenUse(new SirenInUse(1));
        testEmergencyDetails.setLightsUse(new LightbarInUse(1));
        testEmergencyDetails.setMulti(new MultiVehicleResponse(1));
        testEmergencyDetails.setResponseType(testResponseType);
        
        String actualValue = OssEmergencyDetails
                .genericEmergencyDetails(testEmergencyDetails)
                .getResponseType().name();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case minimum response type value (1) returns ("emergency")
     */
    @Test
    public void shouldReturnCornerCaseMinimumResponseType() {
        
        Integer testInput = 1;
        String expectedValue = "emergency";
        
        ResponseType testResponseType = new ResponseType(testInput);
        
        EmergencyDetails testEmergencyDetails = new EmergencyDetails();
        testEmergencyDetails.setSspRights(new SSPindex(1));
        testEmergencyDetails.setSirenUse(new SirenInUse(1));
        testEmergencyDetails.setLightsUse(new LightbarInUse(1));
        testEmergencyDetails.setMulti(new MultiVehicleResponse(1));
        testEmergencyDetails.setResponseType(testResponseType);
        
        String actualValue = OssEmergencyDetails
                .genericEmergencyDetails(testEmergencyDetails)
                .getResponseType().name();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test known, middle response type value (3) returns ("pursuit")
     */
    @Test
    public void shouldReturnMiddleResponseType() {
        
        Integer testInput = 3;
        String expectedValue = "pursuit";
        
        ResponseType testResponseType = new ResponseType(testInput);
        
        EmergencyDetails testEmergencyDetails = new EmergencyDetails();
        testEmergencyDetails.setSspRights(new SSPindex(1));
        testEmergencyDetails.setSirenUse(new SirenInUse(1));
        testEmergencyDetails.setLightsUse(new LightbarInUse(1));
        testEmergencyDetails.setMulti(new MultiVehicleResponse(1));
        testEmergencyDetails.setResponseType(testResponseType);
        
        String actualValue = OssEmergencyDetails
                .genericEmergencyDetails(testEmergencyDetails)
                .getResponseType().name();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case maximum response type value (5) returns ("slowMoving")
     */
    @Test
    public void shouldReturnCornerCaseMaximumResponseType() {
        
        Integer testInput = 5;
        String expectedValue = "slowMoving";
        
        ResponseType testResponseType = new ResponseType(testInput);
        
        EmergencyDetails testEmergencyDetails = new EmergencyDetails();
        testEmergencyDetails.setSspRights(new SSPindex(1));
        testEmergencyDetails.setSirenUse(new SirenInUse(1));
        testEmergencyDetails.setLightsUse(new LightbarInUse(1));
        testEmergencyDetails.setMulti(new MultiVehicleResponse(1));
        testEmergencyDetails.setResponseType(testResponseType);
        
        String actualValue = OssEmergencyDetails
                .genericEmergencyDetails(testEmergencyDetails)
                .getResponseType().name();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test maximum response type value (6) returns ("stopAndGoMovement")
     */
    @Test
    public void shouldReturnMaximumResponseType() {
        
        Integer testInput = 6;
        String expectedValue = "stopAndGoMovement";
        
        ResponseType testResponseType = new ResponseType(testInput);
        
        EmergencyDetails testEmergencyDetails = new EmergencyDetails();
        testEmergencyDetails.setSspRights(new SSPindex(1));
        testEmergencyDetails.setSirenUse(new SirenInUse(1));
        testEmergencyDetails.setLightsUse(new LightbarInUse(1));
        testEmergencyDetails.setMulti(new MultiVehicleResponse(1));
        testEmergencyDetails.setResponseType(testResponseType);
        
        String actualValue = OssEmergencyDetails
                .genericEmergencyDetails(testEmergencyDetails)
                .getResponseType().name();
        
        assertEquals(expectedValue, actualValue);
    }

}
