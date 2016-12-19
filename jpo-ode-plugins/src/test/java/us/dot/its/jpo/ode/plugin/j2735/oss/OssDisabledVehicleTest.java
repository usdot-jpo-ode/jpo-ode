package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.*;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.DisabledVehicle;
import us.dot.its.jpo.ode.j2735.itis.GenericLocations;
import us.dot.its.jpo.ode.j2735.itis.ITIScodes;
import us.dot.its.jpo.ode.plugin.j2735.J2735DisabledVehicle;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssDisabledVehicle;

/**
 * Test class for OssDisabledVehicle
 * 
 * Tests correct return for codes (532..542) specified by ASN.1 representation
 * The locationDetails parameter is optional and known location code 7937
 * ("on_bridges") used
 * 
 * 
 * ASN.1 Representation: DisabledVehicle ::= SEQUENCE { statusDetails
 * ITIS.ITIScodes(532..541), -- Codes 532 to 541, as taken from J2540: --
 * Disabled, etc. -- stalled-vehicle (532), -- abandoned-vehicle (533), --
 * disabled-vehicle (534), -- disabled-truck (535), -- disabled-semi-trailer
 * (536), -^- Alt: disabled -- tractor-trailer -- disabled-bus (537), --
 * disabled-train (538), -- vehicle-spun-out (539), -- vehicle-on-fire (540), --
 * vehicle-in-water (541), locationDetails ITIS.GenericLocations OPTIONAL, ... }
 * 
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

        assertEquals(expectedValue, actualDisabledVehicleStatus.statusDetails);

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

        assertEquals(expectedValue, actualDisabledVehicleStatus.statusDetails);

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

        assertEquals(expectedValue, actualDisabledVehicleStatus.statusDetails);

    }

}
