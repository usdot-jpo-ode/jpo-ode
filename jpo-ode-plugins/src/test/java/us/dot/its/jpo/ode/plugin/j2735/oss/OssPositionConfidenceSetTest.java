package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.ElevationConfidence;
import us.dot.its.jpo.ode.j2735.dsrc.PositionConfidence;
import us.dot.its.jpo.ode.j2735.dsrc.PositionConfidenceSet;
import us.dot.its.jpo.ode.plugin.j2735.J2735PositionConfidenceSet;
import us.dot.its.jpo.ode.plugin.j2735.J2735PositionConfidenceSet.J2735ElevationConfidence;
import us.dot.its.jpo.ode.plugin.j2735.J2735PositionConfidenceSet.J2735PositionConfidence;

/**
 * -- Summary --
 * JUnit test class for OssPositionConfidence
 * 
 * Verifies correct conversion from generic PositionConfidenceSet to compliant-J2735PositionConfidenceSet
 * 
 * -- Documentation --
 * Data Frame: DF_PositionConfidenceSet
 * Use: The DF_PositionConfidenceSet data frame combines multiple related bit fields into a single concept.
 * ASN.1 Representation:
 *    PositionConfidenceSet ::= SEQUENCE {
 *       pos PositionConfidence, -- for both horizontal directions
 *       elevation ElevationConfidence
 *    }
 * 
 * Data Element: DE_PositionConfidence
 * Use: The DE_PositionConfidence entry is used to provide the 95% confidence level for the currently reported value 
 * of entries such as the DE_Position entries, taking into account the current calibration and precision of the 
 * sensor(s) used to measure and/or calculate the value. It is used in the horizontal plane. This data element is only
 * to provide the listener with information on the limitations of the sensing system; not to support any type of 
 * automatic error correction or to imply a guaranteed maximum error. This data element should not be used for fault 
 * detection or diagnosis, but if a vehicle is able to detect a fault, the confidence interval should be increased 
 * accordingly. The frame of reference and axis of rotation used shall be accordance with that defined in Section 11 
 * of this standard.
 * ASN.1 Representation:
 *    PositionConfidence ::= ENUMERATED {
 *       unavailable (0), -- B'0000 Not Equipped or unavailable
 *       a500m (1), -- B'0001 500m or about 5 * 10 ^ -3 decimal degrees
 *       a200m (2), -- B'0010 200m or about 2 * 10 ^ -3 decimal degrees
 *       a100m (3), -- B'0011 100m or about 1 * 10 ^ -3 decimal degrees
 *       a50m (4), -- B'0100 50m or about 5 * 10 ^ -4 decimal degrees
 *       a20m (5), -- B'0101 20m or about 2 * 10 ^ -4 decimal degrees
 *       a10m (6), -- B'0110 10m or about 1 * 10 ^ -4 decimal degrees
 *       a5m (7), -- B'0111 5m or about 5 * 10 ^ -5 decimal degrees
 *       a2m (8), -- B'1000 2m or about 2 * 10 ^ -5 decimal degrees
 *       a1m (9), -- B'1001 1m or about 1 * 10 ^ -5 decimal degrees
 *       a50cm (10), -- B'1010 0.50m or about 5 * 10 ^ -6 decimal degrees
 *       a20cm (11), -- B'1011 0.20m or about 2 * 10 ^ -6 decimal degrees
 *       a10cm (12), -- B'1100 0.10m or about 1 * 10 ^ -6 decimal degrees
 *       a5cm (13), -- B'1101 0.05m or about 5 * 10 ^ -7 decimal degrees
 *       a2cm (14), -- B'1110 0.02m or about 2 * 10 ^ -7 decimal degrees
 *       a1cm (15) -- B'1111 0.01m or about 1 * 10 ^ -7 decimal degrees
 *    }
 *    -- Encoded as a 4 bit value
 * 
 * Data Element: DE_ElevationConfidence
 * Use: The DE_ElevationConfidence data element is used to provide the 95% confidence level for the currently reported
 * value of DE_Elevation, taking into account the current calibration and precision of the sensor(s) used to measure 
 * and/or calculate the value. This data element is only to provide the listener with information on the limitations 
 * of the sensing system, not to support any type of automatic error correction or to imply a guaranteed maximum 
 * error. This data element should not be used for fault detection or diagnosis, but if a vehicle is able to detect a 
 * fault, the confidence interval should be increased accordingly. The frame of reference and axis of rotation used 
 * shall be in accordance with that defined in Section 11.
 * ASN.1 Representation:
 *    ElevationConfidence ::= ENUMERATED {
 *       unavailable (0), -- B'0000 Not Equipped or unavailable
 *       elev-500-00 (1), -- B'0001 (500 m)
 *       elev-200-00 (2), -- B'0010 (200 m)
 *       elev-100-00 (3), -- B'0011 (100 m)
 *       elev-050-00 (4), -- B'0100 (50 m)
 *       elev-020-00 (5), -- B'0101 (20 m)
 *       elev-010-00 (6), -- B'0110 (10 m)
 *       elev-005-00 (7), -- B'0111 (5 m)
 *       elev-002-00 (8), -- B'1000 (2 m)
 *       elev-001-00 (9), -- B'1001 (1 m)
 *       elev-000-50 (10), -- B'1010 (50 cm)
 *       elev-000-20 (11), -- B'1011 (20 cm)
 *       elev-000-10 (12), -- B'1100 (10 cm)
 *       elev-000-05 (13), -- B'1101 (5 cm)
 *       elev-000-02 (14), -- B'1110 (2 cm)
 *       elev-000-01 (15) -- B'1111 (1 cm)
 *    } 
 *    -- Encoded as a 4 bit value
 */
public class OssPositionConfidenceSetTest {
    
    // PositionConfidence tests
    
    /**
     * Test that an undefined position confidence value (0) returns (null)
     * Test that an undefined elevation confidence value (0) returns (null)
     */
    @Test
    public void shouldReturnUndefinedPositionConfidenceSet() {
        
        Integer testInput = 0;
        
        PositionConfidence testPositionConfidence = new PositionConfidence(testInput);
        ElevationConfidence testElevationConfidence = new ElevationConfidence(testInput);
        
        PositionConfidenceSet testPositionConfidenceSet = new PositionConfidenceSet(
                testPositionConfidence,
                testElevationConfidence);
        
        J2735PositionConfidenceSet testJ2735PositionConfidenceSet = 
                OssPositionConfidenceSet.genericPositionConfidenceSet(testPositionConfidenceSet);
        
        
        assertNull("Expected null PositionConfidence", testJ2735PositionConfidenceSet.pos);
        assertNull("Expected null ElevationConfidence", testJ2735PositionConfidenceSet.elevation);
        
        
    }
    
    /**
     * Test that the minimum position confidence value (1) returns (a500m)
     * Test that the minimum elevation confidence value (1) returns (elev-500-00)
     */
    @Test
    public void shouldReturnMinimumPositionConfidenceSet() {
        
        Integer testInput = 1;
        
        PositionConfidence testPositionConfidence = new PositionConfidence(testInput);
        ElevationConfidence testElevationConfidence = new ElevationConfidence(testInput);
        
        PositionConfidenceSet testPositionConfidenceSet = new PositionConfidenceSet(
                testPositionConfidence,
                testElevationConfidence);
        
        J2735PositionConfidenceSet testJ2735PositionConfidenceSet =
                OssPositionConfidenceSet.genericPositionConfidenceSet(testPositionConfidenceSet);
       
        assertEquals("Expected PositionConfidence value a500m", 
                J2735PositionConfidence.a500m, 
                testJ2735PositionConfidenceSet.pos);
        
        
        assertEquals("Expected ElevationConfidence value elev-500-00", 
                J2735ElevationConfidence.elev_500_00,
                testJ2735PositionConfidenceSet.elevation);
        
    }
    
    /**
     * Test that a lower corner-case position confidence set value (2) returns (a200m) and (elev-200-00)
     * for position and elevation, respectively
     */
    @Test
    public void shouldReturnKnownPositionConfidenceSet1() {
        
        Integer testInput = 2;
        
        J2735PositionConfidence expectedPositionValue = J2735PositionConfidence.a200m;
        J2735ElevationConfidence expectedElevationValue = J2735ElevationConfidence.elev_200_00;
        
        PositionConfidence testPositionConfidence = new PositionConfidence(testInput);
        ElevationConfidence testElevationConfidence = new ElevationConfidence(testInput);
        
        PositionConfidenceSet testPositionConfidenceSet = 
                new PositionConfidenceSet(testPositionConfidence, testElevationConfidence);
        
        J2735PositionConfidenceSet actualJ2735PositionConfidenceSet =
                OssPositionConfidenceSet.genericPositionConfidenceSet(testPositionConfidenceSet);
        
        assertEquals("Expected PositionConfidence value a200m",
                expectedPositionValue,
                actualJ2735PositionConfidenceSet.pos);
        
        assertEquals("Expected ElevationConfidence value elev_200_00",
                expectedElevationValue,
                actualJ2735PositionConfidenceSet.elevation);
        
    }
    
    /**
     * Test that an upper corner-case position confidence set value (14) returns (a2cm) and (elev-000-02)
     * for position and elevation, respectively
     */
    @Test
    public void shouldReturnKnownPositionConfidenceSet2() {
        
        Integer testInput = 14;
        
        J2735PositionConfidence expectedPositionValue = J2735PositionConfidence.a2cm;
        J2735ElevationConfidence expectedElevationValue = J2735ElevationConfidence.elev_000_02;
        
        PositionConfidence testPositionConfidence = new PositionConfidence(testInput);
        ElevationConfidence testElevationConfidence = new ElevationConfidence(testInput);
        
        PositionConfidenceSet testPositionConfidenceSet =
                new PositionConfidenceSet(testPositionConfidence, testElevationConfidence);
        
        J2735PositionConfidenceSet actualJ2735PositionConfidenceSet =
                OssPositionConfidenceSet.genericPositionConfidenceSet(testPositionConfidenceSet);
        
        assertEquals("Expected PositionConfidence value a2cm",
                expectedPositionValue,
                actualJ2735PositionConfidenceSet.pos);
        
        assertEquals("Expected ElevationConfidence value elev_000_02",
                expectedElevationValue,
                actualJ2735PositionConfidenceSet.elevation);
        
    }
    
    /**
     * Test that the maximum confidence set value (15) returns (a1cm) and (elev-000-01)
     * for position and elevation, respectively
     */
    @Test
    public void shouldReturnMaximumPositionConfidenceSet() {
        
        Integer testInput = 15;
        
        J2735PositionConfidence expectedPositionValue = J2735PositionConfidence.a1cm;
        J2735ElevationConfidence expectedElevationValue = J2735ElevationConfidence.elev_000_01;
        
        PositionConfidence testPositionConfidence = new PositionConfidence(testInput);
        ElevationConfidence testElevationConfidence = new ElevationConfidence(testInput);
        
        PositionConfidenceSet testPositionConfidenceSet =
                new PositionConfidenceSet(testPositionConfidence, testElevationConfidence);
        
        J2735PositionConfidenceSet actualJ2735PositionConfidenceSet = 
                OssPositionConfidenceSet.genericPositionConfidenceSet(testPositionConfidenceSet);
        
        assertEquals("Exected PositionConfidence value a1cm",
                expectedPositionValue,
                actualJ2735PositionConfidenceSet.pos);
        
        assertEquals("Expected ElevationConfidence value elev_000_01",
                expectedElevationValue,
                actualJ2735PositionConfidenceSet.elevation);
        
    }
    
    /**
     * Test that a position value (-1) below the lower bound (0) throws IllegalArgumentException
     * 
     * Elevation is an independent variable for this test and is set to 0
     */
    @Test
    public void shouldThrowExceptionPositionConfidenceSetPositionBelowLowerBound() {
        
        Integer testInput = -1;
        
        PositionConfidence testPositionConfidence = new PositionConfidence(testInput);
        ElevationConfidence testElevationConfidence = new ElevationConfidence(0);
        
        PositionConfidenceSet testPositionConfidenceSet =
                new PositionConfidenceSet(testPositionConfidence, testElevationConfidence);
        
        try {
            J2735PositionConfidenceSet actualJ2735PositionConfidenceSet =
                    OssPositionConfidenceSet.genericPositionConfidenceSet(testPositionConfidenceSet);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
    }
    
    /**
     * Test that an elevation value (-1) below the lower bound (0) throws IllegalArgumentException
     * 
     * Position is an independent variable for this test and is set to 0
     */
    @Test
    public void shouldThrowExceptionPositionConfidenceSetElevationBelowLowerBound() {
        
        Integer testInput = -1;
        
        PositionConfidence testPositionConfidence = new PositionConfidence(0);
        ElevationConfidence testElevationConfidence = new ElevationConfidence(testInput);
        
        PositionConfidenceSet testPositionConfidenceSet =
                new PositionConfidenceSet(testPositionConfidence, testElevationConfidence);
        
        try {
            J2735PositionConfidenceSet actualJ2735PositionConfidenceSet =
                    OssPositionConfidenceSet.genericPositionConfidenceSet(testPositionConfidenceSet);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
    }
    
    /**
     * Test that a position value (16) above the upper bound (15) throws IllegalArgumentException
     * 
     * Elevation is an independent variable for this test and is set to 0
     */
    @Test
    public void shouldThrowExceptionPositionConfidenceSetPositionAboveUpperBound() {
        
        Integer testInput = 16;
        
        PositionConfidence testPositionConfidence = new PositionConfidence(testInput);
        ElevationConfidence testElevationConfidence = new ElevationConfidence(0);
        
        PositionConfidenceSet testPositionConfidenceSet =
                new PositionConfidenceSet(testPositionConfidence, testElevationConfidence);
        
        try {
            J2735PositionConfidenceSet actualJ2735PositionConfidenceSet =
                    OssPositionConfidenceSet.genericPositionConfidenceSet(testPositionConfidenceSet);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
    }
    
    /**
     * Test that a elevation value (16) above the upper bound (15) throws IllegalArgumentException
     * 
     * Position is an independent variable for this test and is set to 0
     */
    @Test
    public void shouldThrowExceptionPositionConfidenceSetElevationAboveUpperBound() {
        
        Integer testInput = 16;
        
        PositionConfidence testPositionConfidence = new PositionConfidence(0);
        ElevationConfidence testElevationConfidence = new ElevationConfidence(testInput);
        
        PositionConfidenceSet testPositionConfidenceSet = 
                new PositionConfidenceSet(testPositionConfidence, testElevationConfidence);
        
        try {
            J2735PositionConfidenceSet actualJ2735PositionConfidenceSet =
                    OssPositionConfidenceSet.genericPositionConfidenceSet(testPositionConfidenceSet);
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
    }
    

}
