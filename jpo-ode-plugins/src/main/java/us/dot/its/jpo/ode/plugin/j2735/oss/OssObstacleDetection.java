package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.util.Map;

import us.dot.its.jpo.ode.j2735.dsrc.ObstacleDetection;
import us.dot.its.jpo.ode.plugin.j2735.J2735ObstacleDetection;
import us.dot.its.jpo.ode.plugin.j2735.J2735VertEvent;

public class OssObstacleDetection {

	public static J2735ObstacleDetection genericObstacleDetection(ObstacleDetection obstacle) {
		J2735ObstacleDetection ob = new J2735ObstacleDetection();
		
		// Required elements
		ob.obDist = obstacle.obDist.intValue();
      ob.obDirect = OssAngle.genericAngle(obstacle.obDirect);
      ob.dateTime = OssDDateTime.genericDDateTime(obstacle.dateTime);
      
      // Optional elements
		J2735VertEvent event = new J2735VertEvent();
		
		for (int i = 0; i < obstacle.getVertEvent().getSize(); i++) {
		    
		    String eventName = obstacle.getVertEvent().getNamedBits().getMemberName(i);
          Boolean eventStatus = obstacle.getVertEvent().getBit(obstacle.getVertEvent().getSize() - i - 1);

          if (eventName != null) {
              event.put(eventName, eventStatus);
          }
		}
		
		ob.vertEvent = event;
		
		if (obstacle.description != null) {
		    ob.description = obstacle.description.intValue();
		}
		
		if (obstacle.locationDetails != null) {
   		ob.locationDetails = OssNamedNumber.genericGenericLocations(obstacle.locationDetails);
		}
		
		return ob ;
	}

}
