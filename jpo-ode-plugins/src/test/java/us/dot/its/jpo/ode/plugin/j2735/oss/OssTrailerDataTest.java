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
import us.dot.its.jpo.ode.j2735.dsrc.IsDolly;
import us.dot.its.jpo.ode.j2735.dsrc.Node_XY_24b;
import us.dot.its.jpo.ode.j2735.dsrc.Offset_B11;
import us.dot.its.jpo.ode.j2735.dsrc.Offset_B12;
import us.dot.its.jpo.ode.j2735.dsrc.PivotPointDescription;
import us.dot.its.jpo.ode.j2735.dsrc.PivotingAllowed;
import us.dot.its.jpo.ode.j2735.dsrc.SSPindex;
import us.dot.its.jpo.ode.j2735.dsrc.TimeOffset;
import us.dot.its.jpo.ode.j2735.dsrc.TrailerData;
import us.dot.its.jpo.ode.j2735.dsrc.TrailerHistoryPoint;
import us.dot.its.jpo.ode.j2735.dsrc.TrailerHistoryPointList;
import us.dot.its.jpo.ode.j2735.dsrc.TrailerUnitDescription;
import us.dot.its.jpo.ode.j2735.dsrc.TrailerUnitDescriptionList;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleLength;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleWidth;
import us.dot.its.jpo.ode.j2735.dsrc.VertOffset_B07;
import us.dot.its.jpo.ode.plugin.j2735.J2735TrailerData;

/**
 * -- Summary --
 * JUnit test class for OssTrailerData
 * 
 * Verifies correct conversion from generic TrailerData object to compliant-J2735TrailerData
 * 
 * This is a trivial test class to verify the object can be successfully created. All internal elements of this 
 * class are tested by other test classes.
 * 
 * -- Documentation --
 * Data Frame: DF_TrailerData
 * Use: The DF_TrailerData data frame provides a means to describe trailers pulled by a motor vehicle and/or other 
 * equipped devices. The span of use is intended to cover use cases from simple passenger vehicles with trailers 
 * to class 8 vehicles hauling one or more trailers and dollies. The information in this data frame (along with 
 * the BSM message in which it is sent) can be used to determine various aspects of the sender. These include 
 * the path of the vehicle and its trailer(s) under various maneuvering conditions (lane matching) as well as 
 * the rear of the final trailer, which is often useful in signal control optimization and in intersection safety. 
 * This data frame is typically used in the BSM Part II content.
 * ASN.1 Representation:
 *    TrailerData ::= SEQUENCE { -- CERT SSP Privilege Details
 *       sspRights SSPindex, -- index to CERT rights
 *       -- Offset connection point details from the -- hauling vehicle to the first trailer unit
 *       connection PivotPointDescription,
 *       -- One of more Trailer or Dolly Descriptions -- (each called a unit)
 *       units TrailerUnitDescriptionList,
 *       ...
 *       }
 */
public class OssTrailerDataTest {
    
    /**
     * Create a mock trailer data object with minimal, known contents and verify those contents can be
     * successfully and correctly retrieved.
     */
    @Test
    public void shouldCreateMockTrailerData() {
        
        // Create a mock TrailerHistoryPoint
        TrailerHistoryPoint testthp = new TrailerHistoryPoint();
        testthp.setPivotAngle(new Angle(0));
        testthp.setTimeOffset(new TimeOffset(1));
        testthp.setPositionOffset(new Node_XY_24b(new Offset_B12(0), new Offset_B12(0)));
        testthp.setElevationOffset(new VertOffset_B07(0));
        testthp.setHeading(new CoarseHeading(15));
        
        // Create a mock TrailerHistoryPointList and add the mock TrailerHistoryPoint
        TrailerHistoryPointList testthpList = new TrailerHistoryPointList();
        testthpList.add(testthp);
        
        // Create a mock TrailerUnitDescription and add the mock TrailerHistoryPointList
        TrailerUnitDescription testtud = new TrailerUnitDescription();
        testtud.setIsDolly(new IsDolly(false));
        testtud.setWidth(new VehicleWidth(0));
        testtud.setLength(new VehicleLength(0));
        testtud.setFrontPivot(new PivotPointDescription(new Offset_B11(0), new Angle(0), new PivotingAllowed(true)));
        testtud.setPositionOffset(new Node_XY_24b(new Offset_B12(0), new Offset_B12(0)));
        testtud.setCrumbData(testthpList);
        
        // Create a mock TrailerUnitDescriptionList and add the mock TrailerUnitDescription
        TrailerUnitDescriptionList testtudList = new TrailerUnitDescriptionList();
        testtudList.add(testtud);
        
        // Create a mock TrailerData and add the mock TrailerUnitDescriptionList
        TrailerData testTrailerData = new TrailerData();
        testTrailerData.setConnection(new PivotPointDescription(
                new Offset_B11(0), new Angle(0), new PivotingAllowed(true)));
        testTrailerData.setSspRights(new SSPindex(5));
        testTrailerData.setUnits(testtudList);
        
        // Perform conversion
        try {
           J2735TrailerData actualTrailerData = OssTrailerData.genericTrailerData(testTrailerData);
           
           assertEquals("Incorrect heading", 
                   BigDecimal.valueOf(22.5), 
                   actualTrailerData.getUnits().get(0).getCrumbData().get(0).getHeading());
           assertEquals("Incorrect dolly status", false, actualTrailerData.getUnits().get(0).getIsDolly().booleanValue());
           assertEquals("Incorrect SSP rights", 5, actualTrailerData.getSspRights().intValue());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getClass());
        }
    }
    
    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<OssTrailerData> constructor = OssTrailerData.class.getDeclaredConstructor();
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
