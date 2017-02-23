package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.ObstacleDetection;
import us.dot.its.jpo.ode.plugin.j2735.J2735ObstacleDetection;
import us.dot.its.jpo.ode.plugin.j2735.J2735VertEvent;

public class OssObstacleDetection {

    private static final Integer DIST_LOWER_BOUND = 0;
    private static final Integer DIST_UPPER_BOUND = 32767;

    public static J2735ObstacleDetection genericObstacleDetection(ObstacleDetection obstacle) {

        // Bounds check
        if (obstacle.obDist.intValue() < DIST_LOWER_BOUND || obstacle.obDist.intValue() > DIST_UPPER_BOUND) {
            throw new IllegalArgumentException("Distance out of bounds [0..32767]");
        }

        // Required elements
        J2735ObstacleDetection ob = new J2735ObstacleDetection();
        ob.obDist = obstacle.obDist.intValue();
        ob.obDirect = OssAngle.genericAngle(obstacle.obDirect);
        ob.dateTime = OssDDateTime.genericDDateTime(obstacle.dateTime);

        // Optional elements
        if (obstacle.description != null) {
            ob.description = obstacle.description.intValue();
        }
        if (obstacle.locationDetails != null) {
            ob.locationDetails = OssNamedNumber.genericGenericLocations(obstacle.locationDetails);
        }

        J2735VertEvent event = new J2735VertEvent();

        for (int i = 0; i < obstacle.getVertEvent().getSize(); i++) {
            String eventName = obstacle.getVertEvent().getNamedBits().getMemberName(i);
            Boolean eventStatus = obstacle.getVertEvent().getBit(obstacle.getVertEvent().getSize() - i - 1);

            if (eventName != null) {
                event.put(eventName, eventStatus);
            }
        }

        ob.vertEvent = event;

        return ob;
    }

}
