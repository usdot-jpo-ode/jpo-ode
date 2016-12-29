package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import us.dot.its.jpo.ode.j2735.dsrc.PathHistoryPoint;
import us.dot.its.jpo.ode.j2735.dsrc.PathHistoryPointList;
import us.dot.its.jpo.ode.plugin.j2735.J2735PathHistoryPoint;

public class OssPathHistoryPointList {

	public static List<J2735PathHistoryPoint> genericPathHistoryPointList(PathHistoryPointList crumbData) {
		List<J2735PathHistoryPoint> phpl = new ArrayList<J2735PathHistoryPoint>();
		
		Iterator<PathHistoryPoint> iter = crumbData.elements.iterator();
		
		while(iter.hasNext()) {
			phpl.add(OssPathHistoryPoint.genericPathHistoryPoint(iter.next()));
		}
		
		return phpl ;
	}

//	private static J2735PathHistoryPoint genericPathHistoryPoint(PathHistoryPoint next) {
//		
//		J2735PathHistoryPoint php = new J2735PathHistoryPoint();
//		
//		if (next.elevationOffset.longValue() != 2048)
//			php.elevationOffset = BigDecimal.valueOf(next.elevationOffset.longValue(), 1);
//		
//		php.heading = BigDecimal.valueOf(next.heading.longValue() * 15, 1);
//		
//		php.latOffset = BigDecimal.valueOf(next.latOffset.longValue(), 7);
//		php.lonOffset = BigDecimal.valueOf(next.lonOffset.longValue(), 7);
//		php.posAccuracy = OssPositionalAccuracy.genericPositionalAccuracy(next.posAccuracy);
//		if (next.speed.intValue() != 8191) {
//			// speed is received in units of 0.02 m/s
//			php.speed = BigDecimal.valueOf(next.speed.intValue() * 2, 2);
//		}
//
//		if (next.timeOffset.intValue() != 65535)
//			php.timeOffset = BigDecimal.valueOf(next.timeOffset.intValue(), 2);
//		
//		return php ;
//	}

}
