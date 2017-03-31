package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.AmbientAirPressure;
import us.dot.its.jpo.ode.j2735.dsrc.AmbientAirTemperature;
import us.dot.its.jpo.ode.j2735.dsrc.WeatherProbe;
import us.dot.its.jpo.ode.j2735.dsrc.WiperRate;
import us.dot.its.jpo.ode.j2735.dsrc.WiperSet;
import us.dot.its.jpo.ode.j2735.dsrc.WiperStatus;

/**
 * -- Summary --
 * JUnit test class for OssWeatherProbe
 * 
 * Tests correct conversion from generic WeatherProbe to compliant-J2735WeatherProbe
 * 
 * (WiperSet tests handled by OssWiperSetTest.class)
 * 
 * -- Documentation --
 * Data Frame: DF_WeatherProbe
 * Use: The DF_WeatherProbe data frame provides basic data on the air temperature and barometric pressure experienced
 * by a vehicle, as well as the current status of the wiper systems on the vehicle, including front and rear wiper 
 * systems (where equipped) to indicate coarse rainfall levels.
 * ASN.1 Representation:
 *    WeatherProbe ::= SEQUENCE {
 *       airTemp AmbientAirTemperature OPTIONAL,
 *       airPressure AmbientAirPressure OPTIONAL,
 *       rainRates WiperSet OPTIONAL,
 *       ...
 *       }
 *      
 * Data Element: DE_AmbientAirTemperature
 * Use: The DE_AmbientAirTemperature data element is used to relate the measured Ambient Air Temperature from a
 * vehicle or other device. Its measurement range and precision follows that defined by the relevant OBD-II standards. 
 * This provides for a precision of one degree Celsius and a range of -40 to +230 degrees. In this use we reduce the 
 * upper value allow to be +150 and to allow it to be encoded in a one octet value. The value of -40 deg C is encoded 
 * as zero and every degree above that increments the transmitted value by one, resulting in a transmission range of 
 * 0 to 191. Hence, a measurement value representing 25 degrees Celsius is transmitted as 40+25=65 or Hex 0x41.
 * ASN.1 Representation:
 *    AmbientAirTemperature ::= INTEGER (0..191) -- in deg C with a -40 offset
 *       -- The value 191 shall indicate an unknown value
 *
 * Data Element: DE_AmbientAirPressure (Barometric Pressure)
 * Use: The DE_AmbientAirPressure data element is used to relate the measured Ambient Pressure (Barometric Pressure)
 * from a vehicle or other device. The value of zero shall be used when not equipped. The value of one indicates a 
 * pressure of 580 hPa.
 * ASN.1 Representation:
 *    AmbientAirPressure ::= INTEGER (0..255)
 *       -- 8 Bits in hPa starting at 580 with a resolution of
 *       -- 2 hPa resulting in a range of 580 to 1088
 */
public class OssWeatherProbeTest {
    
    private WiperSet mockWiperSet = null;

    /**
     * Setup a mock wiper set object for use in testing
     */
    @Before
    public void setUp() {
        
        WiperStatus testWiperStatusFront = new WiperStatus(0);
        WiperStatus testWiperStatusRear = new WiperStatus(0);
        WiperRate testWiperRateFront = new WiperRate(0);
        WiperRate testWiperRateRear = new WiperRate(0);
        
        mockWiperSet = new WiperSet(
                testWiperStatusFront, 
                testWiperRateFront,
                testWiperStatusRear,
                testWiperRateRear);
        
    }
    
    // AmbientAirTemperature tests
    
    /**
     * Test that the ambient air temperature undefined flag value (191) returns (null)
     */
    @Test
    public void shouldReturnUnknownAmbientAirTemperature() {
        
        Integer testInput = 191;
        Integer expectedValue = null;
        
        AmbientAirTemperature testAmbientAirTemperature = new AmbientAirTemperature(testInput);
        AmbientAirPressure testAmbientAirPressure = new AmbientAirPressure(0);
        
        WeatherProbe testWeatherProbe = new WeatherProbe(
                testAmbientAirTemperature,
                testAmbientAirPressure,
                mockWiperSet);
        
        Integer actualValue = OssWeatherProbe.genericWeatherProbe(testWeatherProbe).getAirTemp();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that the minimum ambient air temperature value (0) returns (-40)
     */
    @Test
    public void shouldReturnMinimumAmbientAirTemperature() {

        Integer testInput = 0;
        Integer expectedValue = -40;
        
        AmbientAirTemperature testAmbientAirTemperature = new AmbientAirTemperature(testInput);
        AmbientAirPressure testAmbientAirPressure = new AmbientAirPressure(0);
        
        WeatherProbe testWeatherProbe = new WeatherProbe(
                testAmbientAirTemperature,
                testAmbientAirPressure,
                mockWiperSet);
        
        Integer actualValue = OssWeatherProbe.genericWeatherProbe(testWeatherProbe).getAirTemp();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that a corner case minimum ambient air temperature value (1) returns (-39)
     */
    @Test
    public void shouldReturnCornerCaseMinimumAmbientAirTemperature() {

        Integer testInput = 1;
        Integer expectedValue = -39;
        
        AmbientAirTemperature testAmbientAirTemperature = new AmbientAirTemperature(testInput);
        AmbientAirPressure testAmbientAirPressure = new AmbientAirPressure(0);
        
        WeatherProbe testWeatherProbe = new WeatherProbe(
                testAmbientAirTemperature,
                testAmbientAirPressure,
                mockWiperSet);
        
        Integer actualValue = OssWeatherProbe.genericWeatherProbe(testWeatherProbe).getAirTemp();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that a corner case maximum ambient air temperature value (189) returns (149)
     */
    @Test
    public void shouldReturnCornerCaseMaximumAmbientAirTemperature() {

        Integer testInput = 189;
        Integer expectedValue = 149;
        
        AmbientAirTemperature testAmbientAirTemperature = new AmbientAirTemperature(testInput);
        AmbientAirPressure testAmbientAirPressure = new AmbientAirPressure(0);
        
        WeatherProbe testWeatherProbe = new WeatherProbe(
                testAmbientAirTemperature,
                testAmbientAirPressure,
                mockWiperSet);
        
        Integer actualValue = OssWeatherProbe.genericWeatherProbe(testWeatherProbe).getAirTemp();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that the maximum ambient air temperature value (190) returns (150)
     */
    @Test
    public void shouldReturnMaximumAmbientAirTemperature() {

        Integer testInput = 190;
        Integer expectedValue = 150;
        
        AmbientAirTemperature testAmbientAirTemperature = new AmbientAirTemperature(testInput);
        AmbientAirPressure testAmbientAirPressure = new AmbientAirPressure(0);
        
        WeatherProbe testWeatherProbe = new WeatherProbe(
                testAmbientAirTemperature,
                testAmbientAirPressure,
                mockWiperSet);
        
        Integer actualValue = OssWeatherProbe.genericWeatherProbe(testWeatherProbe).getAirTemp();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that an ambient air temperature value (-1) below the lower bound (0) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionAmbientAirTemperatureBelowLowerBound() {

        Integer testInput = -1;
        
        AmbientAirTemperature testAmbientAirTemperature = new AmbientAirTemperature(testInput);
        AmbientAirPressure testAmbientAirPressure = new AmbientAirPressure(0);
        
        WeatherProbe testWeatherProbe = new WeatherProbe(
                testAmbientAirTemperature,
                testAmbientAirPressure,
                mockWiperSet);
        
        try {
            Integer actualValue = OssWeatherProbe.genericWeatherProbe(testWeatherProbe).getAirTemp();
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
    }
    
    /**
     * Test that an ambient air temperature value (192) above the upper bound (191) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionAmbientAirTemperatureAboveUpperBound() {

        Integer testInput = 192;
        
        AmbientAirTemperature testAmbientAirTemperature = new AmbientAirTemperature(testInput);
        AmbientAirPressure testAmbientAirPressure = new AmbientAirPressure(0);
        
        WeatherProbe testWeatherProbe = new WeatherProbe(
                testAmbientAirTemperature,
                testAmbientAirPressure,
                mockWiperSet);
        
        try {
            Integer actualValue = OssWeatherProbe.genericWeatherProbe(testWeatherProbe).getAirTemp();
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
    }
    
    // AmbientAirPressure tests
    
    /**
     * Test that the ambient air pressure undefined flag value (0) returns (null)
     */
    @Test
    public void shouldReturnUndefinedAmbientAirPressure() {
        
        Integer testInput = 0;
        Integer expectedValue = null;
        
        AmbientAirTemperature testAmbientAirTemperature = new AmbientAirTemperature(0);
        AmbientAirPressure testAmbientAirPressure = new AmbientAirPressure(testInput);
        
        WeatherProbe testWeatherProbe = new WeatherProbe(
                testAmbientAirTemperature,
                testAmbientAirPressure,
                mockWiperSet);
        
        Integer actualValue = OssWeatherProbe.genericWeatherProbe(testWeatherProbe).getAirPressure();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that the minimum ambient air pressure value (1) returns (580)
     */
    @Test
    public void shouldReturnMinimumAmbientAirPressure() {

        Integer testInput = 1;
        Integer expectedValue = 580;
        
        AmbientAirTemperature testAmbientAirTemperature = new AmbientAirTemperature(0);
        AmbientAirPressure testAmbientAirPressure = new AmbientAirPressure(testInput);
        
        WeatherProbe testWeatherProbe = new WeatherProbe(
                testAmbientAirTemperature,
                testAmbientAirPressure,
                mockWiperSet);
        
        Integer actualValue = OssWeatherProbe.genericWeatherProbe(testWeatherProbe).getAirPressure();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that the corner case minimum ambient air pressure value (2) returns (582)
     */
    @Test
    public void shouldReturnCornerCaseMinimumAmbientAirPressure() {

        Integer testInput = 2;
        Integer expectedValue = 582;
        
        AmbientAirTemperature testAmbientAirTemperature = new AmbientAirTemperature(0);
        AmbientAirPressure testAmbientAirPressure = new AmbientAirPressure(testInput);
        
        WeatherProbe testWeatherProbe = new WeatherProbe(
                testAmbientAirTemperature,
                testAmbientAirPressure,
                mockWiperSet);
        
        Integer actualValue = OssWeatherProbe.genericWeatherProbe(testWeatherProbe).getAirPressure();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that the corner case maximum ambient air pressure value (254) returns (1086)
     */
    @Test
    public void shouldReturnCornerCaseMaximumAmbientAirPressure() {

        Integer testInput = 254;
        Integer expectedValue = 1086;
        
        AmbientAirTemperature testAmbientAirTemperature = new AmbientAirTemperature(0);
        AmbientAirPressure testAmbientAirPressure = new AmbientAirPressure(testInput);
        
        WeatherProbe testWeatherProbe = new WeatherProbe(
                testAmbientAirTemperature,
                testAmbientAirPressure,
                mockWiperSet);
        
        Integer actualValue = OssWeatherProbe.genericWeatherProbe(testWeatherProbe).getAirPressure();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that the maximum ambient air pressure value (255) returns (1088)
     */
    @Test
    public void shouldReturnMaximumAmbientAirPressure() {

        Integer testInput = 255;
        Integer expectedValue = 1088;
        
        AmbientAirTemperature testAmbientAirTemperature = new AmbientAirTemperature(0);
        AmbientAirPressure testAmbientAirPressure = new AmbientAirPressure(testInput);
        
        WeatherProbe testWeatherProbe = new WeatherProbe(
                testAmbientAirTemperature,
                testAmbientAirPressure,
                mockWiperSet);
        
        Integer actualValue = OssWeatherProbe.genericWeatherProbe(testWeatherProbe).getAirPressure();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that an ambient air pressure value (-1) below the lower bound (0) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionAmbientAirPressureBelowLowerBound() {
        
        Integer testInput = -1;
        
        AmbientAirTemperature testAmbientAirTemperature = new AmbientAirTemperature(0);
        AmbientAirPressure testAmbientAirPressure = new AmbientAirPressure(testInput);
        
        WeatherProbe testWeatherProbe = new WeatherProbe(
                testAmbientAirTemperature,
                testAmbientAirPressure,
                mockWiperSet);
        
        try {
            Integer actualValue = OssWeatherProbe.genericWeatherProbe(testWeatherProbe).getAirPressure();
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
    }
    
    /**
     * Test that an ambient air pressure value (256) above the upper bound (255) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionAmbientAirPresureAboveUpperBound() {

        Integer testInput = 256;
        
        AmbientAirTemperature testAmbientAirTemperature = new AmbientAirTemperature(0);
        AmbientAirPressure testAmbientAirPressure = new AmbientAirPressure(testInput);
        
        WeatherProbe testWeatherProbe = new WeatherProbe(
                testAmbientAirTemperature,
                testAmbientAirPressure,
                mockWiperSet);
        
        try {
            Integer actualValue = OssWeatherProbe.genericWeatherProbe(testWeatherProbe).getAirPressure();
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
    }

}
