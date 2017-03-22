package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.BasicVehicleClass;
import us.dot.its.jpo.ode.j2735.dsrc.SupplementalVehicleExtensions;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleClassification;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleData;
import us.dot.its.jpo.ode.j2735.dsrc.WeatherProbe;
import us.dot.its.jpo.ode.j2735.dsrc.WeatherReport;
import us.dot.its.jpo.ode.j2735.ntcip.EssPrecipYesNo;
import us.dot.its.jpo.ode.plugin.j2735.J2735SupplementalVehicleExtensions;

/**
 * -- Summary --
 * JUnit test class for OssSupplementalVehicleExtensions
 * 
 * Verifies correct conversion from generic SupplementalVehicleExtensions object 
 * to compliant-J2735SupplementalVehicleExtensions
 * 
 * See OssBsmPart2Extension for specific object building tests
 * 
 * -- Documentation --
 * Data Frame: DF_SupplementalVehicleExtensions
 * Use: The DF_SupplementalVehicleExtensions data frame is used to send various optional additional information 
 * elements in the Part II BSM. The range of use cases supported by these elements is very broad and includes 
 * both additional V2V functionality and various V2I monitoring applications. A variety of "vehicle as probe" 
 * applications fit within this overall functionality as well. Further use cases and requirements are developed 
 * in relevant standards. It should be noted that the use of the regional extension mechanism here is intended 
 * to provide a means to develop experimental message content within this data frame.
 * ASN.1 Representation:
 *    SupplementalVehicleExtensions ::= SEQUENCE { 
 *       -- Note that VehicleEventFlags, ExteriorLights, 
 *       -- PathHistory, and PathPrediction are in VehicleSafetyExtensions
 *       -- Vehicle Type Classification Data
 *       classification BasicVehicleClass OPTIONAL,
 *       -- May be required to be present for non passenger vehicles
 *       classDetails VehicleClassification OPTIONAL,
 *       vehicleData VehicleData OPTIONAL,
 *       -- Various V2V Probe Data
 *       weatherReport WeatherReport OPTIONAL,
 *       weatherProbe WeatherProbe OPTIONAL,
 *       -- Detected Obstacle data
 *       obstacle ObstacleDetection OPTIONAL,
 *       -- Disabled Vehicle Report
 *       status DisabledVehicle OPTIONAL,
 *       -- Oncoming lane speed reporting
 *       speedProfile SpeedProfile OPTIONAL,
 *       -- Raw GNSS measurements
 *       theRTCM RTCMPackage OPTIONAL,
 *       regional SEQUENCE (SIZE(1..4)) OF RegionalExtension {{REGION.Reg-SupplementalVehicleExtensions}} OPTIONAL,
 *       ...
 *       }
 */
public class OssSupplementalVehicleExtensionsTest {

    /**
     * Test that the simplest mock supplemental vehicle extensions object with minimal content can be created
     */
    @Test
    public void shouldCreateMockSupplementalVehicleExtensions() {
        
        SupplementalVehicleExtensions testsve = new SupplementalVehicleExtensions();
        testsve.setClassification(new BasicVehicleClass());
        testsve.setClassDetails(new VehicleClassification());
        testsve.setVehicleData(new VehicleData());
        testsve.setWeatherReport(new WeatherReport(new EssPrecipYesNo(1)));
        testsve.setWeatherProbe(new WeatherProbe());
        
        try {
           J2735SupplementalVehicleExtensions actualsve = OssSupplementalVehicleExtensions
                   .genericSupplementalVehicleExtensions(testsve);
           assertEquals("precip", actualsve.getWeatherReport().getIsRaining().toString());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getClass());
        }
    }

}
