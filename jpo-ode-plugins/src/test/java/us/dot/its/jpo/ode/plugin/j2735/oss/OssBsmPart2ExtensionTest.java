package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.Ignore;
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
        
        assertEquals(testInput, actualValue.classification);
        
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
        
        assertEquals("Incorrect BasicVehicleClass", expectedBasicVehicleClass, actualValue.classDetails.keyType);
        assertEquals("Incorrect BasicVehicleRole", expectedBasicVehicleRole, actualValue.classDetails.role.name());
        assertEquals("Incorrect Iso3883VehicleType", expectedVCIso3833VehicleType, actualValue.classDetails.iso3883);
        assertEquals("Incorrect HpmsType", expectedVehicleType, actualValue.classDetails.hpmsType.name());
        assertEquals("Incorrect VehicleGroupAffected", expectedVehicleGroupAffected, actualValue.classDetails.vehicleType.name);
        assertEquals("Incorrect IncidentResponseEquipment", expectedIncidentResponseEquipment, actualValue.classDetails.responseEquip.name);
        assertEquals("Incorrect ResponderGroupAffected", expectedResponderGroupAffected, actualValue.classDetails.responderType.name());
        assertEquals("Incorrect FuelType", expectedFuelType, actualValue.classDetails.fuelType.name());
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
        
        assertEquals("Incorrect VehicleHeight", expectedVehicleHeight, actualValue.vehicleData.height);
        assertEquals("Incorrect front BumperHeight", expectedBumperHeightFront, actualValue.vehicleData.bumpers.front);
        assertEquals("Incorrect rear BumperHeight", expectedBumperHeightRear, actualValue.vehicleData.bumpers.rear);
        assertEquals("Incorrect VehicleMass", expectedVehicleMass, actualValue.vehicleData.mass);
        assertEquals("Incorrect TrailerWeight", expectedTrailerWeight, actualValue.vehicleData.trailerWeight);
    }
    
    /**
     * Test adding a weather report
     */
    @Test
    public void shouldCreateWeatherReport() {
        
        Integer testEssPrecipYesNo = 1;
        String expectedEssPrecipYesNo = "precip";
        
        Integer testEssPrecipRate = 0;
        BigDecimal expectedEssPrecipRate = BigDecimal.ZERO.setScale(1);
        
        Integer testEssPrecipSituation = 1;
        String expectedEssPrecipSituation = "other";
        
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
        
        assertEquals("Incorrect EssPrecipYesNo", expectedEssPrecipYesNo, actualValue.weatherReport.isRaining.name());
        assertEquals("Incorrect EssPrecipRate", expectedEssPrecipRate, actualValue.weatherReport.rainRate);
        assertEquals("Incorrect EssPrecipSituation", expectedEssPrecipSituation, actualValue.weatherReport.precipSituation.name());
        assertEquals("Incorrect EssSolarRadiation", expectedEssSolarRadiation, actualValue.weatherReport.solarRadiation);
        assertEquals("Incorrect EssMobileFriction", expectedEssMobileFriction, actualValue.weatherReport.friction);
        assertEquals("Incorrect CoefficientOfFriction", expectedCoefficientOfFriction, actualValue.weatherReport.roadFriction);
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
        String expectedWiperStatusFront = "unavailable";
        
        Integer testWiperRateFront = 0;
        Integer expectedWiperRateFront = 0;
        
        Integer testWiperStatusRear = 0;
        String expectedWiperStatusRear = "unavailable";
        
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
        
        assertEquals("Incorrect AmbientAirTemperature", expectedAmbientAirTemperature, actualValue.weatherProbe.airTemp);
        assertEquals("Incorrect AmbientAirPressure", expectedAmbientAirPressure, actualValue.weatherProbe.airPressure);
        assertEquals("Incorrect front WiperStatus", expectedWiperStatusFront, actualValue.weatherProbe.rainRates.statusFront.name());
        assertEquals("Incorrect front WiperRate", expectedWiperRateFront, actualValue.weatherProbe.rainRates.rateFront);
        assertEquals("Incorrect rear WiperStatus", expectedWiperStatusRear, actualValue.weatherProbe.rainRates.statusRear.name());
        assertEquals("Incorrect rear WiperRate", expectedWiperRateRear, actualValue.weatherProbe.rainRates.rateRear);
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
        
        assertEquals("Incorrect ObstacleDistance", expectedObstacleDistance, actualValue.obstacle.getObDist());
        assertEquals("Incorrect ObstacleDirection", expectedObstacleDirection, actualValue.obstacle.getObDirect());
        assertEquals("Incorrect ObstacleDescription", expectedObstacleDescription, actualValue.obstacle.getDescription());
        assertEquals("Incorrect LocationDetails", expectedObstacleLocationDetails, actualValue.obstacle.getLocationDetails().name);
        assertEquals("Incorrect Year", expectedYear, actualValue.obstacle.getDateTime().getYear());
        assertEquals("Incorrect Month", expectedMonth, actualValue.obstacle.getDateTime().getMonth());
        assertEquals("Incorrect Day", expectedDay, actualValue.obstacle.getDateTime().getDay());
        assertEquals("Incorrect Hour", expectedHour, actualValue.obstacle.getDateTime().getHour());
        assertEquals("Incorrect Minute", expectedMinute, actualValue.obstacle.getDateTime().getMinute());
        assertEquals("Incorrect Second", expectedSecond, actualValue.obstacle.getDateTime().getSecond());
        assertEquals("Incorrect TimeOffset", expectedOffset, actualValue.obstacle.getDateTime().getOffset());
        for (Map.Entry<String, Boolean> curVal : actualValue.obstacle.getVertEvent().entrySet()) { 
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
        
        assertEquals(expectedStatusDetails, actualValue.status.statusDetails);
        assertEquals(expectedLocationDetails, actualValue.status.locationDetails.name);
    }

}
