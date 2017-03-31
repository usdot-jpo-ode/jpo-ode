package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.WeatherReport;
import us.dot.its.jpo.ode.plugin.j2735.J2735EssPrecipSituation;
import us.dot.its.jpo.ode.plugin.j2735.J2735EssPrecipYesNo;
import us.dot.its.jpo.ode.plugin.j2735.J2735WeatherReport;

public class OssWeatherReport {

    private static final Integer RAIN_RATE_LOWER_BOUND = 0;
    private static final Integer RAIN_RATE_UPPER_BOUND = 65535;
    private static final Integer SOLAR_RAD_LOWER_BOUND = 0;
    private static final Integer SOLAR_RAD_UPPER_BOUND = 65535;
    private static final Integer FRICTION_LOWER_BOUND = 0;
    private static final Integer FRICTION_UPPER_BOUND = 101;
    private static final Integer ROAD_FRICTION_LOWER_BOUND = 0;
    private static final Integer ROAD_FRICTION_UPPER_BOUND = 50;
    
    private OssWeatherReport() {}

    public static J2735WeatherReport genericWeatherReport(WeatherReport weatherReport) {
        J2735WeatherReport gwr = new J2735WeatherReport();

        // Required element
        gwr.setIsRaining(J2735EssPrecipYesNo.values()[weatherReport.isRaining.indexOf()]);

        // Optional elements
        if (weatherReport.friction != null) {
            
            if (weatherReport.friction.intValue() < FRICTION_LOWER_BOUND
                    || weatherReport.friction.intValue() > FRICTION_UPPER_BOUND) {
                throw new IllegalArgumentException("Friction value out of bounds [0..101]");
            }
            gwr.setFriction(weatherReport.friction != null && weatherReport.friction.intValue() >= 0
                    && weatherReport.friction.intValue() <= 100 ? weatherReport.friction.intValue() : null);
        }
        if (weatherReport.precipSituation != null) {
            gwr.setPrecipSituation(J2735EssPrecipSituation.values()[weatherReport.precipSituation.indexOf()]);
        }
        if (weatherReport.rainRate != null) {

            if (weatherReport.rainRate.intValue() < RAIN_RATE_LOWER_BOUND
                    || weatherReport.rainRate.intValue() > RAIN_RATE_UPPER_BOUND) {
                throw new IllegalArgumentException("Rain rate out of bounds [0..65535]");
            }

            gwr.setRainRate(weatherReport.rainRate.longValue() != 65535
                    ? BigDecimal.valueOf(weatherReport.rainRate.longValue(), 1) : null);
        }
        /*
         * CoefficientOfFriction ::= INTEGER (0..50) -- where 0 = 0.00 micro
         * (frictionless), also used when data is unavailable -- and 50 = 1.00
         * micro, in steps of 0.02
         */
        if (weatherReport.roadFriction != null) {
            
            if (weatherReport.roadFriction.intValue() < ROAD_FRICTION_LOWER_BOUND
                    || weatherReport.roadFriction.intValue() > ROAD_FRICTION_UPPER_BOUND) {
                throw new IllegalArgumentException("Road friction value out of bounds [0..50]");
            }
            
            gwr.setRoadFriction(weatherReport.roadFriction != null && weatherReport.roadFriction.intValue() != 0
                    ? BigDecimal.valueOf(weatherReport.roadFriction.longValue() * 2, 2) : null);
        }
        if (weatherReport.solarRadiation != null) {
            
            if (weatherReport.solarRadiation.intValue() < SOLAR_RAD_LOWER_BOUND
                    || weatherReport.solarRadiation.intValue() > SOLAR_RAD_UPPER_BOUND) {
                throw new IllegalArgumentException("Solar radiation value out of bounds [0..65535]");
            }
            
            gwr.setSolarRadiation(weatherReport.solarRadiation.longValue() != 65535
                    ? weatherReport.solarRadiation.intValue() : null);
        }

        return gwr;
    }

}
