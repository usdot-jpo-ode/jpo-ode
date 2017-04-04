package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.SpeedProfile;
import us.dot.its.jpo.ode.j2735.dsrc.SpeedProfileMeasurement;
import us.dot.its.jpo.ode.j2735.dsrc.SpeedProfileMeasurementList;

/**
 * -- Summary --
 * JUnit test class for OssSpeedProfile
 * 
 * Verifies correct conversion from generic SpeedProfile to compliant-J2735SpeedProfile
 * 
 * This is a trivial test class that checks that a SpeedProfile can be created
 * 
 * -- Documentation --
 * Data Frame: DF_SpeedProfile
 * 
 * Use: The DF_SpeedProfile data frame supports connected vehicles which will be collecting and parsing BSMs 
 * as they travel: these consist of speed data reported from the opposite direction. Each equipped vehicle collects 
 * the reported BSM speeds from the vehicles traveling in the opposite direction and store the average speed of 
 * these vehicles every 100 meters. The BSM tempID will be used to prevent duplicates. The opposite direction is 
 * considered to be the collecting vehicle's current direction +170 through 190 degrees. Up to 20 readings of 
 * average speed can be transmitted by the SpeedProfile. The SpeedProfile is added to the BSM Part II content, 
 * thus making it available to vehicles traveling in the opposite direction for whom it provides an up to 2 km 
 * SpeedProfile of the traffic on their road ahead. Should the vehicle collecting the SpeedProfile make a turn 
 * greater than 70°, then the SpeedProfile currently stored would be deleted. Further details of these operational 
 * concepts can be found in relevant standards.
 * ASN.1 Representation:
 * SpeedProfile ::= SEQUENCE { 
 *    -- Composed of set of measured average speeds
 *    speedReports SpeedProfileMeasurementList,
 *    ...
 *    }
 *    
 * Data Frame: DF_SpeedProfileMeasurementList
 * Use: The DF_SpeedProfileMeasurementList data frame consists of a list of SpeedProfileMeasurementList entries. 
 * The first value in the sequence would be the last measurement collected. If the sequence is full as a new 
 * measurement value is added, the oldest would be deleted.
 * ASN.1 Representation:
 *    SpeedProfileMeasurementList ::= SEQUENCE (SIZE(1..20)) OF SpeedProfileMeasurement
 *
 * Data Element: DE_SpeedProfileMeasurement
 * Use: The DE_SpeedProfileMeasurement data element represents the average measured or reported speed of a 
 * series of objects traveling in the same direction over a period of time.
 * ASN.1 Representation:
 *    SpeedProfileMeasurement ::= GrossSpeed
 * 
 * Data Element: DE_GrossSpeed
 * Use: The DE_GrossSpeed data element represents the velocity of an object, typically a vehicle speed, 
 * expressed in unsigned units of 1.00 meters per second. This data element is often used to represent traffic 
 * flow rates where precision is not of concern and where the major use cases involve reporting slow traffic flow. 
 * Note that Velocity as used here is intended to be a scalar value and not a vector.
 * ASN.1 Representation:
 *    GrossSpeed ::= INTEGER (0..31) -- Units of 1.00 m/s 
 *       -- The value 30 shall be used for speeds of 30 m/s or greater (67.1 mph) 
 *       -- The value 31 shall indicate that the speed is unavailable
 */
public class OssSpeedProfileTest {

    @Test
    public void shouldCreateSpeedProfileWithEmptySpeedProfileMeasurementList() {
        
        Integer testInput = 0;
        Integer expectedValue = 0;
        
        SpeedProfileMeasurement testSpeedProfileMeasurement = new SpeedProfileMeasurement(testInput);
        SpeedProfileMeasurementList testSpeedProfileMeasurementList = new SpeedProfileMeasurementList();
        testSpeedProfileMeasurementList.add(testSpeedProfileMeasurement);
        
        SpeedProfile testSpeedProfile = new SpeedProfile(testSpeedProfileMeasurementList);
        
        Integer actualValue = OssSpeedProfile.genericSpeedProfile(testSpeedProfile).getSpeedReports().get(0);
        
        assertEquals(expectedValue, actualValue);
        
    }  
    
    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<OssSpeedProfile> constructor = OssSpeedProfile.class.getDeclaredConstructor();
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
