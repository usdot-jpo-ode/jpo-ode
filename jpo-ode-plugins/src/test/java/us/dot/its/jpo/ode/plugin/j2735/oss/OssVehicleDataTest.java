package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.BumperHeight;
import us.dot.its.jpo.ode.j2735.dsrc.BumperHeights;
import us.dot.its.jpo.ode.j2735.dsrc.TrailerWeight;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleData;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleHeight;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleMass;
import us.dot.its.jpo.ode.plugin.j2735.J2735BumperHeights;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleData;

/**
 * -- Summary --
 * JUnit test class for OssVehicleData
 * 
 * All sub-elements of this class are tested by other test classes. 
 * This test class verifies successful creation of optional members.
 * 
 * -- Documentation --
 * Data Frame: DF_VehicleData
 * Use: The DF_VehicleData data frame is used to convey additional data about the vehicle not found in the BSM 
 * Part I data frame.
 * ASN.1 Representation:
 *    VehicleData ::= SEQUENCE { -- Values for width and length are sent in BSM part I
 *       height VehicleHeight OPTIONAL,
 *       bumpers BumperHeights OPTIONAL,
 *       mass VehicleMass OPTIONAL,
 *       trailerWeight TrailerWeight OPTIONAL,
 *       ...
 *       }
 *
 */
public class OssVehicleDataTest {
    
    /**
     * Test that an empty vehicle data object is successfully created
     */
    @Test
    public void shouldCreateEmptyVehicleData() {
        
        VehicleData testVehicleData = new VehicleData();
        
        try {
            J2735VehicleData actualVehicleData = OssVehicleData.genericVehicleData(testVehicleData);
            return; // implicit test pass
        } catch (RuntimeException e) {
            fail("Unexpected RuntimeException: " + e.getClass());
        }
         
    }
    
    /**
     * Test that a vehicle data object that contains only vehicle height is successfully created
     * Verify that input vehicle height value (0) returns (0.00)
     */
    @Test
    public void shouldCreateVehicleDataWithHeight() {
        
        Integer testInput = 0;
        BigDecimal expectedValue = BigDecimal.ZERO.setScale(2);
        
        VehicleHeight testVehicleHeight = new VehicleHeight(testInput);
        
        VehicleData testVehicleData = new VehicleData();
        testVehicleData.setHeight(testVehicleHeight);
        
        try {
            BigDecimal actualValue = OssVehicleData
                    .genericVehicleData(testVehicleData)
                    .getHeight();
            assertEquals(expectedValue, actualValue);
        } catch (RuntimeException e) {
            fail("Unexpected RuntimeException: " + e.getClass());
        }
        
    }
    
    /**
     * Test that a vehicle data object that contains only bumper heights is successfully created
     * Verify input bumper height value (0) returns (0.00)
     */
    @Test
    public void shouldCreateVehicleDataWithBumperHeights() {

        Integer testInput = 0;
        BigDecimal expectedValue = BigDecimal.ZERO.setScale(2);
        
        BumperHeight testFrontBumperHeight = new BumperHeight(testInput);
        BumperHeight testRearBumperHeight = new BumperHeight(testInput);
        
        BumperHeights testBumperHeights = new BumperHeights(testFrontBumperHeight, testRearBumperHeight);
        
        VehicleData testVehicleData = new VehicleData();
        testVehicleData.setBumpers(testBumperHeights);
        
        try {
            J2735BumperHeights actualBumperHeights = OssVehicleData
                    .genericVehicleData(testVehicleData)
                    .getBumpers();
            assertEquals(expectedValue, actualBumperHeights.front);
            assertEquals(expectedValue, actualBumperHeights.rear);
        } catch (RuntimeException e) {
            fail("Unexpected RuntimeException: " + e.getClass());
        }
    }
    
    /**
     * Test that a vehicle data object that contains only mass is successfully created
     * Verify that input vehicle mass value (0) returns (0)
     */
    @Test
    public void shouldCreateVehicleDataWithMass() {
        
        Integer testInput = 0;
        Integer expectedValue = 0;
        
        VehicleMass testVehicleMass = new VehicleMass(testInput);
        
        VehicleData testVehicleData = new VehicleData();
        testVehicleData.setMass(testVehicleMass);
        
        try {
            Integer actualValue = OssVehicleData
                    .genericVehicleData(testVehicleData)
                    .getMass();
            assertEquals(expectedValue, actualValue);
        } catch (RuntimeException e) {
            fail("Unexpected RuntimeException: " + e.getClass());
        }
    }
    
    /**
     * Test that a vehicle data object that contains only trailer weight is successfully created
     * Verify that a trailer weight value (0) returns (0)
     */
    @Test
    public void shouldCreateVehicleDataWithTrailerWeight() {
        
        Integer testInput = 0;
        Integer expectedValue = 0;
        
        TrailerWeight testTrailerWeight = new TrailerWeight(testInput);
        
        VehicleData testVehicleData = new VehicleData();
        testVehicleData.setTrailerWeight(testTrailerWeight);
        
        try {
            Integer actualValue = OssVehicleData
                    .genericVehicleData(testVehicleData)
                    .getTrailerWeight();
            assertEquals(expectedValue, actualValue);
        } catch (RuntimeException e) {
            fail("Unexpected RuntimeException: " + e.getClass());
        }
    }

}
