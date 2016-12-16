package us.dot.its.jpo.ode.plugins.oss.j2735;

import us.dot.its.jpo.ode.j2735.dsrc.ObstacleDetection;
import us.dot.its.jpo.ode.plugin.j2735.J2735ObstacleDetection;

public class OssObstacleDetection {

	public static J2735ObstacleDetection genericObstacleDetection(ObstacleDetection obstacle) {
		J2735ObstacleDetection ob = new J2735ObstacleDetection();
		
		ob.dateTime = OssDDateTime.genericDDateTime(obstacle.dateTime);
		ob.description = obstacle.description.intValue();
		ob.locationDetails = 
				OssNamedNumber.genericGenericLocations(obstacle.locationDetails);
		ob.obDirect = OssAngle.genericAngle(obstacle.obDirect);
		ob.obDist = obstacle.obDist.intValue();
		ob.vertEvent = OssBitString.genericBitString(obstacle.vertEvent);
		
		return ob ;
	}

}
