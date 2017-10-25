package us.dot.its.jpo.ode.plugin.j2735.builders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import us.dot.its.jpo.ode.j2735.dsrc.VehicleData;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleHeight;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleData;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssVehicleData;

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
public class VehicleDataBuilderTest {


   

   /**
    * Test that a vehicle data object that contains only vehicle height is successfully created
    * Verify that input vehicle height value (0) returns (0.00)
    */
   @Ignore
   @Test
   public void shouldCreateVehicleDataWithHeight() {
       ObjectMapper mapper = new ObjectMapper();
       BigDecimal testInput = BigDecimal.ZERO.setScale(2);
       BigDecimal expectedValue = BigDecimal.ZERO.setScale(2);
       
       
       
       J2735VehicleData testVehicleData = new J2735VehicleData();
       testVehicleData.setHeight(testInput);
       
       try {
           BigDecimal actualValue = VehicleDataBuilder
                   .genericVehicleData(testVehicleData)
                   .getHeight();
           assertEquals(expectedValue, actualValue);
       } catch (RuntimeException e) {
           fail("Unexpected RuntimeException: " + e.getClass());
       }
       
   }

}
