package us.dot.its.jpo.ode.plugins.oss.j2735;

import us.dot.its.jpo.ode.j2735.dsrc.DDateTime;
import us.dot.its.jpo.ode.plugin.j2735.J2735DDateTime;

public class OssDDateTime {

	public static J2735DDateTime genericDDateTime(DDateTime dateTime) {
		J2735DDateTime dt = new J2735DDateTime();
		
		dt.day = dateTime.day.intValue();
		dt.hour = dateTime.hour.intValue();
		dt.minute = dateTime.minute.intValue();
		dt.month = dateTime.month.intValue();
		dt.offset = dateTime.offset.intValue();
		dt.second = dateTime.second.intValue();
		dt.year = dateTime.year.intValue();
		
		return dt ;
	}

}
