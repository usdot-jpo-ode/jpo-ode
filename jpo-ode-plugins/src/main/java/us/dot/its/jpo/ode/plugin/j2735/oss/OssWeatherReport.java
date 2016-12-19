package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.WeatherReport;
import us.dot.its.jpo.ode.plugin.j2735.J2735EssPrecipSituation;
import us.dot.its.jpo.ode.plugin.j2735.J2735EssPrecipYesNo;
import us.dot.its.jpo.ode.plugin.j2735.J2735WeatherReport;

public class OssWeatherReport {

	public static J2735WeatherReport genericWeatherReport(WeatherReport weatherReport) {
		J2735WeatherReport gwr = new J2735WeatherReport();
		
		gwr.friction = weatherReport.friction != null 
				&& weatherReport.friction.intValue() >= 0
				&& weatherReport.friction.intValue() <= 100 ? weatherReport.friction.intValue() : null; 
		gwr.isRaining = J2735EssPrecipYesNo.values()[weatherReport.isRaining.indexOf()];
		gwr.precipSituation = J2735EssPrecipSituation.values()[weatherReport.precipSituation.indexOf()];
		gwr.rainRate = weatherReport.rainRate.longValue() != 65535 ? BigDecimal.valueOf(weatherReport.rainRate.longValue(), 1) : null;
		/*
		 * CoefficientOfFriction ::= INTEGER (0..50) 
		 * -- where 0 = 0.00 micro (frictionless), also used when data is unavailable 
		 * -- and 50 = 1.00 micro, in steps of 0.02
		 */
		gwr.roadFriction = weatherReport.roadFriction != null
				&& weatherReport.roadFriction.intValue() != 0
				? BigDecimal.valueOf(weatherReport.roadFriction.longValue() * 2, 2) : null;
				
		gwr.solarRadiation = weatherReport.solarRadiation.longValue() != 65535 ? weatherReport.solarRadiation.intValue() : null;
		
		return gwr ;
	}

}
