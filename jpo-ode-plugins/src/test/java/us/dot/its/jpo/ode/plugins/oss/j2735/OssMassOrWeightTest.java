package us.dot.its.jpo.ode.plugins.oss.j2735;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Ignore;
import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.TrailerMass;
import us.dot.its.jpo.ode.j2735.dsrc.TrailerWeight;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleMass;

/**
 * Test class for OssMassOrWeight
 * 
 * Test three ASN.1 specifications:
 * 
 * 1) Trailer Mass: TrailerMass ::= INTEGER (0..255) -- object mass with LSB
 * steps of 500 kg (~1100 lbs) -- the value zero shall be used for an unknown
 * mass value -- the value 255 shall be used any mass larger than 127,500kg -- a
 * useful range of 0~127.5 metric tons.
 * 
 * 2) Trailer Weight: SAE J1939 standard and encoded as: 2kg/bit, 0 deg offset,
 * Range: 0 to +128,510kg. See SPN 180, PGN reference 65258.
 *
 * 3) Vehicle Mass: VehicleMass ::= INTEGER (0..255) -- Values 000 to 080 in
 * steps of 50kg -- Values 081 to 200 in steps of 500kg -- Values 201 to 253 in
 * steps of 2000kg -- The Value 254 shall be used for weights above 170000 kg --
 * The Value 255 shall be used when the value is unknown or unavailable --
 * Encoded such that the values: -- 81 represents 4500 kg -- 181 represents
 * 54500 kg -- 253 represents 170000 kg
 *
 */
public class OssMassOrWeightTest {

    // TrailerMass tests

    /**
     * Test that an undefined trailer mass value of (0) returns (null)
     */
    @Test
    public void shouldReturnUndefinedTrailerMass() {

        Integer testMass = 0;

        Integer expectedValue = null;

        TrailerMass testTrailerMass = new TrailerMass(testMass);

        Integer actualValue = OssMassOrWeight.genericMass(testTrailerMass);

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that a minimum trailer mass of (1) returns (500)
     */
    @Test
    public void shouldReturnMinimumTrailerMass() {

        Integer testMass = 1;

        Integer expectedValue = 500;

        TrailerMass testTrailerMass = new TrailerMass(testMass);

        Integer actualValue = OssMassOrWeight.genericMass(testTrailerMass);

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that a maximum trailer mass of (254) returns (127000)
     */
    @Test
    public void shouldReturnMaximumTrailerMass() {

        Integer testMass = 254;

        Integer expectedValue = 127000;

        TrailerMass testTrailerMass = new TrailerMass(testMass);

        Integer actualValue = OssMassOrWeight.genericMass(testTrailerMass);

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that a trailer mass of (255) indicates a trailer mass larger than
     * (127000)
     */
    @Test
    public void shouldReturnLargerTrailerMass() {

        Integer testMass = 255;

        Integer massToBeLargerThan = 127000;

        TrailerMass testTrailerMass = new TrailerMass(testMass);

        Integer actualValue = OssMassOrWeight.genericMass(testTrailerMass);

        assertTrue(actualValue > massToBeLargerThan);

    }

    /**
     * Test that a known trailer mass of (123) returns (61500)
     */
    @Test
    public void shouldReturnKnownTrailerMass() {

        Integer testMass = 123;

        Integer expectedValue = 61500;

        TrailerMass testTrailerMass = new TrailerMass(testMass);

        Integer actualValue = OssMassOrWeight.genericMass(testTrailerMass);

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that an invalid trailer mass below (0) throws an exception
     */
    @Test
    public void shouldThrowExceptionTrailerMassBelowLowerBound() {

        Integer testMass = -1;
        TrailerMass testTrailerMass = new TrailerMass(testMass);

        try {
            Integer actualValue = OssMassOrWeight.genericMass(testTrailerMass);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }

    }

    /**
     * Test that an invalid trailer mass above (255) throws an exception
     */
    @Test
    public void shouldThrowExceptionTrailerMassAboveUpperBound() {

        Integer testMass = 256;
        TrailerMass testTrailerMass = new TrailerMass(testMass);

        try {
            Integer actualValue = OssMassOrWeight.genericMass(testTrailerMass);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }

    }
    
    // TrailerWeight tests

    /**
     * Test that a minimum trailer weight of (0) returns (0)
     */
    @Test
    public void shouldReturnMinimumTrailerWeight() {

        Integer testWeight = 0;

        Integer expectedValue = 0;

        TrailerWeight testTrailerWeight = new TrailerWeight(testWeight);

        Integer actualValue = OssMassOrWeight.genericWeight(testTrailerWeight);

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that a maximum trailer weight of (64255) returns (128510)
     */
    @Test
    public void shouldReturnMaximumTrailerWeight() {

        Integer testWeight = 64255;

        Integer expectedValue = 128510;

        TrailerWeight testTrailerWeight = new TrailerWeight(testWeight);

        Integer actualValue = OssMassOrWeight.genericWeight(testTrailerWeight);

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that a known trailer weight of (12500) returns (25000)
     */
    @Test
    public void shouldReturnKnownTrailerWeight() {

        Integer testWeight = 12500;

        Integer expectedValue = 25000;

        TrailerWeight testTrailerWeight = new TrailerWeight(testWeight);

        Integer actualValue = OssMassOrWeight.genericWeight(testTrailerWeight);

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that an invalid trailer weight below (0) throws an exception
     */
    @Test
    public void shouldThrowExceptionTrailerWeightBelowLowerBound() {

        Integer testWeight = -1;

        TrailerWeight testTrailerWeight = new TrailerWeight(testWeight);

        try {
            Integer actualValue = OssMassOrWeight.genericWeight(testTrailerWeight);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }

    }

    /**
     * Test that an invalid trailer weight above (64255) throws an exception
     */
    @Test
    public void shouldThrowExceptionTrailerWeightAboveUpperBound() {

        Integer testWeight = 64256;

        TrailerWeight testTrailerWeight = new TrailerWeight(testWeight);

        try {
            Integer actualValue = OssMassOrWeight.genericWeight(testTrailerWeight);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }

    }

    // VehicleMass tests

    /**
     * Test that an input vehicle mass in lowest 50kg step range of (0) returns
     * (0)
     */
    @Test
    public void shouldReturnMinimumVehicleMass50KGStep() {

        Integer testMass = 0;

        Integer expectedValue = 0;

        VehicleMass testVehicleMass = new VehicleMass(testMass);

        Integer actualValue = OssMassOrWeight.genericMass(testVehicleMass);

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that an input vehicle mass in highest 50kg step range of (80)
     * returns (4000)
     */
    @Test
    public void shouldReturnMaximumVehicleMass50KGStep() {

        Integer testMass = 80;

        Integer expectedValue = 4000;

        VehicleMass testVehicleMass = new VehicleMass(testMass);

        Integer actualValue = OssMassOrWeight.genericMass(testVehicleMass);

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that an input vehicle mass in lowest 500kg step range of (81)
     * returns (4500)
     */
    @Test
    public void shouldReturnMinimumVehicleMass500KGStep() {

        Integer testMass = 81;

        Integer expectedValue = 4500;

        VehicleMass testVehicleMass = new VehicleMass(testMass);

        Integer actualValue = OssMassOrWeight.genericMass(testVehicleMass);

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that an input vehicle mass in highest 500kg step range of (200)
     * returns (64000)
     */
    @Test
    public void shouldReturnMaximumVehicleMass500KGStep() {

        Integer testMass = 200;

        Integer expectedValue = 64000;

        VehicleMass testVehicleMass = new VehicleMass(testMass);

        Integer actualValue = OssMassOrWeight.genericMass(testVehicleMass);

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that an input vehicle mass in lowest 2000kg step range of (201)
     * returns (66000)
     */
    @Test
    public void shouldReturnMinimumVehicleMass2000KGStep() {

        Integer testMass = 201;

        Integer expectedValue = 66000;

        VehicleMass testVehicleMass = new VehicleMass(testMass);

        Integer actualValue = OssMassOrWeight.genericMass(testVehicleMass);

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that an input vehicle mass in highest 2000kg step range of (253)
     * returns (170000)
     */
    @Test
    public void shouldReturnMaximumVehicleMass2000KGStep() {

        Integer testMass = 253;

        Integer expectedValue = 170000;

        VehicleMass testVehicleMass = new VehicleMass(testMass);

        Integer actualValue = OssMassOrWeight.genericMass(testVehicleMass);

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that an input vehicle mass of (254) signifies a vehicle mass greater
     * than (170000)
     */
    @Test
    public void shouldReturnLargerVehicleMass() {

        Integer testMass = 254;

        Integer massToBeLargerThan = 170000;

        VehicleMass testVehicleMass = new VehicleMass(testMass);

        Integer actualValue = OssMassOrWeight.genericMass(testVehicleMass);

        assertTrue(actualValue > massToBeLargerThan);

    }

    /**
     * Test that an input vehicle mass of (255) signifies an undefined vehicle
     * mass (null)
     */
    @Test
    public void shouldReturnUndefinedVehicleMass() {

        Integer testMass = 255;

        Integer expectedValue = null;

        VehicleMass testVehicleMass = new VehicleMass(testMass);

        Integer actualValue = OssMassOrWeight.genericMass(testVehicleMass);

        assertEquals(expectedValue, actualValue);

    }

    /**
     * Test that an input vehicle mass below (0) throws an exception
     */
    @Test
    public void shouldThrowExceptionVehicleMassBelowLowerBound() {

        Integer testMass = -1;

        VehicleMass testVehicleMass = new VehicleMass(testMass);

        try {
            Integer actualValue = OssMassOrWeight.genericMass(testVehicleMass);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }

    }

    /**
     * Test that an input vehicle mass above (255) throws an exception
     */
    @Test
    public void shouldThrowExceptionVehicleMassAboveUpperBound() {

        Integer testMass = 256;

        VehicleMass testVehicleMass = new VehicleMass(testMass);

        try {
            Integer actualValue = OssMassOrWeight.genericMass(testVehicleMass);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }

    }

}
