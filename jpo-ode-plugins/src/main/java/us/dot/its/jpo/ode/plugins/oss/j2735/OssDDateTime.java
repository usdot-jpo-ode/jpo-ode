package us.dot.its.jpo.ode.plugins.oss.j2735;

import us.dot.its.jpo.ode.j2735.dsrc.DDateTime;
import us.dot.its.jpo.ode.plugin.j2735.J2735DDateTime;

public class OssDDateTime {

	public static J2735DDateTime genericDDateTime(DDateTime dateTime) {
		J2735DDateTime dt = new J2735DDateTime();
		
		if (dateTime.year.intValue() != 0) {
			dt.year = dateTime.year.intValue();
		}
		
		if (dateTime.month.intValue() != 0) {
			dt.month = dateTime.month.intValue();
		}
		
		if (dateTime.day.intValue() != 0) {
			dt.day = dateTime.day.intValue();
		}
		
		if (dateTime.hour.intValue() != 31) {
			dt.hour = dateTime.hour.intValue();
		}
		
		if (dateTime.minute.intValue() != 60) {
			dt.minute = dateTime.minute.intValue();
		}
		
		if (dateTime.second.intValue() != 65535) {
			dt.second = dateTime.second.intValue();
		}
		
		dt.offset = dateTime.offset.intValue();
		
		return dt ;
	}

}
