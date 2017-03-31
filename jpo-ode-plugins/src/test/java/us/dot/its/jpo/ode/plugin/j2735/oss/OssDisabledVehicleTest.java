package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.DisabledVehicle;
import us.dot.its.jpo.ode.j2735.itis.GenericLocations;
import us.dot.its.jpo.ode.j2735.itis.ITIScodes;
import us.dot.its.jpo.ode.plugin.j2735.J2735DisabledVehicle;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssDisabledVehicle;

/**
 * -- Summary --
 * JUnit test class for OssDisabledVehicle
 * 
 * Verifies correct conversion from generic DisabledVehicle to compliant-J2735DisabledVehicle
 * 
 * Notes:
 *    - statusDetails and locationDetails are ITIS codes, not part of J2735 specification
 *    - locationDetails is an optional element in this class, is mocked with a dummy value, and is untested
 *    - The goal of this test is to check correct aggregation of those elements, not correct ITIS codes
 * 
 * -- Documentation --
 * Data Frame: DF_DisabledVehicle
 * Use: The DF_DisabledVehicle data frame provides a means for a vehicle (or other equipped device) to describe its 
 * operational status and gross location to others using a subset of the ITIS codes. This data frame is most typically 
 * used to send information about a disabled vehicle to others. The vehicle’s various classification values are 
 * handled by other data elements found in the BSM Part II content.
 * ASN.1 Representation:
 *    DisabledVehicle ::= SEQUENCE {
 *       statusDetails ITIS.ITIScodes(523..541),
 *       -- Codes 532 to 541, as taken from J2540:
 *          -- Disabled, etc.
 *          -- stalled-vehicle (532),
 *          -- abandoned-vehicle (533),
 *          -- disabled-vehicle (534),
 *          -- disabled-truck (535),
 *          -- disabled-semi-trailer (536), -^- Alt: disabled
 *       -- tractor-trailer
 *          -- disabled-bus (537),
 *          -- disabled-train (538),
 *          -- vehicle-spun-out (539),
 *          -- vehicle-on-fire (540),
 *          -- vehicle-in-water (541),
 *       locationDetails ITIS.GenericLocations OPTIONAL,
 *          ...
 *       }
 */
public class OssDisabledVehicleTest {

    /**
     * Test that the minimum disabled vehicle code (532) correctly returns as
     * (532)
     */
    @Test
    public void shouldReturnMinimumDisabledVehicleCode() {

        Integer testITISCode = 532;
        Integer expectedValue = 532;

        ITIScodes statusDetails = new ITIScodes(testITISCode);
        GenericLocations emptyLocationDetails = new GenericLocations(7937);

        DisabledVehicle testVehicleStatus = new DisabledVehicle(statusDetails, emptyLocationDetails);

        J2735DisabledVehicle actualDisabledVehicleStatus = OssDisabledVehicle.genericDisabledVehicle(testVehicleStatus);

        assertEquals(expectedValue, actualDisabledVehicleStatus.getStatusDetails());

    }

    /**
     * Test that the maximum disabled vehicle code (541) correctly returns as
     * (541)
     */
    @Test
    public void shouldReturnMaximumDisabledVehicleCode() {

        Integer testITISCode = 541;
        Integer expectedValue = 541;

        ITIScodes statusDetails = new ITIScodes(testITISCode);
        GenericLocations emptyLocationDetails = new GenericLocations(7937);

        DisabledVehicle testVehicleStatus = new DisabledVehicle(statusDetails, emptyLocationDetails);

        J2735DisabledVehicle actualDisabledVehicleStatus = OssDisabledVehicle.genericDisabledVehicle(testVehicleStatus);

        assertEquals(expectedValue, actualDisabledVehicleStatus.getStatusDetails());

    }

    /**
     * Test that the known disabled vehicle code (535) correctly returns as
     * (535)
     */
    @Test
    public void shouldReturnKnownDisabledVehicleCode() {

        Integer testITISCode = 535;
        Integer expectedValue = 535;

        ITIScodes statusDetails = new ITIScodes(testITISCode);
        GenericLocations emptyLocationDetails = new GenericLocations(7937);

        DisabledVehicle testVehicleStatus = new DisabledVehicle(statusDetails, emptyLocationDetails);

        J2735DisabledVehicle actualDisabledVehicleStatus = OssDisabledVehicle.genericDisabledVehicle(testVehicleStatus);

        assertEquals(expectedValue, actualDisabledVehicleStatus.getStatusDetails());

    }
    
    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<OssDisabledVehicle> constructor = OssDisabledVehicle.class.getDeclaredConstructor();
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
