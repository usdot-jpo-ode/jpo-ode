package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.Angle;
import us.dot.its.jpo.ode.j2735.dsrc.CoarseHeading;
import us.dot.its.jpo.ode.j2735.dsrc.Node_XY_24b;
import us.dot.its.jpo.ode.j2735.dsrc.Offset_B12;
import us.dot.its.jpo.ode.j2735.dsrc.TimeOffset;
import us.dot.its.jpo.ode.j2735.dsrc.TrailerHistoryPoint;
import us.dot.its.jpo.ode.j2735.dsrc.VertOffset_B07;
import us.dot.its.jpo.ode.plugin.j2735.J2735TrailerHistoryPoint;

/**
 * -- Summary --
 * JUnit test class for OssTrailerHistoryPoint
 * 
 * Verifies correct conversion from generic TrailerHistoryPoint to compliant-J2735TrailerHistoryPoint
 * 
 * This is a trivial test that verifies a mock J2735TrailerHistoryPoint object with known contents can be 
 * successfully created. All elements of this class are checked by other tests.
 * 
 * -- Documentation --
 * Data Frame: DF_TrailerHistoryPoint
 * Use: The DF_TrailerHistoryPoint data frame contains a single position point for a trailer, expressed relative 
 * to the vehicle’s BSM positional estimate at the same point in time.
 * ASN.1 Representation:
 *    TrailerHistoryPoint ::= SEQUENCE {
 *       pivotAngle Angle,
 *       -- angle with respect to the lead unit
 *       timeOffset TimeOffset,
 *       -- offset backwards in time -- Position relative to the hauling Vehicle
 *       positionOffset Node-XY-24b,
 *       elevationOffset VertOffset-B07 OPTIONAL,
 *       heading CoarseHeading OPTIONAL,
 *       -- overall heading
 *       ...
 *       }
 */
public class OssTrailerHistoryPointTest {

    @Test
    public void shouldCreateMockTrailerHistoryPoint() {
        
        TrailerHistoryPoint testthp = new TrailerHistoryPoint();
        testthp.setPivotAngle(new Angle(0));
        testthp.setTimeOffset(new TimeOffset(1));
        testthp.setPositionOffset(new Node_XY_24b(new Offset_B12(0), new Offset_B12(0)));
        testthp.setElevationOffset(new VertOffset_B07(0));
        testthp.setHeading(new CoarseHeading(0));
        
        try {
            J2735TrailerHistoryPoint actualthp = OssTrailerHistoryPoint.genericTrailerHistoryPoint(testthp);
            assertEquals("Incorrect angle", BigDecimal.ZERO.setScale(4), actualthp.getPivotAngle());
            assertEquals("Incorrect time offset", BigDecimal.valueOf(0.01), actualthp.getTimeOffset());
            assertEquals("Incorrect position offset x", BigDecimal.ZERO.setScale(2), actualthp.getPositionOffset().getX());
            assertEquals("Incorrect position offset y", BigDecimal.ZERO.setScale(2), actualthp.getPositionOffset().getY());
            assertEquals("Incorrect elevation offset", BigDecimal.ZERO.setScale(1), actualthp.getElevationOffset());
            assertEquals("Incorrect heading", BigDecimal.ZERO.setScale(1), actualthp.getHeading());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getClass());
        }
    }
    
    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<OssTrailerHistoryPoint> constructor = OssTrailerHistoryPoint.class.getDeclaredConstructor();
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
