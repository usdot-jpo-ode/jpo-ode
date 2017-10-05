package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735WeatherProbe;

public class WeatherProbeBuilder {

    private static final Integer TEMP_LOWER_BOUND = 0;
    private static final Integer TEMP_UPPER_BOUND = 191;
    private static final Integer PRESSURE_LOWER_BOUND = 0;
    private static final Integer PRESSURE_UPPER_BOUND = 255;

    private WeatherProbeBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735WeatherProbe genericWeatherProbe(JsonNode weatherProbe) {

        if (weatherProbe.hasAirTemp() && (weatherProbe.airTemp.intValue() < TEMP_LOWER_BOUND
                || weatherProbe.airTemp.intValue() > TEMP_UPPER_BOUND)) {
            throw new IllegalArgumentException("Ambient air temperature out of bounds [0..191]");
        }

        if (weatherProbe.hasAirPressure() && (weatherProbe.airPressure.intValue() < PRESSURE_LOWER_BOUND
                || weatherProbe.airPressure.intValue() > PRESSURE_UPPER_BOUND)) {
            throw new IllegalArgumentException("Ambient air pressure out of bounds [0..255]");
        }

        // Perform conversion
        J2735WeatherProbe gwp = new J2735WeatherProbe();

        if (weatherProbe.hasAirTemp() && weatherProbe.airTemp.intValue() != 191) {
            gwp.setAirTemp(weatherProbe.airTemp.intValue() - 40);
        } else {
            gwp.setAirTemp(null);
        }

        if (weatherProbe.hasAirPressure() && weatherProbe.airPressure.intValue() != 0) {
            gwp.setAirPressure(((weatherProbe.airPressure.intValue() - 1) * 2) + 580);
        } else {
            gwp.setAirPressure(null);
        }

        if (weatherProbe.hasRainRates()) {
            gwp.setRainRates(WiperSetBuilder.genericWiperSet(weatherProbe.rainRates));
        }

        return gwp;
    }

}
