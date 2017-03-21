package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.CoefficientOfFriction;
import us.dot.its.jpo.ode.j2735.dsrc.WeatherReport;
import us.dot.its.jpo.ode.j2735.ntcip.EssMobileFriction;
import us.dot.its.jpo.ode.j2735.ntcip.EssPrecipRate;
import us.dot.its.jpo.ode.j2735.ntcip.EssPrecipSituation;
import us.dot.its.jpo.ode.j2735.ntcip.EssPrecipYesNo;
import us.dot.its.jpo.ode.j2735.ntcip.EssSolarRadiation;

/**
 * -- Summary --
 * JUnit test class for OssWeatherReport
 * 
 * Verifies correct conversion from generic WeatherReport to compliant-J2735WeatherReport
 * 
 * -- Documentation --
 * Data Frame: DF_WeatherReport
 * Use: The DF_WeatherReport data frame is used to convey weather measurments made by the sending device.
 * ASN.1 Representation:
 *    WeatherReport ::= SEQUENCE {
 *       isRaining NTCIP.EssPrecipYesNo,
 *       rainRate NTCIP.EssPrecipRate OPTIONAL,
 *       precipSituation NTCIP.EssPrecipSituation OPTIONAL,
 *       solarRadiation NTCIP.EssSolarRadiation OPTIONAL,
 *       friction NTCIP.EssMobileFriction OPTIONAL,
 *       roadFriction CoefficientOfFriction OPTIONAL,
 *       ...
 *       }
 * 
 * Data Element: ESS_EssPrecipYesNo_code [NTCIP]
 * Use: Indicates whether or not moisture is detected by the sensor.
 *    ASN.1 Representation:
 *       EssPrecipYesNo ::= ENUMERATED {precip (1), noPrecip (2), error (3)}
 * 
 * Data Element: ESS_EssPrecipRate_quantity [NTCIP]
 * Use: The rainfall, or water equivalent of snow, rate in tenths of grams per square meter per second. For rain, 
 * this is approximately to 0.36 mm/hr. A value of 65535 shall indicate an error condition or missing value.
 * ASN.1 Representation:
 *    EssPrecipRate ::= INTEGER (0..65535)
 *    
 * Data Element: ESS_EssPrecipSituation_code [NTCIP]
 * Use: Describes the weather situation in terms of precipitation.
 * ASN.1 Representation:
 *    EssPrecipSituation ::= ENUMERATED {
 *       other (1),
 *       unknown (2),
 *       noPrecipitation (3),
 *       unidentifiedSlight (4),
 *       unidentifiedModerate (5),
 *       unidentifiedHeavy (6),
 *       snowSlight (7),
 *       snowModerate (8),
 *       snowHeavy (9),
 *       rainSlight (10),
 *       rainModerate (11),
 *       rainHeavy (12),
 *       frozenPrecipitationSlight (13),
 *       frozenPrecipitationModerate (14),
 *       frozenPrecipitationHeavy (15)
 *       }
 * 
 * Data Element: ESS_EssSolarRadiation_quantity [NTCIP]
 * Use: The direct solar radiation integrated over the 24 hours preceding the observation in Joules, per square 
 * meter. A value of 65535 shall indicate a missing value.
 * ASN.1 Representation:
 *    EssSolarRadiation ::= INTEGER (0..65535)
 *    
 * Data Element: ESS_EssMobileFriction [NTCIP]
 * Use: Indicates measured coefficient of friction in percent. The value 101 shall indicate an error condition or 
 * missing value.
 * ASN.1 Representation:
 *    EssMobileFriction ::= INTEGER (0..101)
 * 
 * Data Element: DE_CoefficientOfFriction
 * Use: Coefficient of Friction of an object, typically a wheel in contact with the ground. This data element is 
 * typically used in sets where the value at each wheel is provided in turn as a measure of relative local traction.
 * ASN.1 Representation:
 *    CoefficientOfFriction ::= INTEGER (0..50) 
 *       -- where 0 = 0.00 micro (frictionless), also used when data is unavailable 
 *       -- and 50 = 1.00 micro, in steps of 0.02
 */
public class OssWeatherReportTest {

    // isRaining tests
    /**
     * Test that "isRaining" input value (1) returns (precip)
     */
    @Test
    public void shouldReturnIsRainingPrecip() {
        
        Integer testInput = 1;
        String expectedValue = "precip";
        
        EssPrecipYesNo testEssPrecipYesNo = new EssPrecipYesNo(testInput);
        WeatherReport testWeatherReport = new WeatherReport(testEssPrecipYesNo);
        
        String actualValue = OssWeatherReport
                .genericWeatherReport(testWeatherReport)
                .getIsRaining().toString();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that "isRaining" input value (2) returns (noPrecip)
     */
    @Test
    public void shouldReturnIsRainingNoPrecip() {
        
        Integer testInput = 2;
        String expectedValue = "noPrecip";
        
        EssPrecipYesNo testEssPrecipYesNo = new EssPrecipYesNo(testInput);
        WeatherReport testWeatherReport = new WeatherReport(testEssPrecipYesNo);
        
        String actualValue = OssWeatherReport
                .genericWeatherReport(testWeatherReport)
                .getIsRaining().toString();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test that "isRaining" input value (3) returns (error)
     */
    @Test
    public void shouldReturnIsRainingError() {
        
        Integer testInput = 3;
        String expectedValue = "error";
        
        EssPrecipYesNo testEssPrecipYesNo = new EssPrecipYesNo(testInput);
        WeatherReport testWeatherReport = new WeatherReport(testEssPrecipYesNo);
        
        String actualValue = OssWeatherReport
                .genericWeatherReport(testWeatherReport)
                .getIsRaining().toString();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    // rainRate tests
    /**
     * Test that "rainRate" input value (65535) returns (null)
     */
    @Test
    public void shouldReturnUndefinedRainRate() {
        
        Integer testInput = 65535;
        BigDecimal expectedValue = null;
        
        WeatherReport testWeatherReport = new WeatherReport();
        testWeatherReport.isRaining = new EssPrecipYesNo(1);
        testWeatherReport.rainRate = new EssPrecipRate(testInput);
        
        BigDecimal actualValue = OssWeatherReport.genericWeatherReport(testWeatherReport).getRainRate();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test minimum "rainRate" input value (0) returns (0)
     */
    @Test
    public void shouldReturnMinimumRainRate() {
        
        Integer testInput = 0;
        BigDecimal expectedValue = BigDecimal.ZERO.setScale(1);
        
        WeatherReport testWeatherReport = new WeatherReport();
        testWeatherReport.isRaining = new EssPrecipYesNo(1);
        testWeatherReport.rainRate = new EssPrecipRate(testInput);
        
        BigDecimal actualValue = OssWeatherReport.genericWeatherReport(testWeatherReport).getRainRate();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case minimum "rainRate" input value (1) returns (1)
     */
    @Test
    public void shouldReturnCornerCaseMinimumRainRate() {
        
        Integer testInput = 1;
        BigDecimal expectedValue = BigDecimal.valueOf(0.1);
        
        WeatherReport testWeatherReport = new WeatherReport();
        testWeatherReport.isRaining = new EssPrecipYesNo(1);
        testWeatherReport.rainRate = new EssPrecipRate(testInput);
        
        BigDecimal actualValue = OssWeatherReport.genericWeatherReport(testWeatherReport).getRainRate();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test known, middle "rainRate" input value (36811) returns (36811)
     */
    @Test
    public void shouldReturnMiddleRainRate() {
        
        Integer testInput = 36811;
        BigDecimal expectedValue = BigDecimal.valueOf(3681.1);
        
        WeatherReport testWeatherReport = new WeatherReport();
        testWeatherReport.isRaining = new EssPrecipYesNo(1);
        testWeatherReport.rainRate = new EssPrecipRate(testInput);
        
        BigDecimal actualValue = OssWeatherReport.genericWeatherReport(testWeatherReport).getRainRate();
        
        assertEquals(expectedValue, actualValue);        
    }
    
    /**
     * Test corner case maximum rain rate input value (65533) returns (65533)
     */
    @Test
    public void shouldReturnCornerCaseMaximumRainRate() {
        
        Integer testInput = 65533;
        BigDecimal expectedValue = BigDecimal.valueOf(6553.3);
        
        WeatherReport testWeatherReport = new WeatherReport();
        testWeatherReport.isRaining = new EssPrecipYesNo(1);
        testWeatherReport.rainRate = new EssPrecipRate(testInput);
        
        BigDecimal actualValue = OssWeatherReport.genericWeatherReport(testWeatherReport).getRainRate();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test maximum rain rate input value (65534) returns (65534)
     */
    @Test
    public void shouldReturnMaximumRainRate() {
        
        Integer testInput = 65534;
        BigDecimal expectedValue = BigDecimal.valueOf(6553.4);
        
        WeatherReport testWeatherReport = new WeatherReport();
        testWeatherReport.isRaining = new EssPrecipYesNo(1);
        testWeatherReport.rainRate = new EssPrecipRate(testInput);
        
        BigDecimal actualValue = OssWeatherReport.genericWeatherReport(testWeatherReport).getRainRate();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test rain rate value (-1) below lower bound (0) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionRainRateBelowLowerBound() {
        
        Integer testInput = -1;
        
        WeatherReport testWeatherReport = new WeatherReport();
        testWeatherReport.isRaining = new EssPrecipYesNo(1);
        testWeatherReport.rainRate = new EssPrecipRate(testInput);
        
        try {
            BigDecimal actualValue = OssWeatherReport.genericWeatherReport(testWeatherReport).getRainRate();
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    /**
     * Test rain rate value (65536) above the upper bound (65535) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionRainRateAboveUpperBound() {
        
        Integer testInput = 65536;
        
        WeatherReport testWeatherReport = new WeatherReport();
        testWeatherReport.isRaining = new EssPrecipYesNo(1);
        testWeatherReport.rainRate = new EssPrecipRate(testInput);
        
        try {
            BigDecimal actualValue = OssWeatherReport.genericWeatherReport(testWeatherReport).getRainRate();
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    // precipSituation tests
    /**
     * Test that "precipSituation" minimum value (1) returns (other)
     */
    @Test
    public void shouldReturnMinimumPrecipSituation() {
        
        Integer testInput = 1;
        String expectedValue = "other";
        
        WeatherReport testWeatherReport = new WeatherReport();
        testWeatherReport.isRaining = new EssPrecipYesNo(1);
        testWeatherReport.precipSituation = new EssPrecipSituation(testInput);
        
        String actualValue = OssWeatherReport
                .genericWeatherReport(testWeatherReport)
                .getPrecipSituation().toString();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test that "precipSituation" corner case minimum value (2) returns (unknown)
     */
    @Test
    public void shouldReturnCornerCaseMinimumPrecipSituation() {
        
        Integer testInput = 2;
        String expectedValue = "unknown";
        
        WeatherReport testWeatherReport = new WeatherReport();
        testWeatherReport.isRaining = new EssPrecipYesNo(1);
        testWeatherReport.precipSituation = new EssPrecipSituation(testInput);
        
        String actualValue = OssWeatherReport
                .genericWeatherReport(testWeatherReport)
                .getPrecipSituation().toString();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test that "precipSituation" known, middle value (8) returns (snowModerate)
     */
    @Test
    public void shouldReturnMiddlePrecipSituation() {
        
        Integer testInput = 8;
        String expectedValue = "snowModerate";
        
        WeatherReport testWeatherReport = new WeatherReport();
        testWeatherReport.isRaining = new EssPrecipYesNo(1);
        testWeatherReport.precipSituation = new EssPrecipSituation(testInput);
        
        String actualValue = OssWeatherReport
                .genericWeatherReport(testWeatherReport)
                .getPrecipSituation().toString();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test that "precipSituation" corner case maximum value (14) returns (frozenPrecipitationModerate)
     */
    @Test
    public void shouldReturnCornerCaseMaximumPrecipSituation() {
        
        Integer testInput = 14;
        String expectedValue = "frozenPrecipitationModerate";
        
        WeatherReport testWeatherReport = new WeatherReport();
        testWeatherReport.isRaining = new EssPrecipYesNo(1);
        testWeatherReport.precipSituation = new EssPrecipSituation(testInput);
        
        String actualValue = OssWeatherReport
                .genericWeatherReport(testWeatherReport)
                .getPrecipSituation().toString();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test that "precipSituation" maximum value (15) returns (frozenPrecipitationHeavy)
     */
    @Test
    public void shouldReturnMaximumPrecipSituation() {
        
        Integer testInput = 15;
        String expectedValue = "frozenPrecipitationHeavy";
        
        WeatherReport testWeatherReport = new WeatherReport();
        testWeatherReport.isRaining = new EssPrecipYesNo(1);
        testWeatherReport.precipSituation = new EssPrecipSituation(testInput);
        
        String actualValue = OssWeatherReport
                .genericWeatherReport(testWeatherReport)
                .getPrecipSituation().toString();
        
        assertEquals(expectedValue, actualValue);
    }

    // solarRadiation tests
    /**
     * Test undefined solar radiation flag value (65535) returns (null)
     */
    @Test
    public void shouldReturnUndefinedSolarRadiation() {
        
        Integer testInput = 65535;
        Integer expectedValue = null;
        
        WeatherReport testWeatherReport = new WeatherReport();
        testWeatherReport.isRaining = new EssPrecipYesNo(1);
        testWeatherReport.solarRadiation = new EssSolarRadiation(testInput);
        
        Integer actualValue = OssWeatherReport.genericWeatherReport(testWeatherReport).getSolarRadiation();
        
        assertEquals(expectedValue, actualValue);
        
    }
    
    /**
     * Test minimum solar radiation value (0) returns (0)
     */
    @Test
    public void shouldReturnMinimumSolarRadiation() {
        
        Integer testInput = 0;
        Integer expectedValue = 0;
        
        WeatherReport testWeatherReport = new WeatherReport();
        testWeatherReport.isRaining = new EssPrecipYesNo(1);
        testWeatherReport.solarRadiation = new EssSolarRadiation(testInput);
        
        Integer actualValue = OssWeatherReport.genericWeatherReport(testWeatherReport).getSolarRadiation();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case minimum radiation value (1) returns (1)
     */
    @Test
    public void shouldReturnCornerCaseMinimumSolarRadiation() {
        
        Integer testInput = 1;
        Integer expectedValue = 1;
        
        WeatherReport testWeatherReport = new WeatherReport();
        testWeatherReport.isRaining = new EssPrecipYesNo(1);
        testWeatherReport.solarRadiation = new EssSolarRadiation(testInput);
        
        Integer actualValue = OssWeatherReport.genericWeatherReport(testWeatherReport).getSolarRadiation();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner known, middle radiation value (35337) returns (35337)
     */
    @Test
    public void shouldReturnMiddleSolarRadiation() {
        
        Integer testInput = 35337;
        Integer expectedValue = 35337;
        
        WeatherReport testWeatherReport = new WeatherReport();
        testWeatherReport.isRaining = new EssPrecipYesNo(1);
        testWeatherReport.solarRadiation = new EssSolarRadiation(testInput);
        
        Integer actualValue = OssWeatherReport.genericWeatherReport(testWeatherReport).getSolarRadiation();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case maximum radiation value (65533) returns (65533)
     */
    @Test
    public void shouldReturnCornerCaseMaximumSolarRadiation() {
        
        Integer testInput = 65533;
        Integer expectedValue = 65533;
        
        WeatherReport testWeatherReport = new WeatherReport();
        testWeatherReport.isRaining = new EssPrecipYesNo(1);
        testWeatherReport.solarRadiation = new EssSolarRadiation(testInput);
        
        Integer actualValue = OssWeatherReport.genericWeatherReport(testWeatherReport).getSolarRadiation();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test maximum radiation value (65534) returns (65534)
     */
    @Test
    public void shouldReturnMaximumSolarRadiation() {
        
        Integer testInput = 65534;
        Integer expectedValue = 65534;
        
        WeatherReport testWeatherReport = new WeatherReport();
        testWeatherReport.isRaining = new EssPrecipYesNo(1);
        testWeatherReport.solarRadiation = new EssSolarRadiation(testInput);
        
        Integer actualValue = OssWeatherReport.genericWeatherReport(testWeatherReport).getSolarRadiation();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test solar radiation value (-1) below the lower bound (0) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionSolarRadiationBelowLowerBound() {
        
        Integer testInput = -1;
        
        WeatherReport testWeatherReport = new WeatherReport();
        testWeatherReport.isRaining = new EssPrecipYesNo(1);
        testWeatherReport.solarRadiation = new EssSolarRadiation(testInput);
        
        try {
            Integer actualValue = OssWeatherReport.genericWeatherReport(testWeatherReport).getSolarRadiation();
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    /**
     * Test solar radiation value (65536) above the upper bound (65535) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionSolarRadiationAboveUpperBound() {
        
        Integer testInput = 65536;
        
        WeatherReport testWeatherReport = new WeatherReport();
        testWeatherReport.isRaining = new EssPrecipYesNo(1);
        testWeatherReport.solarRadiation = new EssSolarRadiation(testInput);
        
        try {
            Integer actualValue = OssWeatherReport.genericWeatherReport(testWeatherReport).getSolarRadiation();
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }

    // friction tests
    /**
     * Test undefined friction value (101) returns (null)
     */
    @Test
    public void shouldReturnUndefinedFriction() {
        
        Integer testInput = 101;
        Integer expectedValue = null;
        
        WeatherReport testWeatherReport = new WeatherReport();
        testWeatherReport.isRaining = new EssPrecipYesNo(1);
        testWeatherReport.friction = new EssMobileFriction(testInput);
        
        Integer actualValue = OssWeatherReport.genericWeatherReport(testWeatherReport).getFriction();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test minimum friction value (0) returns (0)
     */
    @Test
    public void shouldReturnMinimumFriction() {
        
        Integer testInput = 0;
        Integer expectedValue = 0;
        
        WeatherReport testWeatherReport = new WeatherReport();
        testWeatherReport.isRaining = new EssPrecipYesNo(1);
        testWeatherReport.friction = new EssMobileFriction(testInput);
        
        Integer actualValue = OssWeatherReport.genericWeatherReport(testWeatherReport).getFriction();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case minimum friction value (1) returns (1)
     */
    @Test
    public void shouldReturnCornerCaseMinimumFriction() {
        
        Integer testInput = 1;
        Integer expectedValue = 1;
        
        WeatherReport testWeatherReport = new WeatherReport();
        testWeatherReport.isRaining = new EssPrecipYesNo(1);
        testWeatherReport.friction = new EssMobileFriction(testInput);
        
        Integer actualValue = OssWeatherReport.genericWeatherReport(testWeatherReport).getFriction();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test known, middle friction value (52) returns (52)
     */
    @Test
    public void shouldReturnMiddleFriction() {
        
        Integer testInput = 52;
        Integer expectedValue = 52;
        
        WeatherReport testWeatherReport = new WeatherReport();
        testWeatherReport.isRaining = new EssPrecipYesNo(1);
        testWeatherReport.friction = new EssMobileFriction(testInput);
        
        Integer actualValue = OssWeatherReport.genericWeatherReport(testWeatherReport).getFriction();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case maximum friction value (99) returns (99)
     */
    @Test
    public void shouldReturnCornerCaseMaximumFriction() {
        
        Integer testInput = 99;
        Integer expectedValue = 99;
        
        WeatherReport testWeatherReport = new WeatherReport();
        testWeatherReport.isRaining = new EssPrecipYesNo(1);
        testWeatherReport.friction = new EssMobileFriction(testInput);
        
        Integer actualValue = OssWeatherReport.genericWeatherReport(testWeatherReport).getFriction();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test maximum friction value (100) returns (100)
     */
    @Test
    public void shouldReturnMaximumFriction() {
        
        Integer testInput = 100;
        Integer expectedValue = 100;
        
        WeatherReport testWeatherReport = new WeatherReport();
        testWeatherReport.isRaining = new EssPrecipYesNo(1);
        testWeatherReport.friction = new EssMobileFriction(testInput);
        
        Integer actualValue = OssWeatherReport.genericWeatherReport(testWeatherReport).getFriction();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test friction value (-1) below lower bound (0) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionFrictionBelowLowerBound() {
        
        Integer testInput = -1;
        
        WeatherReport testWeatherReport = new WeatherReport();
        testWeatherReport.isRaining = new EssPrecipYesNo(1);
        testWeatherReport.friction = new EssMobileFriction(testInput);
        
        try {
            Integer actualValue = OssWeatherReport.genericWeatherReport(testWeatherReport).getFriction();
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    /**
     * Test friction value (102) above the upper bound (101) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionFrictionAboveUpperBound() {
        
        Integer testInput = 102;
        
        WeatherReport testWeatherReport = new WeatherReport();
        testWeatherReport.isRaining = new EssPrecipYesNo(1);
        testWeatherReport.friction = new EssMobileFriction(testInput);
        
        try {
            Integer actualValue = OssWeatherReport.genericWeatherReport(testWeatherReport).getFriction();
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    // roadFriction tests
    /**
     * Test minimum road friction value (0) returns (0.00)
     * OR minimum road friction unavailable flag value (0) returns (null)
     */
    @Test
    public void shouldReturnMinimumOrUndefinedRoadFriction() {
        
        Integer testInput = 0;
        BigDecimal expectedValue = BigDecimal.ZERO.setScale(2);
        
        WeatherReport testWeatherReport = new WeatherReport();
        testWeatherReport.isRaining = new EssPrecipYesNo(1);
        testWeatherReport.roadFriction = new CoefficientOfFriction(testInput);
        
        BigDecimal actualValue = OssWeatherReport.genericWeatherReport(testWeatherReport).getRoadFriction();
        
        assertTrue((expectedValue == actualValue) || (null == actualValue));
    }
    
    /**
     * Test corner case minimum road friction value (1) returns (0.02)
     */
    @Test
    public void shouldReturnCornerCaseRoadFriction() {
        
        Integer testInput = 1;
        BigDecimal expectedValue = BigDecimal.valueOf(0.02);
        
        WeatherReport testWeatherReport = new WeatherReport();
        testWeatherReport.isRaining = new EssPrecipYesNo(1);
        testWeatherReport.roadFriction = new CoefficientOfFriction(testInput);
        
        BigDecimal actualValue = OssWeatherReport.genericWeatherReport(testWeatherReport).getRoadFriction();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test known, middle road friction value (24) returns (0.48)
     */
    @Test
    public void shouldReturnMiddleRoadFriction() {
        
        Integer testInput = 24;
        BigDecimal expectedValue = BigDecimal.valueOf(0.48);
        
        WeatherReport testWeatherReport = new WeatherReport();
        testWeatherReport.isRaining = new EssPrecipYesNo(1);
        testWeatherReport.roadFriction = new CoefficientOfFriction(testInput);
        
        BigDecimal actualValue = OssWeatherReport.genericWeatherReport(testWeatherReport).getRoadFriction();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test corner case maximum road friction value (49) returns (0.98)
     */
    @Test
    public void shouldReturnCornerCaseMaximumRoadFriction() {
        
        Integer testInput = 49;
        BigDecimal expectedValue = BigDecimal.valueOf(0.98);
        
        WeatherReport testWeatherReport = new WeatherReport();
        testWeatherReport.isRaining = new EssPrecipYesNo(1);
        testWeatherReport.roadFriction = new CoefficientOfFriction(testInput);
        
        BigDecimal actualValue = OssWeatherReport.genericWeatherReport(testWeatherReport).getRoadFriction();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test maximum road friction value (50) returns (1.00)
     */
    @Test
    public void shouldReturnMaximumRoadFriction() {
        
        Integer testInput = 50;
        BigDecimal expectedValue = BigDecimal.valueOf(1.00).setScale(2);
        
        WeatherReport testWeatherReport = new WeatherReport();
        testWeatherReport.isRaining = new EssPrecipYesNo(1);
        testWeatherReport.roadFriction = new CoefficientOfFriction(testInput);
        
        BigDecimal actualValue = OssWeatherReport.genericWeatherReport(testWeatherReport).getRoadFriction();
        
        assertEquals(expectedValue, actualValue);
    }
    
    /**
     * Test road friction value (-1) below the lower bound (0) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionRoadFrictionBelowLowerBound() {
        
        Integer testInput = -1;
        
        WeatherReport testWeatherReport = new WeatherReport();
        testWeatherReport.isRaining = new EssPrecipYesNo(1);
        testWeatherReport.roadFriction = new CoefficientOfFriction(testInput);
        
        try {
            BigDecimal actualValue = OssWeatherReport.genericWeatherReport(testWeatherReport).getRoadFriction();
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }
    
    /**
     * Test road friction value (51) above the upper bound (50) throws IllegalArgumentException
     */
    @Test
    public void shouldThrowExceptionRoadFrictionAboveUpperBound() {
        
        Integer testInput = 51;
        
        WeatherReport testWeatherReport = new WeatherReport();
        testWeatherReport.isRaining = new EssPrecipYesNo(1);
        testWeatherReport.roadFriction = new CoefficientOfFriction(testInput);
        
        try {
            BigDecimal actualValue = OssWeatherReport.genericWeatherReport(testWeatherReport).getRoadFriction();
            fail("Expected IllegalArgumentException");
        } catch (RuntimeException e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }

}
