package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.Angle;
import us.dot.its.jpo.ode.j2735.dsrc.CoarseHeading;
import us.dot.its.jpo.ode.j2735.dsrc.IsDolly;
import us.dot.its.jpo.ode.j2735.dsrc.Node_XY_24b;
import us.dot.its.jpo.ode.j2735.dsrc.Offset_B11;
import us.dot.its.jpo.ode.j2735.dsrc.Offset_B12;
import us.dot.its.jpo.ode.j2735.dsrc.PivotPointDescription;
import us.dot.its.jpo.ode.j2735.dsrc.PivotingAllowed;
import us.dot.its.jpo.ode.j2735.dsrc.TimeOffset;
import us.dot.its.jpo.ode.j2735.dsrc.TrailerHistoryPoint;
import us.dot.its.jpo.ode.j2735.dsrc.TrailerHistoryPointList;
import us.dot.its.jpo.ode.j2735.dsrc.TrailerUnitDescription;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleLength;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleWidth;
import us.dot.its.jpo.ode.j2735.dsrc.VertOffset_B07;
import us.dot.its.jpo.ode.plugin.j2735.J2735TrailerUnitDescription;

/**
 * -- Summary --
 * JUnit test class for OssTrailerUnitDescription
 * 
 * Verifies correct conversion from generic TrailerUnitDescription to compliant-J2735TrailerUnitDescription
 * 
 * Notes:
 * - Tested elements: isDolly, width, length, crumbData
 * - Required element Front pivot is tested by OssPivotPointDescriptionTest
 * - Required element Position offset is tested by OssNode_XYTest
 * - The rest of the elements of this class are optional and tested by other test classes
 * 
 * -- Documentation --
 * Data Frame: DF_TrailerUnitDescription
 * Use: The DF_TrailerUnitDescription data frame provides a physical description for one trailer or a dolly 
 * element (called a unit), including details of how it connects with other elements fore and aft.
 * ASN.1 Representation:
 *    TrailerUnitDescription ::= SEQUENCE {
 *       isDolly IsDolly, -- if false this is a trailer
 *       width VehicleWidth,
 *       length VehicleLength,
 *       height VehicleHeight OPTIONAL,
 *       mass TrailerMass OPTIONAL,
 *       bumperHeights BumperHeights OPTIONAL,
 *       centerOfGravity VehicleHeight OPTIONAL, 
 *       -- The front pivot point of the unit
 *       frontPivot PivotPointDescription, 
 *       -- The rear pivot point connecting to the next element, 
 *       -- if present and used (implies another unit is connected)
 *       rearPivot PivotPointDescription OPTIONAL,
 *       -- Rear wheel pivot point center-line offset 
 *       -- measured from the rear of the above length
 *       rearWheelOffset Offset-B12 OPTIONAL,
 *       -- the effective center-line of the wheel set
 *       -- Current Position relative to the hauling Vehicle
 *       positionOffset Node-XY-24b,
 *       elevationOffset VertOffset-B07 OPTIONAL,
 *       -- Past Position history relative to the hauling Vehicle
 *       crumbData TrailerHistoryPointList OPTIONAL,
 *       ...
 *       }
 * 
 * Data Element: DE_IsDolly
 * Use: A DE_IsDolly data element is a flag which is set to true to indicate that the described element is a 
 * dolly type rather than a trailer type of object. It should be noted that dollies (like trailers) may or may 
 * not pivot at the front and back connection points, and that they do not carry cargo or placards. Dollies do 
 * have an outline and connection point offsets like a trailer. Dollies have some form of draw bar to connect 
 * to the power unit (the vehicle or trailer in front of it). The term “bogie” is also used for dolly in some 
 * markets. In this standard, there is no differentiation between a dolly for a full trailer and a semi-trailer 
 * or a converter dolly. The only difference between an A-dolly (single coupling point) and a C-dolly (a dolly
 * with two coupling points arranged side by side) is the way in which the pivoting flag is set. (As a rule a 
 * C-dolly does not pivot.)
 * ASN.1 Representation:
 *    IsDolly ::= BOOLEAN -- When false indicates a trailer unit
 * 
 * Data Element: DE_VehicleWidth
 * Use: The width of the vehicle expressed in centimeters, unsigned. The width shall be the widest point of 
 * the vehicle with all factory installed equipment. The value zero shall be sent when data is unavailable.
 * ASN.1 Representation:
 *    VehicleWidth ::= INTEGER (0..1023) -- LSB units are 1 cm with a range of >10 meters
 * 
 * Data Element: DE_VehicleLength
 * Use: The length of the vehicle measured from the edge of the front bumper to the edge of the rear bumper 
 * expressed in centimeters, unsigned. It should be noted that this value is often combined with a vehicle 
 * width value to form a data frame. The value zero shall be sent when data is unavailable.
 * ASN.1 Representation:
 *    VehicleLength ::= INTEGER (0.. 4095) -- LSB units of 1 cm with a range of >40 meters
 *
 */
public class OssTrailerUnitDescriptionTest {
    
    // isDolly tests
    /**
     * Test isDolly value (true) returns (true)
     */
    @Test
    public void shouldReturnIsDollyTrue() {
        
        Boolean testInput = true;
        Boolean expectedValue = true;
        
        IsDolly testIsDolly = new IsDolly(testInput);
       
        TrailerUnitDescription testtud = new TrailerUnitDescription();
        testtud.setIsDolly(testIsDolly);
        testtud.setWidth(new VehicleWidth(0));
        testtud.setLength(new VehicleLength(0));
        testtud.setFrontPivot(new PivotPointDescription(new Offset_B11(0), new Angle(0), new PivotingAllowed(true)));
        testtud.setPositionOffset(new Node_XY_24b(new Offset_B12(0), new Offset_B12(0)));
        
        Boolean actualValue = OssTrailerUnitDescription.genericTrailerUnitDescription(testtud).getIsDolly();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test isDolly value (false) returns (false)
     */
    @Test
    public void shouldReturnIsDollyFalse() {
        
        Boolean testInput = false;
        Boolean expectedValue = false;
        
        IsDolly testIsDolly = new IsDolly(testInput);
       
        TrailerUnitDescription testtud = new TrailerUnitDescription();
        testtud.setIsDolly(testIsDolly);
        testtud.setWidth(new VehicleWidth(0));
        testtud.setLength(new VehicleLength(0));
        testtud.setFrontPivot(new PivotPointDescription(new Offset_B11(0), new Angle(0), new PivotingAllowed(true)));
        testtud.setPositionOffset(new Node_XY_24b(new Offset_B12(0), new Offset_B12(0)));
        
        Boolean actualValue = OssTrailerUnitDescription.genericTrailerUnitDescription(testtud).getIsDolly();
        
        assertEquals(expectedValue, actualValue);
    }
    
    // width tests
    /**
     * Test width undefined flag value (0) returns (null)
     */
    @Test
    public void shouldReturnUndefinedVehicleWidth() {
        
        Integer testInput = 0;
        Integer expectedValue = null;
        
        VehicleWidth testVehicleWidth = new VehicleWidth(testInput);
       
        TrailerUnitDescription testtud = new TrailerUnitDescription();
        testtud.setIsDolly(new IsDolly(false));
        testtud.setWidth(testVehicleWidth);
        testtud.setLength(new VehicleLength(0));
        testtud.setFrontPivot(new PivotPointDescription(new Offset_B11(0), new Angle(0), new PivotingAllowed(true)));
        testtud.setPositionOffset(new Node_XY_24b(new Offset_B12(0), new Offset_B12(0)));
        
        Integer actualValue = OssTrailerUnitDescription.genericTrailerUnitDescription(testtud).getWidth();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test minimum vehicle width value (1) returns (1)
     */
    @Test
    public void shouldReturnMinimumVehicleWidth() {
        
        Integer testInput = 1;
        Integer expectedValue = 1;
        
        VehicleWidth testVehicleWidth = new VehicleWidth(testInput);
       
        TrailerUnitDescription testtud = new TrailerUnitDescription();
        testtud.setIsDolly(new IsDolly(false));
        testtud.setWidth(testVehicleWidth);
        testtud.setLength(new VehicleLength(0));
        testtud.setFrontPivot(new PivotPointDescription(new Offset_B11(0), new Angle(0), new PivotingAllowed(true)));
        testtud.setPositionOffset(new Node_XY_24b(new Offset_B12(0), new Offset_B12(0)));
        
        Integer actualValue = OssTrailerUnitDescription.genericTrailerUnitDescription(testtud).getWidth();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case minimum vehicle width value (2) returns (2)
     */
    @Test
    public void shouldReturnCornerCaseMinimumVehicleWidth() {
        
        Integer testInput = 2;
        Integer expectedValue = 2;
        
        VehicleWidth testVehicleWidth = new VehicleWidth(testInput);
       
        TrailerUnitDescription testtud = new TrailerUnitDescription();
        testtud.setIsDolly(new IsDolly(false));
        testtud.setWidth(testVehicleWidth);
        testtud.setLength(new VehicleLength(0));
        testtud.setFrontPivot(new PivotPointDescription(new Offset_B11(0), new Angle(0), new PivotingAllowed(true)));
        testtud.setPositionOffset(new Node_XY_24b(new Offset_B12(0), new Offset_B12(0)));
        
        Integer actualValue = OssTrailerUnitDescription.genericTrailerUnitDescription(testtud).getWidth();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test known, middle vehicle width value (437) returns (437)
     */
    @Test
    public void shouldReturnMiddleVehicleWidth() {
        
        Integer testInput = 437;
        Integer expectedValue = 437;
        
        VehicleWidth testVehicleWidth = new VehicleWidth(testInput);
       
        TrailerUnitDescription testtud = new TrailerUnitDescription();
        testtud.setIsDolly(new IsDolly(false));
        testtud.setWidth(testVehicleWidth);
        testtud.setLength(new VehicleLength(0));
        testtud.setFrontPivot(new PivotPointDescription(new Offset_B11(0), new Angle(0), new PivotingAllowed(true)));
        testtud.setPositionOffset(new Node_XY_24b(new Offset_B12(0), new Offset_B12(0)));
        
        Integer actualValue = OssTrailerUnitDescription.genericTrailerUnitDescription(testtud).getWidth();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case maximum vehicle width value (1022) returns (1022)
     */
    @Test
    public void shouldReturnCornerCaseMaximumVehicleWidth() {
        
        Integer testInput = 1022;
        Integer expectedValue = 1022;
        
        VehicleWidth testVehicleWidth = new VehicleWidth(testInput);
       
        TrailerUnitDescription testtud = new TrailerUnitDescription();
        testtud.setIsDolly(new IsDolly(false));
        testtud.setWidth(testVehicleWidth);
        testtud.setLength(new VehicleLength(0));
        testtud.setFrontPivot(new PivotPointDescription(new Offset_B11(0), new Angle(0), new PivotingAllowed(true)));
        testtud.setPositionOffset(new Node_XY_24b(new Offset_B12(0), new Offset_B12(0)));
        
        Integer actualValue = OssTrailerUnitDescription.genericTrailerUnitDescription(testtud).getWidth();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test maximum vehicle width value (1023) returns (1023)
     */
    @Test
    public void shouldReturnMaximumVehicleWidth() {
        
        Integer testInput = 1023;
        Integer expectedValue = 1023;
        
        VehicleWidth testVehicleWidth = new VehicleWidth(testInput);
       
        TrailerUnitDescription testtud = new TrailerUnitDescription();
        testtud.setIsDolly(new IsDolly(false));
        testtud.setWidth(testVehicleWidth);
        testtud.setLength(new VehicleLength(0));
        testtud.setFrontPivot(new PivotPointDescription(new Offset_B11(0), new Angle(0), new PivotingAllowed(true)));
        testtud.setPositionOffset(new Node_XY_24b(new Offset_B12(0), new Offset_B12(0)));
        
        Integer actualValue = OssTrailerUnitDescription.genericTrailerUnitDescription(testtud).getWidth();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test vehicle width value (-1) below lower bound (0) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionVehicleWidthBelowLowerBound() {
        
        Integer testInput = -1;
        
        VehicleWidth testVehicleWidth = new VehicleWidth(testInput);
       
        TrailerUnitDescription testtud = new TrailerUnitDescription();
        testtud.setIsDolly(new IsDolly(false));
        testtud.setWidth(testVehicleWidth);
        testtud.setLength(new VehicleLength(0));
        testtud.setFrontPivot(new PivotPointDescription(new Offset_B11(0), new Angle(0), new PivotingAllowed(true)));
        testtud.setPositionOffset(new Node_XY_24b(new Offset_B12(0), new Offset_B12(0)));
        
        try {
           OssTrailerUnitDescription.genericTrailerUnitDescription(testtud).getWidth();
           fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    /**
     * Test vehicle width value (1024) above upper bound (1023) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionVehicleWidthAboveUpperBound() {
        
        Integer testInput = 1024;
        
        VehicleWidth testVehicleWidth = new VehicleWidth(testInput);
       
        TrailerUnitDescription testtud = new TrailerUnitDescription();
        testtud.setIsDolly(new IsDolly(false));
        testtud.setWidth(testVehicleWidth);
        testtud.setLength(new VehicleLength(0));
        testtud.setFrontPivot(new PivotPointDescription(new Offset_B11(0), new Angle(0), new PivotingAllowed(true)));
        testtud.setPositionOffset(new Node_XY_24b(new Offset_B12(0), new Offset_B12(0)));
        
        try {
           OssTrailerUnitDescription.genericTrailerUnitDescription(testtud).getWidth();
           fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    // length tests
    /**
     * Test vehicle length undefined flag value (0) returns (null)
     */
    @Test
    public void shouldReturnUndefinedVehicleLength() {
        
        Integer testInput = 0;
        Integer expectedValue = null;
        
        VehicleLength testVehicleLength = new VehicleLength(testInput);
       
        TrailerUnitDescription testtud = new TrailerUnitDescription();
        testtud.setIsDolly(new IsDolly(false));
        testtud.setWidth(new VehicleWidth(0));
        testtud.setLength(testVehicleLength);
        testtud.setFrontPivot(new PivotPointDescription(new Offset_B11(0), new Angle(0), new PivotingAllowed(true)));
        testtud.setPositionOffset(new Node_XY_24b(new Offset_B12(0), new Offset_B12(0)));
        
        Integer actualValue = OssTrailerUnitDescription.genericTrailerUnitDescription(testtud).getLength();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test minimum vehicle length value (1) returns (1)
     */
    @Test
    public void shouldReturnMinimumVehicleLength() {
        
        Integer testInput = 1;
        Integer expectedValue = 1;
        
        VehicleLength testVehicleLength = new VehicleLength(testInput);
       
        TrailerUnitDescription testtud = new TrailerUnitDescription();
        testtud.setIsDolly(new IsDolly(false));
        testtud.setWidth(new VehicleWidth(0));
        testtud.setLength(testVehicleLength);
        testtud.setFrontPivot(new PivotPointDescription(new Offset_B11(0), new Angle(0), new PivotingAllowed(true)));
        testtud.setPositionOffset(new Node_XY_24b(new Offset_B12(0), new Offset_B12(0)));
        
        Integer actualValue = OssTrailerUnitDescription.genericTrailerUnitDescription(testtud).getLength();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case minimum vehicle length value (2) returns (2)
     */
    @Test
    public void shouldReturnCornerCaseMinimumVehicleLength() {
        
        Integer testInput = 2;
        Integer expectedValue = 2;
        
        VehicleLength testVehicleLength = new VehicleLength(testInput);
       
        TrailerUnitDescription testtud = new TrailerUnitDescription();
        testtud.setIsDolly(new IsDolly(false));
        testtud.setWidth(new VehicleWidth(0));
        testtud.setLength(testVehicleLength);
        testtud.setFrontPivot(new PivotPointDescription(new Offset_B11(0), new Angle(0), new PivotingAllowed(true)));
        testtud.setPositionOffset(new Node_XY_24b(new Offset_B12(0), new Offset_B12(0)));
        
        Integer actualValue = OssTrailerUnitDescription.genericTrailerUnitDescription(testtud).getLength();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test known, middle vehicle length value (2213) returns (2213)
     */
    @Test
    public void shouldReturnMiddleVehicleLength() {
        
        Integer testInput = 2213;
        Integer expectedValue = 2213;
        
        VehicleLength testVehicleLength = new VehicleLength(testInput);
       
        TrailerUnitDescription testtud = new TrailerUnitDescription();
        testtud.setIsDolly(new IsDolly(false));
        testtud.setWidth(new VehicleWidth(0));
        testtud.setLength(testVehicleLength);
        testtud.setFrontPivot(new PivotPointDescription(new Offset_B11(0), new Angle(0), new PivotingAllowed(true)));
        testtud.setPositionOffset(new Node_XY_24b(new Offset_B12(0), new Offset_B12(0)));
        
        Integer actualValue = OssTrailerUnitDescription.genericTrailerUnitDescription(testtud).getLength();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case maximum vehicle length value (4094) returns (4094)
     */
    @Test
    public void shouldReturnCornerCaseMaximumVehicleLength() {
        
        Integer testInput = 4094;
        Integer expectedValue = 4094;
        
        VehicleLength testVehicleLength = new VehicleLength(testInput);
       
        TrailerUnitDescription testtud = new TrailerUnitDescription();
        testtud.setIsDolly(new IsDolly(false));
        testtud.setWidth(new VehicleWidth(0));
        testtud.setLength(testVehicleLength);
        testtud.setFrontPivot(new PivotPointDescription(new Offset_B11(0), new Angle(0), new PivotingAllowed(true)));
        testtud.setPositionOffset(new Node_XY_24b(new Offset_B12(0), new Offset_B12(0)));
        
        Integer actualValue = OssTrailerUnitDescription.genericTrailerUnitDescription(testtud).getLength();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test maximum vehicle length value (4095) returns (4095)
     */
    @Test
    public void shouldReturnMaximumVehicleLength() {
        
        Integer testInput = 4095;
        Integer expectedValue = 4095;
        
        VehicleLength testVehicleLength = new VehicleLength(testInput);
       
        TrailerUnitDescription testtud = new TrailerUnitDescription();
        testtud.setIsDolly(new IsDolly(false));
        testtud.setWidth(new VehicleWidth(0));
        testtud.setLength(testVehicleLength);
        testtud.setFrontPivot(new PivotPointDescription(new Offset_B11(0), new Angle(0), new PivotingAllowed(true)));
        testtud.setPositionOffset(new Node_XY_24b(new Offset_B12(0), new Offset_B12(0)));
        
        Integer actualValue = OssTrailerUnitDescription.genericTrailerUnitDescription(testtud).getLength();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test vehicle length value (-1) below lower bound (0) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionVehicleLengthBelowLowerBound() {
        
        Integer testInput = -1;
        
        VehicleLength testVehicleLength = new VehicleLength(testInput);
       
        TrailerUnitDescription testtud = new TrailerUnitDescription();
        testtud.setIsDolly(new IsDolly(false));
        testtud.setWidth(new VehicleWidth(0));
        testtud.setLength(testVehicleLength);
        testtud.setFrontPivot(new PivotPointDescription(new Offset_B11(0), new Angle(0), new PivotingAllowed(true)));
        testtud.setPositionOffset(new Node_XY_24b(new Offset_B12(0), new Offset_B12(0)));
        
        try {
            OssTrailerUnitDescription.genericTrailerUnitDescription(testtud).getLength();
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    /**
     * Test vehicle length value (4096) above upper bound (4095) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionVehicleLengthAboveUpperBound() {
        
        Integer testInput = 4096;
        
        VehicleLength testVehicleLength = new VehicleLength(testInput);
       
        TrailerUnitDescription testtud = new TrailerUnitDescription();
        testtud.setIsDolly(new IsDolly(false));
        testtud.setWidth(new VehicleWidth(0));
        testtud.setLength(testVehicleLength);
        testtud.setFrontPivot(new PivotPointDescription(new Offset_B11(0), new Angle(0), new PivotingAllowed(true)));
        testtud.setPositionOffset(new Node_XY_24b(new Offset_B12(0), new Offset_B12(0)));
        
        try {
            OssTrailerUnitDescription.genericTrailerUnitDescription(testtud).getLength();
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    // crumbData tests
    /**
     * Test that a mock crumb data object can be added to a trailer unit description object
     * and that its contents can be correctly extracted
     */
    @Test
    public void shouldCreateMockCrumbData() {
        
        CoarseHeading testHeading = new CoarseHeading(15);
        BigDecimal expectedHeading = BigDecimal.valueOf(22.5);
        
        // Test input
        TrailerHistoryPoint testthp = new TrailerHistoryPoint();
        testthp.setPivotAngle(new Angle(0));
        testthp.setTimeOffset(new TimeOffset(1));
        testthp.setPositionOffset(new Node_XY_24b(new Offset_B12(0), new Offset_B12(0)));
        testthp.setElevationOffset(new VertOffset_B07(0));
        testthp.setHeading(testHeading);
        
        TrailerHistoryPointList testthpList = new TrailerHistoryPointList();
        testthpList.add(testthp);
        
        // Control variables
        TrailerUnitDescription testtud = new TrailerUnitDescription();
        testtud.setIsDolly(new IsDolly(false));
        testtud.setWidth(new VehicleWidth(0));
        testtud.setLength(new VehicleLength(0));
        testtud.setFrontPivot(new PivotPointDescription(new Offset_B11(0), new Angle(0), new PivotingAllowed(true)));
        testtud.setPositionOffset(new Node_XY_24b(new Offset_B12(0), new Offset_B12(0)));
        testtud.setCrumbData(testthpList);
        
        // Perform conversion
        J2735TrailerUnitDescription actualtud = OssTrailerUnitDescription.genericTrailerUnitDescription(testtud);
        
        assertEquals(1, actualtud.getCrumbData().size());
        assertEquals(expectedHeading, actualtud.getCrumbData().get(0).getHeading());
    }

}
