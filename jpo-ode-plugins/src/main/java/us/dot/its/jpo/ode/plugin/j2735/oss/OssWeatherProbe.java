package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.WeatherProbe;
import us.dot.its.jpo.ode.plugin.j2735.J2735WeatherProbe;

public class OssWeatherProbe {

	public static J2735WeatherProbe genericWeatherProbe(WeatherProbe weatherProbe) {
		J2735WeatherProbe gwp = new J2735WeatherProbe();
		
        // AmbientAirPressure ::= INTEGER (0..255) 
        //       -- 8 Bits in hPa starting at 580 with a resolution of 
        //       -- 2 hPa resulting in a range of 580 to 1090
		gwp.airPressure = (weatherProbe.airPressure != null ? 580 + (weatherProbe.airPressure.intValue() * 2) : null);
        // AmbientAirTemperature ::= INTEGER (0..191) -- in deg C with a -40 offset
		gwp.airTemp = (weatherProbe.airTemp != null ? weatherProbe.airTemp.intValue() - 40 : null);
		gwp.rainRates = OssWiperSet.genericWiperSet(weatherProbe.rainRates);
		
		return gwp ;
	}

}
