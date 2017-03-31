package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.itis.GenericLocations;
import us.dot.its.jpo.ode.plugin.j2735.J2735NamedNumber;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssNamedNumber;

/**
 * JUnit test class for OssNamedNumber
 * 
 * Verifies that OssNamedNumber correctly returns String name and value of
 * generic locations
 * 
 * -- Test Criteria -- The ITIS enumeration list commonly referred to as
 * "Generic Locations," is assigned the upper byte value of [31] (which provides
 * for value ranges from 7936 to 8191, inclusive).
 * 
 * ASN.1 Representation: 
 *    GenericLocations ::= ENUMERATED { 
 *    -- Road Related
 *    on-bridges (7937), 
 *    -- Not to be used as the default for this 
 *    -- category
 *    in-tunnels (7938), 
 *    entering-or-leaving-tunnels (7939), 
 *    on-ramps (7940), 
 *    [shortened]
 *    northwest (8003), 
 *    southeast (8004), 
 *    southwest (8005), 
 *    ... 
 *    }
 */
public class OssNamedNumberTest {

    /**
     * Undefined (name, pair) test: Pass null generic location to the method and
     * expect null name back
     * 
     * Expected name: null Expected value: null
     */
    @Test(expected = NullPointerException.class)
    public void shouldReturnUndefinedNamedNumberGenericLocation() {

        GenericLocations gl = null;

        J2735NamedNumber testNamedNumber = OssNamedNumber.genericGenericLocations(gl);

        assertNull(testNamedNumber.getName());
        assertNull(testNamedNumber.getValue());

    }

    /**
     * Minimum known (name, value) pair test: Pass generic location name to the
     * method and expect correct value back Pass generic location number to the
     * method and expect correct name back
     * 
     * Expected name: "on-bridges" Expected value: 7937L
     */
    @Test
    public void shouldReturnMinimumNamedNumberGenericLocation() {

        String expectedName = "on-bridges";
        Long expectedValue = 7937L;

        GenericLocations glByName = GenericLocations.on_bridges;
        GenericLocations glByValue = GenericLocations.valueOf(7937L);

        J2735NamedNumber testNamedNumberByName = OssNamedNumber.genericGenericLocations(glByName);
        J2735NamedNumber testNamedNumberByValue = OssNamedNumber.genericGenericLocations(glByValue);

        assertEquals(expectedName, testNamedNumberByValue.getName());
        assertEquals(expectedValue, testNamedNumberByName.getValue());

    }

    /**
     * Maximum known (name, value) pair test: Pass generic location name to the
     * method and expect correct value back Pass generic location number to the
     * method and expect correct name back
     * 
     * Expected name: "roadside-park" Expected value: 8033L
     */
    @Test
    public void shouldReturnMaximumNamedNumberGenericLocation() {

        String expectedName = "roadside-park";
        Long expectedValue = 8033L;

        GenericLocations glByName = GenericLocations.roadside_park;
        GenericLocations glByValue = GenericLocations.valueOf(8033L);

        J2735NamedNumber testNamedNumberByName = OssNamedNumber.genericGenericLocations(glByName);
        J2735NamedNumber testNamedNumberByValue = OssNamedNumber.genericGenericLocations(glByValue);

        assertEquals(expectedName, testNamedNumberByValue.getName());
        assertEquals(expectedValue, testNamedNumberByName.getValue());

    }
    
    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<OssNamedNumber> constructor = OssNamedNumber.class.getDeclaredConstructor();
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
