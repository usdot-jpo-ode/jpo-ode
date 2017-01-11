package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.oss.asn1.OpenType;
import com.oss.asn1.PERUnalignedCoder;

import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.dsrc.BasicSafetyMessage.PartII.Sequence_;
import us.dot.its.jpo.ode.j2735.dsrc.BasicVehicleClass;
import us.dot.its.jpo.ode.j2735.dsrc.Confidence;
import us.dot.its.jpo.ode.j2735.dsrc.EmergencyDetails;
import us.dot.its.jpo.ode.j2735.dsrc.EventDescription;
import us.dot.its.jpo.ode.j2735.dsrc.ExteriorLights;
import us.dot.its.jpo.ode.j2735.dsrc.LightbarInUse;
import us.dot.its.jpo.ode.j2735.dsrc.MultiVehicleResponse;
import us.dot.its.jpo.ode.j2735.dsrc.PartII_Id;
import us.dot.its.jpo.ode.j2735.dsrc.PathPrediction;
import us.dot.its.jpo.ode.j2735.dsrc.RadiusOfCurvature;
import us.dot.its.jpo.ode.j2735.dsrc.SSPindex;
import us.dot.its.jpo.ode.j2735.dsrc.SirenInUse;
import us.dot.its.jpo.ode.j2735.dsrc.SpecialVehicleExtensions;
import us.dot.its.jpo.ode.j2735.dsrc.SupplementalVehicleExtensions;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleEventFlags;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleSafetyExtensions;
import us.dot.its.jpo.ode.j2735.itis.ITIScodes;
import us.dot.its.jpo.ode.plugin.j2735.J2735SpecialVehicleExtensions;
import us.dot.its.jpo.ode.plugin.j2735.J2735SupplementalVehicleExtensions;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleSafetyExtensions;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssBsmPart2Content.OssBsmPart2Exception;

/**
 * -- Summary --
 * JUnit test class for OssBsmPart2Content
 * 
 * Verifies OssBsmPart2Content can create a generic J2735BsmPart2Content object of one of three types
 * 
 * More complete tests for SupplementalVehicleExtensions can be found in OssSupplementalVehicleExtensionsTest and 
 * OssBsmPart2ExtensionTest
 * 
 * -- Documentation --
 * Data Frame: DF_SpecialVehicleExtensions
 * Use: The DF_SpecialVehicleExtensions data frame is used to send various additional optional information 
 * elements in the Part II BSM used by special vehicles. In this context, the term "special" indicates vehicles 
 * or other equipped devices which differ from other vehicles in their overall ability or intent to flow in 
 * traffic and which are likely to have additional certification permissions (CERTs) which expressly allow 
 * this information to be sent. As a broad rule, light passenger vehicles (when in non special roles) will 
 * not send this type of content. A typical use case would be a police vehicle, actively engaged in a police 
 * vehicle role, sending additional information (the Emergency Details data frame) about its flashing lights 
 * and immediate movements. An alternative use case would be a garbage truck engaged in stop and go operations 
 * (irregular vehicle movements) sending the same data frame with different internal content details. A further 
 * example use case would be an equipped heavy truck sending content about the trailer it was hauling.
 * ASN.1 Representation:
 *    SpecialVehicleExtensions ::= SEQUENCE { -- The content below requires SSP permissions to transmit
 *       -- The entire EVA message has been reduced to these items
 *       vehicleAlerts EmergencyDetails OPTIONAL, -- Description or Direction from an emergency vehicle
 *       description EventDescription OPTIONAL, -- short ITIS description
 *       -- Trailers for both passenger vehicles and heavy trucks
 *       trailers TrailerData OPTIONAL,
 *       -- HAZMAT and Cargo details to be added in a future revision
 *       -- Wideload, oversized load to be added in a future revision
 *       ...
 *       }
 *       
 * Data Frame: DF_VehicleSafetyExtensions
 * Use: The DF_VehicleSafetyExtensions data frame is used to send various additional details about the vehicle. 
 * This data frame is used for vehicle safety applications to exchange safety information such as event flag and 
 * detailed positional information. This data frame is typically sent in conjunction with BSM Part I or used in 
 * other messages at the same or reduced frequency.
 * ASN.1 Representation:
 *    VehicleSafetyExtensions ::= SEQUENCE {
 *       events VehicleEventFlags OPTIONAL,
 *       pathHistory PathHistory OPTIONAL,
 *       pathPrediction PathPrediction OPTIONAL,
 *       lights ExteriorLights OPTIONAL,
 *       ...
 *       }
 */
public class OssBsmPart2ContentTest {
    
    /**
     * Create a test SpecialVehicleExtensions object with minimal contents and verify correct conversion
     */
    @Test
    public void shouldCreateSpecialVehicleExtensions() {
        
        SpecialVehicleExtensions testsve = new SpecialVehicleExtensions();
        
        /*
        EmergencyDetails ::= SEQUENCE { -- CERT SSP Privilege Details
            sspRights SSPindex, -- index set by CERT
            sirenUse SirenInUse,
            lightsUse LightbarInUse,
            multi MultiVehicleResponse,
            events PrivilegedEvents OPTIONAL,
            responseType ResponseType OPTIONAL,
            ...
            }
        */
        Integer testSspRights = 5;
        Integer expectedSspRights = 5;
        
        Integer testSirenInUse = 2;
        String expectedSirenInUse = "inUse";
        
        Integer testLightbarInUse = 3;
        String expectedLightbarInUse = "yellowCautionLights";
        
        Integer testMultiVehicleResponse = 2;
        String expectedMultiVehicleResponse = "multiVehicle";
        
        EmergencyDetails testEmergencyDetails = new EmergencyDetails(
                new SSPindex(testSspRights),
                new SirenInUse(testSirenInUse),
                new LightbarInUse(testLightbarInUse),
                new MultiVehicleResponse(testMultiVehicleResponse));
        
        testsve.setVehicleAlerts(testEmergencyDetails);
        
        // EventDescription
        Integer testTypeEvent = 12312;
        Integer expectedTypeEvent = 12312;
        
        EventDescription testEventDescription = new EventDescription(
                new ITIScodes(testTypeEvent));
        
        testsve.setDescription(testEventDescription);
        
        // Create a BasicSafetyMessage.PartII by encoding the SpecialVehicleExtensions object
        PERUnalignedCoder coder = J2735.getPERUnalignedCoder();
        
        Sequence_ testSequence = new Sequence_();
        testSequence.partII_Id = new PartII_Id(1);
        try {
            testSequence.setPartII_Value(new OpenType(coder.encode(testsve).array()));
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getClass());
        }
        
        //BasicSafetyMessage.PartII testPart2 = new BasicSafetyMessage.PartII(new Sequence_[]{testSequence});
        
        J2735SpecialVehicleExtensions actualValue = null;
        try {
            actualValue = (J2735SpecialVehicleExtensions) OssBsmPart2Content.genericPart2Content(testSequence).value;
            assertNotNull("J2735BsmPart2Content null", actualValue);
        } catch (OssBsmPart2Exception e) {
            fail("Unexpected exception: " + e.getClass());
        }
        
        assertEquals("Incorrect SSPrights", expectedSspRights, actualValue.vehicleAlerts.sspRights);
        assertEquals("Incorrect SirenInUse", expectedSirenInUse, actualValue.vehicleAlerts.sirenUse.name());
        assertEquals("Incorrect LightbarInUse", expectedLightbarInUse, actualValue.vehicleAlerts.lightsUse.name());
        assertEquals("Incorrect MultiVehicleResponse", expectedMultiVehicleResponse, actualValue.vehicleAlerts.multi.name());
        assertEquals("Incorrect EventType", expectedTypeEvent, actualValue.description.typeEvent);
    }
    
    
    @Test
    public void shouldCreateSupplementalVehicleExtensions() {
        
        Integer testBasicVehicleClass = 27;
        Integer expectedBasicVehicleClass = 27;
        
        SupplementalVehicleExtensions testsve = new SupplementalVehicleExtensions();
        testsve.setClassification(new BasicVehicleClass(testBasicVehicleClass));
        
        // Create a BasicSafetyMessage.PartII by encoding the SupplementalVehicleExtensions object
        PERUnalignedCoder coder = J2735.getPERUnalignedCoder();

        Sequence_ testSequence = new Sequence_();
        testSequence.partII_Id = new PartII_Id(2);

        try {
            testSequence.setPartII_Value(new OpenType(coder.encode(testsve).array()));
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getClass());
        }

        J2735SupplementalVehicleExtensions actualValue = null;
        try {
            actualValue = (J2735SupplementalVehicleExtensions) OssBsmPart2Content.genericPart2Content(testSequence).value;
            assertNotNull("J2735BsmPart2Content null", actualValue);
        } catch (OssBsmPart2Exception e) {
            fail("Unexpected exception: " + e.getClass());
        }
        assertEquals(expectedBasicVehicleClass, actualValue.classification);
        
    }
    
    /**
     * Create a test VehicleSafetyExtensions object with minimal contents and verify correct conversion
     */
    @Ignore
    @Test
    public void shouldCreateVehicleSafetyExtensions () {
        
        // VehicleEventFlags
        Integer testVehicleEventFlags = 0b1;
        String expectedVehicleEventFlag1 = "eventHazardLights";
        String expectedVehicleEventFlag2 = "eventHazardousMaterials";
        
        // PathPrediction
        Integer testRadiusOfCurvature = 435;
        BigDecimal expectedRadiusOfCurvature = BigDecimal.valueOf(43.5);
        
        Integer testConfidence = 56;
        BigDecimal expectedConfidence = BigDecimal.valueOf(28.0);
        
        // ExteriorLights
        Integer testExteriorLights = 0b000100010;
        String expectedExteriorLights1 = "leftTurnSignalOn";
        String expectedExteriorLights2 = "daytimeRunningLightsOn";
        
        VehicleSafetyExtensions testvse = new VehicleSafetyExtensions();
        
        testvse.setEvents(new VehicleEventFlags(new byte[]{testVehicleEventFlags.byteValue()}));
        testvse.setPathPrediction(
                new PathPrediction(
                        new RadiusOfCurvature(testRadiusOfCurvature), 
                        new Confidence(testConfidence)));
        testvse.setLights(new ExteriorLights(new byte[]{testExteriorLights.byteValue()}));
        
        // Create a BasicSafetyMessage.PartII by encoding the VehicleSafetyExtensions object
        PERUnalignedCoder coder = J2735.getPERUnalignedCoder();
        
        Sequence_ testSequence = new Sequence_();
        testSequence.partII_Id = new PartII_Id(0);
        try {
            testSequence.setPartII_Value(new OpenType(coder.encode(testvse).array()));
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getClass());
        }
        
        J2735VehicleSafetyExtensions actualValue = null;
        try {
            actualValue = (J2735VehicleSafetyExtensions) OssBsmPart2Content.genericPart2Content(testSequence).value;
            assertNotNull("J2735BsmPart2Content null", actualValue);
        } catch (OssBsmPart2Exception e) {
            fail("Unexpected exception: " + e.getClass());
        }
        assertEquals("Incorrect radius of curvature", expectedRadiusOfCurvature, actualValue.pathPrediction.radiusOfCurve);
        assertEquals("Incorrect confidence", expectedConfidence, actualValue.pathPrediction.confidence);
        
        System.out.println("[Test] # elements =" + actualValue.events.entrySet().size());
        for (Map.Entry<String, Boolean> curVal1 : actualValue.events.entrySet()) {
            System.out.println("[Test] key=" + curVal1.getKey() + ", status=" + curVal1.getValue());
            if (curVal1.getKey().equals(expectedVehicleEventFlag1) || curVal1.getKey().equals(expectedVehicleEventFlag2)) {
                assertTrue("Expected " + curVal1.getKey() + " to be true", curVal1.getValue());
            } else {
                assertFalse("Expected " + curVal1.getKey() + " to be false", curVal1.getValue());
            }
        }
        

        for (Map.Entry<String, Boolean> curVal2 : actualValue.lights.entrySet()) {
            if (curVal2.getKey().equals(expectedExteriorLights1) || curVal2.getKey().equals(expectedExteriorLights2)) {
                assertTrue("Expected " + curVal2.getKey() + " to be true", curVal2.getValue());
            } else {
                assertFalse("Expected " + curVal2.getKey() + " to be false", curVal2.getValue());
            }
        }
    }

}
