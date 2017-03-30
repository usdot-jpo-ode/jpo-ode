package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.Confidence;
import us.dot.its.jpo.ode.j2735.dsrc.PathPrediction;
import us.dot.its.jpo.ode.j2735.dsrc.RadiusOfCurvature;

/**
 * -- Summary --
 * JUnit test class for OssPathPrediction
 * 
 * Verifies correct conversion from generic PathPrediction to compliant-J2735PathPrediction
 * 
 * -- Documentation --
 * Data Frame: DF_PathPrediction
 * Use: The DF_PathPrediction data frame allows vehicles and other type of users to share their predicted path 
 * trajectory by estimating a future path of travel. This future trajectory estimation provides an indication of 
 * future positions of the transmitting vehicle and can significantly enhance in-lane and out-of-lane threat 
 * classification. Trajectories in the PathPrediction data element are represented by the RadiusOfCurvature element. 
 * The algorithmic approach and allowed error limits are defined in a relevant standard using the data frame. To 
 * help distinguish between steady state and non-steady state conditions, a confidence factor is included in the 
 * data element to provide an indication of signal accuracy due to rapid change in driver input. When driver input 
 * is in steady state (straight roadways or curves with a constant radius of curvature), a high confidence value is 
 * reported. During non-steady state conditions (curve transitions, lane changes, etc.), signal confidence is reduced.
 * ASN.1 Representation:
 *    PathPrediction ::= SEQUENCE {
 *       radiusOfCurve RadiusOfCurvature,
 *          -- LSB units of 10cm
 *          -- straight path to use value of 32767
 *       confidence Confidence,
 *          -- LSB units of 0.5 percent
 *       ...
 *       }
 * 
 * Data Element: DE_RadiusOfCurvature
 * Use: The entry DE_RadiusOfCurvature is a data element representing an estimate of the current trajectory of the 
 * sender. The value is represented as a first order of curvature approximation, as a circle with a radius R and an 
 * origin located at (0,R), where the x-axis is bore sight from the transmitting vehicle's perspective and normal 
 * to the vehicle's vertical axis. The vehicle's (x,y,z) coordinate frame follows the SAE convention. Radius R will 
 * be positive for curvatures to the right when observed from the transmitting vehicle's perspective. Radii shall 
 * be capped at a maximum value supported by the Path Prediction radius data type. Overflow of this data type shall 
 * be interpreted by the receiving vehicle as "a straight path" prediction. The radius can be derived from a number 
 * of sources including, but not limited to, map databases, rate sensors, vision systems, and global positioning. 
 * The precise algorithm to be used is outside the scope of this document.
 * ASN.1 Representation:
 *    RadiusOfCurvature ::= INTEGER (-32767..32767) 
 *       -- LSB units of 10cm 
 *       -- A straight path to use value of 32767
 * 
 * Data Element: DE_Confidence
 * Use: The entry DE_Confidence is a data element representing the general confidence of another associated value.
 * ASN.1 Representation:
 *    Confidence ::= INTEGER (0..200) 
 *       -- LSB units of 0.5 percent
 *
 */
public class OssPathPredictionTest {
    
    // RadiusOfCurvature tests
    /**
     * Test that a radius of curvature value (32767) indicating a straight path returns (0.0)
     */
    @Test
    public void shouldReturnStraightLineRadiusOfCurvature() {
        
        Integer testInput = 32767;
        BigDecimal expectedValue = BigDecimal.ZERO.setScale(1);
        
        RadiusOfCurvature testRadiusOfCurvature = new RadiusOfCurvature(testInput);
        Confidence testConfidence = new Confidence(0);
        
        PathPrediction testPathPrediction = new PathPrediction(testRadiusOfCurvature, testConfidence);
        
        BigDecimal actualValue = OssPathPrediction.genericPathPrediction(testPathPrediction).getRadiusOfCurve();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test minimum radius of curvature value (-32767) returns (-3276.7)
     */
    @Test
    public void shouldReturnMinimumRadiusOfCurvature() {
        
        Integer testInput = -32767;
        BigDecimal expectedValue = BigDecimal.valueOf(-3276.7);
        
        RadiusOfCurvature testRadiusOfCurvature = new RadiusOfCurvature(testInput);
        Confidence testConfidence = new Confidence(0);
        
        PathPrediction testPathPrediction = new PathPrediction(testRadiusOfCurvature, testConfidence);
        
        BigDecimal actualValue = OssPathPrediction.genericPathPrediction(testPathPrediction).getRadiusOfCurve();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case minimum radius of curvature value (-32766) returns (-3276.6)
     */
    @Test
    public void shouldReturnCornerCaseMinimumRadiusOfCurvature() {
        
        Integer testInput = -32766;
        BigDecimal expectedValue = BigDecimal.valueOf(-3276.6);
        
        RadiusOfCurvature testRadiusOfCurvature = new RadiusOfCurvature(testInput);
        Confidence testConfidence = new Confidence(0);
        
        PathPrediction testPathPrediction = new PathPrediction(testRadiusOfCurvature, testConfidence);
        
        BigDecimal actualValue = OssPathPrediction.genericPathPrediction(testPathPrediction).getRadiusOfCurve();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test that a known, middle radius of curvature value (-50) returns (-5.0)
     */
    @Test
    public void shouldReturnMiddleRadiusOfCurvature() {
        
        Integer testInput = -50;
        BigDecimal expectedValue = BigDecimal.valueOf(-5.0).setScale(1);
        
        RadiusOfCurvature testRadiusOfCurvature = new RadiusOfCurvature(testInput);
        Confidence testConfidence = new Confidence(0);
        
        PathPrediction testPathPrediction = new PathPrediction(testRadiusOfCurvature, testConfidence);
        
        BigDecimal actualValue = OssPathPrediction.genericPathPrediction(testPathPrediction).getRadiusOfCurve();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test that a corner case maximum radius of curvature value (32765) returns (3276.5)
     */
    @Test
    public void shouldReturnCornerCaseMaximumRadiusOfCurvature() {
        
        Integer testInput = 32765;
        BigDecimal expectedValue = BigDecimal.valueOf(3276.5);
        
        RadiusOfCurvature testRadiusOfCurvature = new RadiusOfCurvature(testInput);
        Confidence testConfidence = new Confidence(0);
        
        PathPrediction testPathPrediction = new PathPrediction(testRadiusOfCurvature, testConfidence);
        
        BigDecimal actualValue = OssPathPrediction.genericPathPrediction(testPathPrediction).getRadiusOfCurve();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test the maximum radius of curvature value (32766) returns (3276.6)
     */
    @Test
    public void shouldReturnMaximumRadiusOfCurvature() {
        
        Integer testInput = 32766;
        BigDecimal expectedValue = BigDecimal.valueOf(3276.6);
        
        RadiusOfCurvature testRadiusOfCurvature = new RadiusOfCurvature(testInput);
        Confidence testConfidence = new Confidence(0);
        
        PathPrediction testPathPrediction = new PathPrediction(testRadiusOfCurvature, testConfidence);
        
        BigDecimal actualValue = OssPathPrediction.genericPathPrediction(testPathPrediction).getRadiusOfCurve();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test that a radius of curvature value (-32768) below the lower bound (-32767)
     * is interpreted as a straight line and returned as (0)
     */
    @Test
    public void shouldReturnRadiusOfCurvatureBelowLowerBoundAsStraightLine() {
        
        Integer testInput = -32768;
        BigDecimal expectedValue = BigDecimal.ZERO.setScale(1);
        
        RadiusOfCurvature testRadiusOfCurvature = new RadiusOfCurvature(testInput);
        Confidence testConfidence = new Confidence(0);
        
        PathPrediction testPathPrediction = new PathPrediction(testRadiusOfCurvature, testConfidence);
        
        BigDecimal actualValue = OssPathPrediction.genericPathPrediction(testPathPrediction).getRadiusOfCurve();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test that a radius of curvature value (32768) above the upper bound (32767)
     * is interpreted as a straight line and returned as (0)
     */
    @Test
    public void shouldReturnRadiusOfCurvatureAboveUpperBoundAsStraightLine() {
        
        Integer testInput = 32768;
        BigDecimal expectedValue = BigDecimal.ZERO.setScale(1);
        
        RadiusOfCurvature testRadiusOfCurvature = new RadiusOfCurvature(testInput);
        Confidence testConfidence = new Confidence(0);
        
        PathPrediction testPathPrediction = new PathPrediction(testRadiusOfCurvature, testConfidence);
        
        BigDecimal actualValue = OssPathPrediction.genericPathPrediction(testPathPrediction).getRadiusOfCurve();
        
        assertEquals(expectedValue, actualValue);
    }
    
    // Confidence tests
    /**
     * Test minimum confidence value (0) returns (0.0)
     */
    @Test
    public void shouldReturnMinimumConfidence() {
        
        Integer testInput = 0;
        BigDecimal expectedValue = BigDecimal.ZERO.setScale(1);
        
        RadiusOfCurvature testRadiusOfCurvature = new RadiusOfCurvature(0);
        Confidence testConfidence = new Confidence(testInput);
        
        PathPrediction testPathPrediction = new PathPrediction(testRadiusOfCurvature, testConfidence);
        
        BigDecimal actualValue = OssPathPrediction.genericPathPrediction(testPathPrediction).getConfidence();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case minimum confidence value (1) returns (0.5)
     */
    @Test
    public void shouldReturnCornerCaseMinimumConfidence() {
        
        Integer testInput = 1;
        BigDecimal expectedValue = BigDecimal.valueOf(0.5);
        
        RadiusOfCurvature testRadiusOfCurvature = new RadiusOfCurvature(0);
        Confidence testConfidence = new Confidence(testInput);
        
        PathPrediction testPathPrediction = new PathPrediction(testRadiusOfCurvature, testConfidence);
        
        BigDecimal actualValue = OssPathPrediction.genericPathPrediction(testPathPrediction).getConfidence();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test known, middle confidence value (105) returns (52.5)
     */
    @Test
    public void shouldReturnMiddleConfidence() {
        
        Integer testInput = 105;
        BigDecimal expectedValue = BigDecimal.valueOf(52.5);
        
        RadiusOfCurvature testRadiusOfCurvature = new RadiusOfCurvature(0);
        Confidence testConfidence = new Confidence(testInput);
        
        PathPrediction testPathPrediction = new PathPrediction(testRadiusOfCurvature, testConfidence);
        
        BigDecimal actualValue = OssPathPrediction.genericPathPrediction(testPathPrediction).getConfidence();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case maximum confidence value (199) returns (99.5)
     */
    @Test
    public void shouldReturnCornerCaseMaximumConfidence() {
        
        Integer testInput = 199;
        BigDecimal expectedValue = BigDecimal.valueOf(99.5);
        
        RadiusOfCurvature testRadiusOfCurvature = new RadiusOfCurvature(0);
        Confidence testConfidence = new Confidence(testInput);
        
        PathPrediction testPathPrediction = new PathPrediction(testRadiusOfCurvature, testConfidence);
        
        BigDecimal actualValue = OssPathPrediction.genericPathPrediction(testPathPrediction).getConfidence();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test maximum confidence value (200) returns (100.0)
     */
    @Test
    public void shouldReturnMaximumConfidence() {
        
        Integer testInput = 200;
        BigDecimal expectedValue = BigDecimal.valueOf(100.0).setScale(1);
        
        RadiusOfCurvature testRadiusOfCurvature = new RadiusOfCurvature(0);
        Confidence testConfidence = new Confidence(testInput);
        
        PathPrediction testPathPrediction = new PathPrediction(testRadiusOfCurvature, testConfidence);
        
        BigDecimal actualValue = OssPathPrediction.genericPathPrediction(testPathPrediction).getConfidence();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test confidence value (-1) below the lower bound (0) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionConfidenceBelowLowerBound() {
        
        Integer testInput = -1;
        
        RadiusOfCurvature testRadiusOfCurvature = new RadiusOfCurvature(0);
        Confidence testConfidence = new Confidence(testInput);
        
        PathPrediction testPathPrediction = new PathPrediction(testRadiusOfCurvature, testConfidence);
        
        try {
            OssPathPrediction.genericPathPrediction(testPathPrediction).getConfidence();
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    /**
     * Test confidence value (201) above the upper bound (200) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionConfidenceAboveUpperBound() {
        
        Integer testInput = 201;
        
        RadiusOfCurvature testRadiusOfCurvature = new RadiusOfCurvature(0);
        Confidence testConfidence = new Confidence(testInput);
        
        PathPrediction testPathPrediction = new PathPrediction(testRadiusOfCurvature, testConfidence);
        
        try {
            OssPathPrediction.genericPathPrediction(testPathPrediction).getConfidence();
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
}
