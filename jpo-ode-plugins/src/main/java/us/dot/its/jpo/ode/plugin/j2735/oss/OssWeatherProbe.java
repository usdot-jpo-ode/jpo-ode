package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.WeatherProbe;
import us.dot.its.jpo.ode.plugin.j2735.J2735WeatherProbe;

public class OssWeatherProbe {

    private static final Integer TEMP_LOWER_BOUND = 0;
    private static final Integer TEMP_UPPER_BOUND = 191;
    private static final Integer PRESSURE_LOWER_BOUND = 0;
    private static final Integer PRESSURE_UPPER_BOUND = 255;

    public static J2735WeatherProbe genericWeatherProbe(WeatherProbe weatherProbe) {

        // Verify bounds of optional members
        if (weatherProbe.hasAirTemp()) {
            if (weatherProbe.airTemp.intValue() < TEMP_LOWER_BOUND
                    || weatherProbe.airTemp.intValue() > TEMP_UPPER_BOUND) {
                throw new IllegalArgumentException("Ambient air temperature out of bounds [0..191]");
            }
        }

        if (weatherProbe.hasAirPressure()) {
            if (weatherProbe.airPressure.intValue() < PRESSURE_LOWER_BOUND
                    || weatherProbe.airPressure.intValue() > PRESSURE_UPPER_BOUND) {
                throw new IllegalArgumentException("Ambient air pressure out of bounds [0..255]");
            }
        }

        // Perform conversion
        J2735WeatherProbe gwp = new J2735WeatherProbe();

        if (weatherProbe.hasAirTemp() && weatherProbe.airTemp.intValue() != 191) {
            gwp.airTemp = weatherProbe.airTemp.intValue() - 40;
        } else {
            gwp.airTemp = null;
        }

        if (weatherProbe.hasAirPressure() && weatherProbe.airPressure.intValue() != 0) {
            gwp.airPressure = ((weatherProbe.airPressure.intValue() - 1) * 2) + 580;
        } else {
            gwp.airPressure = null;
        }

        gwp.rainRates = OssWiperSet.genericWiperSet(weatherProbe.rainRates);

        return gwp;
    }

}
