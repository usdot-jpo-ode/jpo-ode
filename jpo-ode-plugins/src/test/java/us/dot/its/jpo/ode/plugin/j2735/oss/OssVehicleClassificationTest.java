package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.BasicVehicleClass;
import us.dot.its.jpo.ode.j2735.dsrc.BasicVehicleRole;
import us.dot.its.jpo.ode.j2735.dsrc.FuelType;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleClassification;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleType;
import us.dot.its.jpo.ode.j2735.itis.ResponderGroupAffected;
import us.dot.its.jpo.ode.j2735.itis.VehicleGroupAffected;

/**
 * -- Summary --
 * JUnit test class for OssVehicleClassification
 * 
 * Verifies correct conversion from generic VehicleClassification object to compliant-J2735VehicleClassification
 * 
 * Notes:
 * - ResponseEquipment is tested by OssNamedNumberTest
 * - VehicleType is tested by OssNamedNumberTest
 * - RegionalExtension is specific to use case and is not tested
 * 
 * -- Documentation --
 * Data Frame: DF_VehicleClassification
 * Use: The DF_VehicleClassification data frame is a structure with a composite set of common classification systems 
 * used in ITS and DSRC work. There are any number of such 'types' that can be used to classify a vehicle based on 
 * different systems and needs. A given use case will typically use only a subset of the items noted below.
 * ASN.1 Representation:
 *    VehicleClassification ::= SEQUENCE { 
 *       -- Composed of the following elements:
 *       -- The 'master' DSRC list used when space is limited
 *       keyType BasicVehicleClass OPTIONAL,
 *       -- Types used in the MAP/SPAT/SSR/SRM exchanges
 *       role BasicVehicleRole OPTIONAL, -- Basic CERT role at a given time
 *       iso3883 Iso3833VehicleType OPTIONAL,
 *       hpmsType VehicleType OPTIONAL, -- HPMS classification types
 *       -- ITIS types for classes of vehicle and agency
 *       vehicleType ITIS.VehicleGroupAffected OPTIONAL,
 *       responseEquip ITIS.IncidentResponseEquipment OPTIONAL,
 *       responderType ITIS.ResponderGroupAffected OPTIONAL,
 *       -- Fuel types for vehicles
 *       fuelType FuelType OPTIONAL,
 *       regional SEQUENCE (SIZE(1..4)) OF RegionalExtension {{REGION.Reg-VehicleClassification}} OPTIONAL,
 *       ...
 *       }
 * 
 * Data Element: DE_BasicVehicleClass
 * Use: The BasicVehicleClass data element is used to provide a common classification system to categorize DSRC- 
 * equipped devices for various cross-cutting uses. Several other classification systems in this data dictionary 
 * can be used to provide more domain specific detail when required.
 *    ASN.1 Representation:
 *       BasicVehicleClass ::= INTEGER (0..255)
 *       unknownVehicleClass BasicVehicleClass ::= 0
 *       -- Not Equipped, Not known or unavailable
 *       specialVehicleClass BasicVehicleClass ::= 1
 *       -- Special use
 *       --
 *       -- Basic Passenger Motor Vehicle Types
 *       --
 *       passenger-Vehicle-TypeUnknown BasicVehicleClass ::= 10 -- default type
 *       passenger-Vehicle-TypeOther BasicVehicleClass ::= 11
 *       [...]
 *       -- Other DSRC Equipped Device Types
 *       --
 *       infrastructure-TypeUnknown BasicVehicleClass ::= 90 -- default type
 *       infrastructure-Fixed BasicVehicleClass ::= 91
 *       infrastructure-Movable BasicVehicleClass ::= 92
 *       equipped-CargoTrailer BasicVehicleClass ::= 93
 * 
 * Data Element: DE_BasicVehicleRole
 * Use: The BasicVehicleRole data element provides a means to indicate the current role that a DSRC device is playing. 
 * This is most commonly employed when a vehicle needs to take on another role in order to send certain DSRC message 
 * types. As an example, when a public safety vehicle such as a police car wishes to send a signal request message 
 * (SRM) to an intersection to request a preemption service, the vehicle takes on the role "police" from the below 
 * list in both the SRM message itself and also in the type of security CERT which is sent (the SSP in the CERT it 
 * used to identify the requester as being of type "police" and that they are allowed to send this message in this 
 * way). The BasicVehicleRole entry is often used and combined with other information about the requester as well, 
 * such as details of why the request is being made.
 * ASN.1 Representation:
 *    BasicVehicleRole ::= ENUMERATED {
 *       -- Values used in the EU and in the US
 *       basicVehicle (0), -- Light duty passenger vehicle type
 *       publicTransport (1), -- Used in EU for Transit us
 *       [...]
 *       motorcycle (10), --
 *       [...]
 *       pedestrian (20), -- also includes those with mobility limitations
 *       nonMotorized (21), -- other, horse drawn, etc.
 *       military (22), --
 *       ...
 *       }
 * 
 * 
 * [Tested by OssNamedNumberTest] Data Element: DE_Iso3833VehicleType
 * Use: The DE_Iso3833VehicleType data element represents the value domain provided by ISO 3833 for general vehicle 
 * types. It is a European list similar to the list used for the Highway Performance Monitoring System (HPMS) in 
 * the US region. In this standard, the HPMS list is used in the data concept named VehicleType.
 * ASN.1 Representation:
 *    Iso3833VehicleType ::= INTEGER (0..100)
 *    
 * Data Element: DE_VehicleType
 * Use: The DE_VehicleType data element is a type list (i.e., a classification list) of the vehicle in terms of 
 * overall size. The data element entries follow the definitions defined in the US DOT Highway Performance 
 * Monitoring System (HPMS). Many infrastructure roadway operators collect and classify data according to this 
 * list for regulatory reporting needs. Within the ITS industry and within the DSRC message set standards work, 
 * there are many similar lists of types for overlapping needs and uses.
 * ASN.1 Representation:
 *    VehicleType ::= ENUMERATED {
 *       none (0), -- Not Equipped, Not known or unavailable
 *       unknown (1), -- Does not fit any other category
 *       [...]
 *       bus (6), -- Buses
 *       [...]
 *       axleCnt6MultiTrailer (14), -- Six axle, multi-trailer
 *       axleCnt7MultiTrailer (15), -- Seven or more axle, multi-trailer
 *       ...
 *       }
 * 
 * Data Element: DE_Vehicle Groups Affected [ITIS]
 * Use: The ITIS enumeration list commonly referred to as "Vehicle Groups Affected" is assigned the upper octet 
 * value of [36], which provides for value ranges from 9216 to 9471, inclusive. This list is formally called 
 * "VehicleGroupAffected" in the ASN.1 and XML productions. Items from this enumeration list can be used as an 
 * event category classification. This list contains a total of 35 different phrases. The remaining 92 values 
 * up to the lower octet value of [127] are reserved for additional "national" phrases in this octet range. Local 
 * phrases may be added to the list starting with the lower octet value of 128 and proceeding upward from there, 
 * i.e., the first value assigned for any local additions to this list would be given the value 9344.
 * ASN.1 Representation:
 *    VehicleGroupAffected ::= ENUMERATED {
 *       all-vehicles (9217),
 *       bicycles (9218),
 *       motorcycles (9219), -- to include mopeds as well
 *       lPG-vehicles (9249), -- The L is lower case here
 *       military-convoys (9250),
 *       military-vehicles (9251),
 *       ... -- # LOCAL_CONTENT_ITIS
 *       } -- Classification of vehicles and types of transport
 * 
 * [Tested by OssNamedNumberTest] Data Element: DE_Incident Response Equipment [ITIS]
 * Use: The ITIS enumeration list commonly refered to as "Incident Response Equipment" is assigned the upper 
 * octet value of [39], which provides for value ranges from 9984 to 10239, inclusive. This list is formally 
 * called "IncidentResponseEquipment" in the ASN.1 and XML productions. The items in this enumeration list are 
 * not allowed to be used as an event category classification. This list contains a total of 72 different phrases. 
 * The remaining 55 values up to the lower octet value of [127] are reserved for additional "national" phrases 
 * in this octet range. Local phrases may be added to the list starting with the lower octet value of 128 and 
 * proceeding upward from there, i.e., the first value assigned for any local additions to this list would be 
 * given the value 10112.
 * ASN.1 Representation:
 *    IncidentResponseEquipment ::= ENUMERATED {
 *       ground-fire-suppression (9985),
 *       heavy-ground-equipment (9986),
 *       aircraft (9988),
 *       [shortened]
 *       rotary-snow-blower (10111),
 *       road-grader (10112), -- Alternative term: motor grader
 *       steam-truck (10113), -- A special truck that thaws culverts
 *       -- and storm drains
 *       ... -- # LOCAL_CONTENT_ITIS
 *       }
 * 
 * Data Element: DE_Responder Group Affected [ITIS]
 * Use: The ITIS enumeration list commonly refered to as "Responder Group Affected" is assigned the upper 
 * octet value of [38], which provides for value ranges from 9728 to 9983, inclusive. This list is formally 
 * called "ResponderGroupAffected" in the ASN.1 and XML productions. Items from this enumeration list can be 
 * used as an event category classification. This list contains a total of 14 different phrases. The remaining 
 * 113 values up to the lower octet value of [127] are reserved for additional "national" phrases in this octet 
 * range. Local phrases may be added to the list starting with the lower octet value of 128 and proceeding upward 
 * from there, i.e., the first value assigned for any local additions to this list would be given the value 9856.
 * ASN.1 Representation:
 *    ResponderGroupAffected ::= ENUMERATED {
 *       emergency-vehicle-units (9729), -- Default, to be used when one of
 *       -- the below does not fit better
 *       federal-law-enforcement-units (9730),
 *       state-police-units (9731),
 *       [shortened]
 *       transportation-response-units (9741),
 *       private-contractor-response-units (9742),
 *       ... -- # LOCAL_CONTENT_ITIS
 *       } -- These groups are used in coordinated response and staging area information 
 *       -- (rather than typically consumer related)
 * 
 * Data Element: DE_FuelType
 * Use: This data element provides the type of fuel used by a vehicle.
 * ASN.1 Representation:
 *    FuelType ::= INTEGER (0..15)
 *       unknownFuel FuelType::= 0 -- Gasoline Powered
 *       gasoline FuelType::= 1
 *       ethanol FuelType::= 2 -- Including blends
 *       diesel FuelType::= 3 -- All types
 *       electric FuelType::= 4
 *       hybrid FuelType::= 5 -- All types
 *       hydrogen FuelType::= 6
 *       natGasLiquid FuelType::= 7 -- Liquefied
 *       natGasComp FuelType::= 8 -- Compressed
 *       propane FuelType::= 9
 */
public class OssVehicleClassificationTest {
    
    // BasicVehicleClass tests
    /**
     * Test that the minimum basic vehicle class value (0) returns (0)
     */
    @Test
    public void shouldReturnMinimumBasicVehicleClass() {
        
        Integer testInput = 0;
        Integer expectedValue = 0;
        
        BasicVehicleClass testBasicVehicleClass = new BasicVehicleClass(testInput);
        
        VehicleClassification testvc = new VehicleClassification();
        testvc.setKeyType(testBasicVehicleClass);
        
        Integer actualValue = OssVehicleClassification
                .genericVehicleClassification(testvc).getKeyType();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case minimum basic vehicle class value (1) returns (1)
     */
    @Test
    public void shouldReturnCornerCaseMinimumBasicVehicleClass() {
        
        Integer testInput = 1;
        Integer expectedValue = 1;
        
        BasicVehicleClass testBasicVehicleClass = new BasicVehicleClass(testInput);
        
        VehicleClassification testvc = new VehicleClassification();
        testvc.setKeyType(testBasicVehicleClass);
        
        Integer actualValue = OssVehicleClassification
                .genericVehicleClassification(testvc).getKeyType();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test known, middle basic vehicle class value (127) returns (127)
     */
    @Test
    public void shouldReturnMiddleBasicVehicleClass() {
        
        Integer testInput = 127;
        Integer expectedValue = 127;
        
        BasicVehicleClass testBasicVehicleClass = new BasicVehicleClass(testInput);
        
        VehicleClassification testvc = new VehicleClassification();
        testvc.setKeyType(testBasicVehicleClass);
        
        Integer actualValue = OssVehicleClassification
                .genericVehicleClassification(testvc).getKeyType();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case maximum basic vehicle class value (254) returns (254)
     */
    @Test
    public void shouldReturnCornerCaseMaximumBasicVehicleClass() {
        
        Integer testInput = 254;
        Integer expectedValue = 254;
        
        BasicVehicleClass testBasicVehicleClass = new BasicVehicleClass(testInput);
        
        VehicleClassification testvc = new VehicleClassification();
        testvc.setKeyType(testBasicVehicleClass);
        
        Integer actualValue = OssVehicleClassification
                .genericVehicleClassification(testvc).getKeyType();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test maximum basic vehicle class value (255) returns (255)
     */
    @Test
    public void shouldReturnMaximumBasicVehicleClass() {
        
        Integer testInput = 255;
        Integer expectedValue = 255;
        
        BasicVehicleClass testBasicVehicleClass = new BasicVehicleClass(testInput);
        
        VehicleClassification testvc = new VehicleClassification();
        testvc.setKeyType(testBasicVehicleClass);
        
        Integer actualValue = OssVehicleClassification
                .genericVehicleClassification(testvc).getKeyType();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test basic vehicle class value (-1) below the lower bound (0) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionBasicVehicleClassBelowLowerBound() {
        
        Integer testInput = -1;
        
        BasicVehicleClass testBasicVehicleClass = new BasicVehicleClass(testInput);
        
        VehicleClassification testvc = new VehicleClassification();
        testvc.setKeyType(testBasicVehicleClass);
        
        try {
           OssVehicleClassification
                   .genericVehicleClassification(testvc).getKeyType();
           fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    /**
     * Test basic vehicle class value (256) above the upper bound (255) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionBasicVehicleClassAboveUpperBound() {
        
        Integer testInput = 256;
        
        BasicVehicleClass testBasicVehicleClass = new BasicVehicleClass(testInput);
        
        VehicleClassification testvc = new VehicleClassification();
        testvc.setKeyType(testBasicVehicleClass);
        
        try {
           OssVehicleClassification
                   .genericVehicleClassification(testvc).getKeyType();
           fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    // BasicVehicleRole tests
    /**
     * Test minimum basic vehicle role value (0) returns ("basicVehicle")
     */
    @Test
    public void shouldReturnMinimumBasicVehicleRole() {
        
        Integer testInput = 0;
        String expectedValue = "basicVehicle";
        
        BasicVehicleRole testBasicVehicleRole = new BasicVehicleRole(testInput);
        
        VehicleClassification testvc = new VehicleClassification();
        testvc.setRole(testBasicVehicleRole);
        
        String actualValue = OssVehicleClassification
                .genericVehicleClassification(testvc)
                .getRole().toString();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case minimum basic vehicle role value (1) returns ("publicTransport")
     */
    @Test
    public void shouldReturnCornerCaseMinimumBasicVehicleRole() {
        
        Integer testInput = 1;
        String expectedValue = "publicTransport";
        
        BasicVehicleRole testBasicVehicleRole = new BasicVehicleRole(testInput);
        
        VehicleClassification testvc = new VehicleClassification();
        testvc.setRole(testBasicVehicleRole);
        
        String actualValue = OssVehicleClassification
                .genericVehicleClassification(testvc)
                .getRole().toString();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test known, middle basic vehicle role value (10) returns ("motorcycle")
     */
    @Test
    public void shouldReturnMiddleBasicVehicleRole() {
        
        Integer testInput = 10;
        String expectedValue = "motorcycle";
        
        BasicVehicleRole testBasicVehicleRole = new BasicVehicleRole(testInput);
        
        VehicleClassification testvc = new VehicleClassification();
        testvc.setRole(testBasicVehicleRole);
        
        String actualValue = OssVehicleClassification
                .genericVehicleClassification(testvc)
                .getRole().toString();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case maximum basic vehicle role value (21) returns ("nonMotorized")
     */
    @Test
    public void shouldReturnCornerCaseMaximumBasicVehicleRole() {
        
        Integer testInput = 21;
        String expectedValue = "nonMotorized";
        
        BasicVehicleRole testBasicVehicleRole = new BasicVehicleRole(testInput);
        
        VehicleClassification testvc = new VehicleClassification();
        testvc.setRole(testBasicVehicleRole);
        
        String actualValue = OssVehicleClassification
                .genericVehicleClassification(testvc)
                .getRole().toString();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test maximum basic vehicle role value (22) returns ("military")
     */
    @Test
    public void shouldReturnMaximumBasicVehicleRole() {
        
        Integer testInput = 22;
        String expectedValue = "military";
        
        BasicVehicleRole testBasicVehicleRole = new BasicVehicleRole(testInput);
        
        VehicleClassification testvc = new VehicleClassification();
        testvc.setRole(testBasicVehicleRole);
        
        String actualValue = OssVehicleClassification
                .genericVehicleClassification(testvc)
                .getRole().toString();
        
        assertEquals(expectedValue, actualValue);
    }
    
    // VehicleType tests
    /**
     * Test minimum vehicle type value (0) returns ("none")
     */
    @Test
    public void shouldReturnMinimumVehicleType() {
        
        Integer testInput = 0;
        String expectedValue = "none";
        
        VehicleType testVehicleType = new VehicleType(testInput);
        
        VehicleClassification testvc = new VehicleClassification();
        testvc.setHpmsType(testVehicleType);
        
        String actualValue = OssVehicleClassification
                .genericVehicleClassification(testvc)
                .getHpmsType().toString();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case minimum vehicle type value (1) returns ("unknown")
     */
    @Test
    public void shouldReturnCornerCaseMinimumVehicleType() {
        
        Integer testInput = 1;
        String expectedValue = "unknown";
        
        VehicleType testVehicleType = new VehicleType(testInput);
        
        VehicleClassification testvc = new VehicleClassification();
        testvc.setHpmsType(testVehicleType);
        
        String actualValue = OssVehicleClassification
                .genericVehicleClassification(testvc)
                .getHpmsType().toString();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test known, middle vehicle type value (6) returns ("bus")
     */
    @Test
    public void shouldReturnMiddleVehicleType() {
        
        Integer testInput = 6;
        String expectedValue = "bus";
        
        VehicleType testVehicleType = new VehicleType(testInput);
        
        VehicleClassification testvc = new VehicleClassification();
        testvc.setHpmsType(testVehicleType);
        
        String actualValue = OssVehicleClassification
                .genericVehicleClassification(testvc)
                .getHpmsType().toString();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case maximum vehicle type value (14) returns ("axleCnt6MultiTrailer")
     */
    @Test
    public void shouldReturnCornerCaseMaximumVehicleType() {
        
        Integer testInput = 14;
        String expectedValue = "axleCnt6MultiTrailer";
        
        VehicleType testVehicleType = new VehicleType(testInput);
        
        VehicleClassification testvc = new VehicleClassification();
        testvc.setHpmsType(testVehicleType);
        
        String actualValue = OssVehicleClassification
                .genericVehicleClassification(testvc)
                .getHpmsType().toString();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test maximum vehicle type value (15) returns ("axleCnt7MultiTrailer")
     */
    @Test
    public void shouldReturnMaximumVehicleType() {
        
        Integer testInput = 15;
        String expectedValue = "axleCnt7MultiTrailer";
        
        VehicleType testVehicleType = new VehicleType(testInput);
        
        VehicleClassification testvc = new VehicleClassification();
        testvc.setHpmsType(testVehicleType);
        
        String actualValue = OssVehicleClassification
                .genericVehicleClassification(testvc)
                .getHpmsType().toString();
        
        assertEquals(expectedValue, actualValue);
    }
    
    // VehicleGroupsAffected tests
    /**
     * Test minimum vehicle group affected value (9217) returns ("all-vehicles")
     */
    @Test
    public void shouldReturnMinimumVehicleGroupAffected() {
        
        Integer testInput = 9217;

        String expectedName = "all-vehicles";
        Long expectedValue = 9217L;
        
        VehicleGroupAffected testVehicleGroupAffected = new VehicleGroupAffected(testInput);
        
        VehicleClassification testcv = new VehicleClassification();
        testcv.setVehicleType(testVehicleGroupAffected);
        
        String actualName = OssVehicleClassification
                .genericVehicleClassification(testcv)
                .getVehicleType().getName();
        Long actualValue = OssVehicleClassification
                .genericVehicleClassification(testcv)
                .getVehicleType().getValue();
        
        assertEquals(expectedName, actualName);
        assertEquals(expectedValue, actualValue);
    }
    /**
     * Test corner case minimum vehicle group affected value (9218) returns ("bicycles")
     */
    @Test
    public void shouldReturnCornerCaseMinimumVehicleGroupAffected() {
        
        Integer testInput = 9218;

        String expectedName = "bicycles";
        Long expectedValue = 9218L;
        
        VehicleGroupAffected testVehicleGroupAffected = new VehicleGroupAffected(testInput);
        
        VehicleClassification testcv = new VehicleClassification();
        testcv.setVehicleType(testVehicleGroupAffected);
        
        String actualName = OssVehicleClassification
                .genericVehicleClassification(testcv)
                .getVehicleType().getName();
        Long actualValue = OssVehicleClassification
                .genericVehicleClassification(testcv)
                .getVehicleType().getValue();
        
        assertEquals(expectedName, actualName);
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test known, middle vehicle group affected value (9219) returns ("motorcycles")
     */
    @Test
    public void shouldReturnMiddleVehicleGroupAffected() {
        
        Integer testInput = 9219;

        String expectedName = "motorcycles";
        Long expectedValue = 9219L;
        
        VehicleGroupAffected testVehicleGroupAffected = new VehicleGroupAffected(testInput);
        
        VehicleClassification testcv = new VehicleClassification();
        testcv.setVehicleType(testVehicleGroupAffected);
        
        String actualName = OssVehicleClassification
                .genericVehicleClassification(testcv)
                .getVehicleType().getName();
        Long actualValue = OssVehicleClassification
                .genericVehicleClassification(testcv)
                .getVehicleType().getValue();
        
        assertEquals(expectedName, actualName);
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case maximum vehicle group affected value (9250) returns ("military-convoys")
     */
    @Test
    public void shouldReturnCornerCaseMaximumVehicleGroupAffected() {
        
        Integer testInput = 9250;

        String expectedName = "military-convoys";
        Long expectedValue = 9250L;
        
        VehicleGroupAffected testVehicleGroupAffected = new VehicleGroupAffected(testInput);
        
        VehicleClassification testcv = new VehicleClassification();
        testcv.setVehicleType(testVehicleGroupAffected);
        
        String actualName = OssVehicleClassification
                .genericVehicleClassification(testcv)
                .getVehicleType().getName();
        Long actualValue = OssVehicleClassification
                .genericVehicleClassification(testcv)
                .getVehicleType().getValue();
        
        assertEquals(expectedName, actualName);
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test maximum vehicle group affected value (9251) returns ("military-vehicles")
     */
    @Test
    public void shouldReturnMaximumVehicleGroupAffected() {
        
        Integer testInput = 9251;

        String expectedName = "military-vehicles";
        Long expectedValue = 9251L;
        
        VehicleGroupAffected testVehicleGroupAffected = new VehicleGroupAffected(testInput);
        
        VehicleClassification testcv = new VehicleClassification();
        testcv.setVehicleType(testVehicleGroupAffected);
        
        String actualName = OssVehicleClassification
                .genericVehicleClassification(testcv)
                .getVehicleType().getName();
        Long actualValue = OssVehicleClassification
                .genericVehicleClassification(testcv)
                .getVehicleType().getValue();
        
        assertEquals(expectedName, actualName);
        assertEquals(expectedValue, actualValue);
    }

    // ResponderGroupAffected tests
    /**
     * Test minimum responder group affected value (9729) returns ("emergency_vehicle_units")
     */
    @Test
    public void shouldReturnMinimumResponderGroupAffected() {
        
        Integer testInput = 9729;
        
        String expectedValue = "emergency_vehicle_units";
        
        ResponderGroupAffected testResponderGroupAffected = new ResponderGroupAffected(testInput);
        
        VehicleClassification testvc = new VehicleClassification();
        testvc.setResponderType(testResponderGroupAffected);
        
        String actualValue = OssVehicleClassification
                .genericVehicleClassification(testvc)
                .getResponderType().name();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case minimum responder group affected value (9730) returns ("federal_law_enforcement_units")
     */
    @Test
    public void shouldReturnCornerCaseMinimumResponderGroupAffected() {
        
        Integer testInput = 9730;
        
        String expectedValue = "federal_law_enforcement_units";
        
        ResponderGroupAffected testResponderGroupAffected = new ResponderGroupAffected(testInput);
        
        VehicleClassification testvc = new VehicleClassification();
        testvc.setResponderType(testResponderGroupAffected);
        
        String actualValue = OssVehicleClassification
                .genericVehicleClassification(testvc)
                .getResponderType().name();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test known, middle responder group affected value (9731) returns ("state_police_units")
     */
    @Test
    public void shouldReturnMiddleResponderGroupAffected() {
        
        Integer testInput = 9731;
        
        String expectedValue = "state_police_units";
        
        ResponderGroupAffected testResponderGroupAffected = new ResponderGroupAffected(testInput);
        
        VehicleClassification testvc = new VehicleClassification();
        testvc.setResponderType(testResponderGroupAffected);
        
        String actualValue = OssVehicleClassification
                .genericVehicleClassification(testvc)
                .getResponderType().name();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test corner case maximum responder group affected value (9741) returns ("transportation_response_units")
     */
    @Test
    public void shouldReturnCornerCaseMaximumResponderGroupAffected() {
        
        Integer testInput = 9741;
        
        String expectedValue = "transportation_response_units";
        
        ResponderGroupAffected testResponderGroupAffected = new ResponderGroupAffected(testInput);
        
        VehicleClassification testvc = new VehicleClassification();
        testvc.setResponderType(testResponderGroupAffected);
        
        String actualValue = OssVehicleClassification
                .genericVehicleClassification(testvc)
                .getResponderType().name();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test maximum responder group affected value (9742) returns ("private_contractor_response_units")
     */
    @Test
    public void shouldReturnMaximumResponderGroupAffected() {
        
        Integer testInput = 9742;
        
        String expectedValue = "private_contractor_response_units";
        
        ResponderGroupAffected testResponderGroupAffected = new ResponderGroupAffected(testInput);
        
        VehicleClassification testvc = new VehicleClassification();
        testvc.setResponderType(testResponderGroupAffected);
        
        String actualValue = OssVehicleClassification
                .genericVehicleClassification(testvc)
                .getResponderType().name();
        
        assertEquals(expectedValue, actualValue);
    }
    
    // FuelType tests
    /**
     * Test minimum fuel type value (0) returns ("unknownFuel")
     */
    @Test
    public void shouldReturnMinimumFuelType() {
        
        Integer testInput = 0;
        String expectedValue = "unknownFuel";
        
        FuelType testFuelType = new FuelType(testInput);
        
        VehicleClassification testvc = new VehicleClassification();
        testvc.setFuelType(testFuelType);
        
        String actualValue = OssVehicleClassification
                .genericVehicleClassification(testvc)
                .getFuelType().toString();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case minimum fuel type value (1) returns ("gasoline")
     */
    @Test
    public void shouldReturnCornerCaseMinimumFuelType() {
        
        Integer testInput = 1;
        String expectedValue = "gasoline";
        
        FuelType testFuelType = new FuelType(testInput);
        
        VehicleClassification testvc = new VehicleClassification();
        testvc.setFuelType(testFuelType);
        
        String actualValue = OssVehicleClassification
                .genericVehicleClassification(testvc)
                .getFuelType().toString();
        
        assertEquals(expectedValue, actualValue); 
    }
    
    /**
     * Test known, middle fuel type value (4) returns ("electric")
     */
    @Test
    public void shouldReturnMiddleFuelType() {
        
        Integer testInput = 4;
        String expectedValue = "electric";
        
        FuelType testFuelType = new FuelType(testInput);
        
        VehicleClassification testvc = new VehicleClassification();
        testvc.setFuelType(testFuelType);
        
        String actualValue = OssVehicleClassification
                .genericVehicleClassification(testvc)
                .getFuelType().toString();
        
        assertEquals(expectedValue, actualValue); 
    }
    
    /**
     * Test corner case maximum fuel type value (8) returns ("natGasComp")
     */
    @Test
    public void shouldReturnCornerCaseMaximumFuelType() {
        
        Integer testInput = 8;
        String expectedValue = "natGasComp";
        
        FuelType testFuelType = new FuelType(testInput);
        
        VehicleClassification testvc = new VehicleClassification();
        testvc.setFuelType(testFuelType);
        
        String actualValue = OssVehicleClassification
                .genericVehicleClassification(testvc)
                .getFuelType().toString();
        
        assertEquals(expectedValue, actualValue); 
    }
    
    /**
     * Test maximum fuel type value (9) returns ("propane")
     */
    @Test
    public void shouldReturnMaximumFuelType() {
        
        Integer testInput = 9;
        String expectedValue = "propane";
        
        FuelType testFuelType = new FuelType(testInput);
        
        VehicleClassification testvc = new VehicleClassification();
        testvc.setFuelType(testFuelType);
        
        String actualValue = OssVehicleClassification
                .genericVehicleClassification(testvc)
                .getFuelType().toString();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test fuel type value (-1) below lower bound (0) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionFuelTypeBelowLowerBound() {
        
        Integer testInput = -1;
        
        FuelType testFuelType = new FuelType(testInput);
        
        VehicleClassification testvc = new VehicleClassification();
        testvc.setFuelType(testFuelType);
        
        try {
           OssVehicleClassification
                   .genericVehicleClassification(testvc)
                   .getFuelType().toString();
           fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    /**
     * Test fuel type value (10) above upper bound (9) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionFuelTypeAboveUpperBound() {
        
        Integer testInput = 10;
        
        FuelType testFuelType = new FuelType(testInput);
        
        VehicleClassification testvc = new VehicleClassification();
        testvc.setFuelType(testFuelType);
        
        try {
           OssVehicleClassification
                   .genericVehicleClassification(testvc)
                   .getFuelType().toString();
           fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<OssVehicleClassification> constructor = OssVehicleClassification.class.getDeclaredConstructor();
      assertTrue(Modifier.isPrivate(constructor.getModifiers()));
      constructor.setAccessible(true);
      try {
        constructor.newInstance();
        fail("Expected IllegalAccessException.class");
      } catch (Exception e) {
        assertEquals(InvocationTargetException.class, e.getClass());
      }
    }

}
