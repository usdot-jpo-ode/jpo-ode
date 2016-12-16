package us.dot.its.jpo.ode.plugins.oss.j2735;

import us.dot.its.jpo.ode.j2735.dsrc.FullPositionVector;
import us.dot.its.jpo.ode.plugin.j2735.J2735FullPositionVector;
import us.dot.its.jpo.ode.plugin.j2735.J2735Position3D;
import us.dot.its.jpo.ode.plugin.j2735.J2735TimeConfidence;

public class OssFullPositionVector {

	public static J2735FullPositionVector genericFullPositionVector(FullPositionVector initialPosition) {
		
		J2735FullPositionVector fpv = new J2735FullPositionVector();
		
		fpv.position = new J2735Position3D(
				initialPosition.lat.longValue(),
				initialPosition._long.longValue(),
				initialPosition.elevation.longValue());
		
		fpv.heading = OssHeading.genericHeading(initialPosition.heading);
		fpv.posAccuracy = OssPositionalAccuracy.genericPositionalAccuracy(initialPosition.posAccuracy);
		fpv.posConfidence = OssPositionConfidenceSet.genericPositionConfidenceSet(initialPosition.posConfidence);
		fpv.speed = OssTransmissionAndSpeed.genericTransmissionAndSpeed(initialPosition.speed);
		fpv.speedConfidence = OssSpeedandHeadingandThrottleConfidence
				.genericSpeedandHeadingandThrottleConfidence(
						initialPosition.speedConfidence);
		fpv.timeConfidence = J2735TimeConfidence.values()[initialPosition.timeConfidence.indexOf()];
		fpv.utcTime = OssDDateTime.genericDDateTime(initialPosition.utcTime);
		
		return fpv ;
	}

}
