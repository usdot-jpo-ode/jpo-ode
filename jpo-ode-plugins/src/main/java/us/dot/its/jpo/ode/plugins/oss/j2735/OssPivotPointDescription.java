package us.dot.its.jpo.ode.plugins.oss.j2735;

import us.dot.its.jpo.ode.j2735.dsrc.PivotPointDescription;
import us.dot.its.jpo.ode.plugin.j2735.J2735PivotPointDescription;

public class OssPivotPointDescription {

	public static J2735PivotPointDescription genericPivotPointDescription(PivotPointDescription ppd) {
		J2735PivotPointDescription gppd = new J2735PivotPointDescription();
		
		gppd.pivotAngle = OssAngle.genericAngle(ppd.pivotAngle);
		gppd.pivotOffset = ppd.pivotOffset.intValue();
		gppd.pivots = ppd.pivots.booleanValue();
		
		return gppd ;
	}

}
