package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Map;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.AmbientAirPressure;
import us.dot.its.jpo.ode.j2735.dsrc.AmbientAirTemperature;
import us.dot.its.jpo.ode.j2735.dsrc.BasicVehicleClass;
import us.dot.its.jpo.ode.j2735.dsrc.BasicVehicleRole;
import us.dot.its.jpo.ode.j2735.dsrc.BumperHeight;
import us.dot.its.jpo.ode.j2735.dsrc.BumperHeights;
import us.dot.its.jpo.ode.j2735.dsrc.CoefficientOfFriction;
import us.dot.its.jpo.ode.j2735.dsrc.DDateTime;
import us.dot.its.jpo.ode.j2735.dsrc.DDay;
import us.dot.its.jpo.ode.j2735.dsrc.DHour;
import us.dot.its.jpo.ode.j2735.dsrc.DMinute;
import us.dot.its.jpo.ode.j2735.dsrc.DMonth;
import us.dot.its.jpo.ode.j2735.dsrc.DOffset;
import us.dot.its.jpo.ode.j2735.dsrc.DSecond;
import us.dot.its.jpo.ode.j2735.dsrc.DYear;
import us.dot.its.jpo.ode.j2735.dsrc.DisabledVehicle;
import us.dot.its.jpo.ode.j2735.dsrc.FuelType;
import us.dot.its.jpo.ode.j2735.dsrc.Iso3833VehicleType;
import us.dot.its.jpo.ode.j2735.dsrc.ObstacleDetection;
import us.dot.its.jpo.ode.j2735.dsrc.ObstacleDirection;
import us.dot.its.jpo.ode.j2735.dsrc.ObstacleDistance;
import us.dot.its.jpo.ode.j2735.dsrc.SupplementalVehicleExtensions;
import us.dot.its.jpo.ode.j2735.dsrc.TrailerWeight;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleClassification;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleData;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleHeight;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleMass;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleType;
import us.dot.its.jpo.ode.j2735.dsrc.VerticalAccelerationThreshold;
import us.dot.its.jpo.ode.j2735.dsrc.WeatherProbe;
import us.dot.its.jpo.ode.j2735.dsrc.WeatherReport;
import us.dot.its.jpo.ode.j2735.dsrc.WiperRate;
import us.dot.its.jpo.ode.j2735.dsrc.WiperSet;
import us.dot.its.jpo.ode.j2735.dsrc.WiperStatus;
import us.dot.its.jpo.ode.j2735.itis.GenericLocations;
import us.dot.its.jpo.ode.j2735.itis.ITIScodes;
import us.dot.its.jpo.ode.j2735.itis.IncidentResponseEquipment;
import us.dot.its.jpo.ode.j2735.itis.ResponderGroupAffected;
import us.dot.its.jpo.ode.j2735.itis.VehicleGroupAffected;
import us.dot.its.jpo.ode.j2735.ntcip.EssMobileFriction;
import us.dot.its.jpo.ode.j2735.ntcip.EssPrecipRate;
import us.dot.its.jpo.ode.j2735.ntcip.EssPrecipSituation;
import us.dot.its.jpo.ode.j2735.ntcip.EssPrecipYesNo;
import us.dot.its.jpo.ode.j2735.ntcip.EssSolarRadiation;
import us.dot.its.jpo.ode.plugin.j2735.J2735SupplementalVehicleExtensions;

/**
 * --Summary --
 * JUnit test class for OssBsmPart2Extension
 * 
 * Verifies correct creation of a J2735BsmPart2Extension object
 * 
 * SpeedProfile and RTCMpackage are tested by their respective test classes. RegionalExtension is omitted
 * 
 * -- Documentation --
 * See: DF_SupplementalVehicleExtensions
 */
public class OssBsmPart2ExtensionTest {
    
    /**
     * Test adding a basic vehicle class
     */
    @Test
    public void shouldCreateBasicVehicleClass() {
        
        Integer testInput = 0;

        SupplementalVehicleExtensions testsve = new SupplementalVehicleExtensions();
        testsve.setClassification(new BasicVehicleClass(testInput));
        
        J2735SupplementalVehicleExtensions actualValue = 
                (J2735SupplementalVehicleExtensions) OssBsmPart2Extension
                .genericSupplementalVehicleExtensions(testsve);
        
        assertEquals(testInput, actualValue.getClassification());
        
    }
    
    /**
     * Test adding a vehicle classification
     */
    @Test
    public void shouldCreateVehicleClassification() {
        
        Integer testBasicVehicleClass = 0;
        Integer expectedBasicVehicleClass = 0;
        
        Integer testBasicVehicleRole = 0;
        String expectedBasicVehicleRole = "basicVehicle";
        
        Integer testIso3833VehicleType = 0;
        Integer expectedVCIso3833VehicleType = 0;
        
        Integer testVehicleType = 0;
        String expectedVehicleType = "none";
        
        Integer testVehicleGroupAffected = 9217;
        String expectedVehicleGroupAffected = "all-vehicles";
        
        Integer testIncidentResponseEquipment = 9985;
        String expectedIncidentResponseEquipment = "ground-fire-suppression";
        
        Integer testResponderGroupAffected = 9729;
        String expectedResponderGroupAffected = "emergency_vehicle_units";
        
        Integer testFuelType = 0;
        String expectedFuelType = "unknownFuel";
        
        VehicleClassification testVehicleClassification = new VehicleClassification(
                new BasicVehicleClass(testBasicVehicleClass),
                new BasicVehicleRole(testBasicVehicleRole),
                new Iso3833VehicleType(testIso3833VehicleType),
                new VehicleType(testVehicleType),
                new VehicleGroupAffected(testVehicleGroupAffected),
                new IncidentResponseEquipment(testIncidentResponseEquipment),
                new ResponderGroupAffected(testResponderGroupAffected),
                new FuelType(testFuelType), 
                null);

        SupplementalVehicleExtensions testsve = new SupplementalVehicleExtensions();
        testsve.setClassDetails(testVehicleClassification);
        
        J2735SupplementalVehicleExtensions actualValue = 
                (J2735SupplementalVehicleExtensions) OssBsmPart2Extension
                .genericSupplementalVehicleExtensions(testsve);
        
        assertEquals("Incorrect BasicVehicleClass", expectedBasicVehicleClass, actualValue.getClassDetails().getKeyType());
        assertEquals("Incorrect BasicVehicleRole", expectedBasicVehicleRole, actualValue.getClassDetails().getRole().name());
        assertEquals("Incorrect Iso3883VehicleType", expectedVCIso3833VehicleType, actualValue.getClassDetails().getIso3883());
        assertEquals("Incorrect HpmsType", expectedVehicleType, actualValue.getClassDetails().getHpmsType().name());
        assertEquals("Incorrect VehicleGroupAffected", expectedVehicleGroupAffected, actualValue.getClassDetails().getVehicleType().getName());
        assertEquals("Incorrect IncidentResponseEquipment", expectedIncidentResponseEquipment, actualValue.getClassDetails().getResponseEquip().getName());
        assertEquals("Incorrect ResponderGroupAffected", expectedResponderGroupAffected, actualValue.getClassDetails().getResponderType().name());
        assertEquals("Incorrect FuelType", expectedFuelType, actualValue.getClassDetails().getFuelType().name());
    }
    
    /**
     * Test adding vehicle data
     */
    @Test
    public void shouldCreateVehicleData() {
       
        Integer testVehicleHeight = 0;
        BigDecimal expectedVehicleHeight = BigDecimal.ZERO.setScale(2);
        
        Integer testBumperHeightFront = 0;
        BigDecimal expectedBumperHeightFront = BigDecimal.ZERO.setScale(2);
        
        Integer testBumperHeightRear = 0;
        BigDecimal expectedBumperHeightRear = BigDecimal.ZERO.setScale(2);
        
        Integer testVehicleMass = 0;
        Integer expectedVehicleMass = 0;
        
        Integer testTrailerWeight = 0;
        Integer expectedTrailerWeight = 0;
        
        VehicleData testVehicleData = new VehicleData(
                new VehicleHeight(testVehicleHeight),
                new BumperHeights(
                        new BumperHeight(testBumperHeightFront), 
                        new BumperHeight(testBumperHeightRear)),
                new VehicleMass(testVehicleMass),
                new TrailerWeight(testTrailerWeight));
        
        SupplementalVehicleExtensions testsve = new SupplementalVehicleExtensions();
        testsve.setVehicleData(testVehicleData);
        
        J2735SupplementalVehicleExtensions actualValue = 
                (J2735SupplementalVehicleExtensions) OssBsmPart2Extension
                .genericSupplementalVehicleExtensions(testsve);
        
        assertEquals("Incorrect VehicleHeight", expectedVehicleHeight, actualValue.getVehicleData().getHeight());
        assertEquals("Incorrect front BumperHeight", expectedBumperHeightFront, actualValue.getVehicleData().getBumpers().getFront());
        assertEquals("Incorrect rear BumperHeight", expectedBumperHeightRear, actualValue.getVehicleData().getBumpers().getRear());
        assertEquals("Incorrect VehicleMass", expectedVehicleMass, actualValue.getVehicleData().getMass());
        assertEquals("Incorrect TrailerWeight", expectedTrailerWeight, actualValue.getVehicleData().getTrailerWeight());
    }
    
    /**
     * Test adding a weather report
     */
    @Test
    public void shouldCreateWeatherReport() {
        
        Integer testEssPrecipYesNo = 1;
        String expectedEssPrecipYesNo = "PRECIP";
        
        Integer testEssPrecipRate = 0;
        BigDecimal expectedEssPrecipRate = BigDecimal.ZERO.setScale(1);
        
        Integer testEssPrecipSituation = 1;
        String expectedEssPrecipSituation = "OTHER";
        
        Integer testEssSolarRadiation = 0;
        Integer expectedEssSolarRadiation = 0;
        
        Integer testEssMobileFriction = 0;
        Integer expectedEssMobileFriction = 0;
        
        Integer testCoefficientOfFriction = 1;
        BigDecimal expectedCoefficientOfFriction = BigDecimal.valueOf(0.02);
        
        WeatherReport testWeatherReport = new WeatherReport(
                new EssPrecipYesNo(testEssPrecipYesNo),
                new EssPrecipRate(testEssPrecipRate),
                new EssPrecipSituation(testEssPrecipSituation),
                new EssSolarRadiation(testEssSolarRadiation),
                new EssMobileFriction(testEssMobileFriction),
                new CoefficientOfFriction(testCoefficientOfFriction));
        
        SupplementalVehicleExtensions testsve = new SupplementalVehicleExtensions();
        testsve.setWeatherReport(testWeatherReport);
        
        J2735SupplementalVehicleExtensions actualValue = 
                (J2735SupplementalVehicleExtensions) OssBsmPart2Extension
                .genericSupplementalVehicleExtensions(testsve);
        
        assertEquals("Incorrect EssPrecipYesNo", expectedEssPrecipYesNo, actualValue.getWeatherReport().getIsRaining().name());
        assertEquals("Incorrect EssPrecipRate", expectedEssPrecipRate, actualValue.getWeatherReport().getRainRate());
        assertEquals("Incorrect EssPrecipSituation", expectedEssPrecipSituation, actualValue.getWeatherReport().getPrecipSituation().name());
        assertEquals("Incorrect EssSolarRadiation", expectedEssSolarRadiation, actualValue.getWeatherReport().getSolarRadiation());
        assertEquals("Incorrect EssMobileFriction", expectedEssMobileFriction, actualValue.getWeatherReport().getFriction());
        assertEquals("Incorrect CoefficientOfFriction", expectedCoefficientOfFriction, actualValue.getWeatherReport().getRoadFriction());
    }
    
    /**
     * Test adding a weather probe
     */
    @Test
    public void shouldCreateWeatherProbe() {
        
        Integer testAmbientAirTemperature = 0;
        Integer expectedAmbientAirTemperature = -40;
        
        Integer testAmbientAirPressure = 1;
        Integer expectedAmbientAirPressure = 580;
        
        Integer testWiperStatusFront = 0;
        String expectedWiperStatusFront = "UNAVAILABLE";
        
        Integer testWiperRateFront = 0;
        Integer expectedWiperRateFront = 0;
        
        Integer testWiperStatusRear = 0;
        String expectedWiperStatusRear = "UNAVAILABLE";
        
        Integer testWiperRateRear = 0;
        Integer expectedWiperRateRear = 0;
        
        WeatherProbe testWeatherProbe = new WeatherProbe(
                new AmbientAirTemperature(testAmbientAirTemperature),
                new AmbientAirPressure(testAmbientAirPressure),
                new WiperSet(
                        new WiperStatus(testWiperStatusFront),
                        new WiperRate(testWiperRateFront),
                        new WiperStatus(testWiperStatusRear),
                        new WiperRate(testWiperRateRear))
                );
        
        SupplementalVehicleExtensions testsve = new SupplementalVehicleExtensions();
        testsve.setWeatherProbe(testWeatherProbe);
        
        J2735SupplementalVehicleExtensions actualValue = 
                (J2735SupplementalVehicleExtensions) OssBsmPart2Extension
                .genericSupplementalVehicleExtensions(testsve);
        
        assertEquals("Incorrect AmbientAirTemperature", expectedAmbientAirTemperature, actualValue.getWeatherProbe().getAirTemp());
        assertEquals("Incorrect AmbientAirPressure", expectedAmbientAirPressure, actualValue.getWeatherProbe().getAirPressure());
        assertEquals("Incorrect front WiperStatus", expectedWiperStatusFront, actualValue.getWeatherProbe().getRainRates().getStatusFront().name());
        assertEquals("Incorrect front WiperRate", expectedWiperRateFront, actualValue.getWeatherProbe().getRainRates().getRateFront());
        assertEquals("Incorrect rear WiperStatus", expectedWiperStatusRear, actualValue.getWeatherProbe().getRainRates().getStatusRear().name());
        assertEquals("Incorrect rear WiperRate", expectedWiperRateRear, actualValue.getWeatherProbe().getRainRates().getRateRear());
    }
    
    /**
     * Test adding an obstacle detection
     */
    @Test
    public void shouldCreateObstacleDetection() {

        Integer testObstacleDistance = 0;
        Integer expectedObstacleDistance = 0;
        
        Integer testObstacleDirection = 0;
        BigDecimal expectedObstacleDirection = BigDecimal.ZERO.setScale(4);
        
        Integer testObstacleDescription = 532;
        Integer expectedObstacleDescription = 532;
        
        Integer testObstaclelocationDetails = 7937;
        String expectedObstacleLocationDetails = "on-bridges";
        
        Integer testYear = 2017;
        Integer expectedYear = 2017;
        
        Integer testMonth = 1;
        Integer expectedMonth = 1;
        
        Integer testDay = 10;
        Integer expectedDay = 10;
        
        Integer testHour = 12;
        Integer expectedHour = 12;
        
        Integer testMinute = 52;
        Integer expectedMinute = 52;
        
        Integer testSecond = 43;
        Integer expectedSecond = 43;
                
        Integer testOffset = 1;
        Integer expectedOffset = 1;
        
        Integer testVerticalAccelerationThreshold = 0b00001;
        String expectedVerticalAccelerationThreshold = "notEquipped";
        
        ObstacleDetection testObstacleDetection = new ObstacleDetection(
                new ObstacleDistance(testObstacleDistance),
                new ObstacleDirection(testObstacleDirection),
                new ITIScodes(testObstacleDescription),
                new GenericLocations(testObstaclelocationDetails),
                new DDateTime(
                        new DYear(testYear),
                        new DMonth(testMonth),
                        new DDay(testDay),
                        new DHour(testHour),
                        new DMinute(testMinute),
                        new DSecond(testSecond),
                        new DOffset(testOffset)),
                new VerticalAccelerationThreshold(new byte[]{testVerticalAccelerationThreshold.byteValue()}));
        
        SupplementalVehicleExtensions testsve = new SupplementalVehicleExtensions();
        testsve.setObstacle(testObstacleDetection);
        
        J2735SupplementalVehicleExtensions actualValue = 
                (J2735SupplementalVehicleExtensions) OssBsmPart2Extension
                .genericSupplementalVehicleExtensions(testsve);
        
        assertEquals("Incorrect ObstacleDistance", expectedObstacleDistance, actualValue.getObstacle().getObDist());
        assertEquals("Incorrect ObstacleDirection", expectedObstacleDirection, actualValue.getObstacle().getObDirect());
        assertEquals("Incorrect ObstacleDescription", expectedObstacleDescription, actualValue.getObstacle().getDescription());
        assertEquals("Incorrect LocationDetails", expectedObstacleLocationDetails, actualValue.getObstacle().getLocationDetails().getName());
        assertEquals("Incorrect Year", expectedYear, actualValue.getObstacle().getDateTime().getYear());
        assertEquals("Incorrect Month", expectedMonth, actualValue.getObstacle().getDateTime().getMonth());
        assertEquals("Incorrect Day", expectedDay, actualValue.getObstacle().getDateTime().getDay());
        assertEquals("Incorrect Hour", expectedHour, actualValue.getObstacle().getDateTime().getHour());
        assertEquals("Incorrect Minute", expectedMinute, actualValue.getObstacle().getDateTime().getMinute());
        assertEquals("Incorrect Second", expectedSecond, actualValue.getObstacle().getDateTime().getSecond());
        assertEquals("Incorrect TimeOffset", expectedOffset, actualValue.getObstacle().getDateTime().getOffset());
        for (Map.Entry<String, Boolean> curVal : actualValue.getObstacle().getVertEvent().entrySet()) { 
            if (curVal.getKey().equals(expectedVerticalAccelerationThreshold)) {
                assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
            } else {
                assertFalse("Expected" + curVal.getKey() + " to be false", curVal.getValue());
            }
                
        }
    }
    
    /**
     * Test adding a disabled vehicle report
     */
    @Test
    public void shouldCreateDisabledVehicle() {
        
        Integer testStatusDetails = 532;
        Integer expectedStatusDetails = 532;
        
        Integer testLocationDetails = 8026;
                String expectedLocationDetails = "on-curve";
                
        DisabledVehicle testDisabledVehicle = new DisabledVehicle(
                new ITIScodes(testStatusDetails),
                new GenericLocations(testLocationDetails));
        
        SupplementalVehicleExtensions testsve = new SupplementalVehicleExtensions();
        testsve.setStatus(testDisabledVehicle);
        
        J2735SupplementalVehicleExtensions actualValue = 
                (J2735SupplementalVehicleExtensions) OssBsmPart2Extension
                .genericSupplementalVehicleExtensions(testsve);
        
        assertEquals(expectedStatusDetails, actualValue.getStatus().getStatusDetails());
        assertEquals(expectedLocationDetails, actualValue.getStatus().getLocationDetails().getName());
    }
    
    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<OssBsmPart2Extension > constructor = OssBsmPart2Extension.class.getDeclaredConstructor();
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
