package us.dot.its.jpo.ode.plugins.oss.j2735;

import java.util.Iterator;

import us.dot.its.jpo.ode.j2735.dsrc.TrailerHistoryPoint;
import us.dot.its.jpo.ode.j2735.dsrc.TrailerUnitDescription;
import us.dot.its.jpo.ode.plugin.j2735.J2735TrailerUnitDescription;

public class OssTrailerUnitDescription {

	public static J2735TrailerUnitDescription genericTrailerUnitDescription(TrailerUnitDescription tud) {
		J2735TrailerUnitDescription gtud = new J2735TrailerUnitDescription();
		
		gtud.bumperHeights = OssBumperHeights.genericBumperHeights(tud.bumperHeights);
		gtud.centerOfGravity = OssHeight.genericHeight(tud.centerOfGravity);
		
		Iterator<TrailerHistoryPoint> iter = tud.crumbData.elements.iterator();
		while(iter.hasNext()) {
			gtud.crumbData.add(OssTrailerHistoryPoint.genericTrailerHistoryPoint(iter.next()));
		}
		
		gtud.elevationOffset = OssOffset.genericOffset(tud.elevationOffset);
		gtud.frontPivot = OssPivotPointDescription.genericPivotPointDescription(tud.frontPivot);
		gtud.height = OssHeight.genericHeight(tud.height);
		gtud.isDolly = tud.isDolly.booleanValue();
		gtud.length = tud.length.intValue();
		gtud.mass = OssMassOrWeight.genericMass(tud.mass);
		gtud.positionOffset = OssNode_XY.genericNode_XY(tud.positionOffset);
		gtud.rearPivot = OssPivotPointDescription.genericPivotPointDescription(tud.rearPivot);
		gtud.rearWheelOffset = OssOffset.genericOffset(tud.rearWheelOffset);
		gtud.width = tud.width.intValue();
		
		return gtud;
	}

}
