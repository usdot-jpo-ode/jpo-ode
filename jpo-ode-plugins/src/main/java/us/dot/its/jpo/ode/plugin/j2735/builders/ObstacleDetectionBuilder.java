package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735ObstacleDetection;
import us.dot.its.jpo.ode.plugin.j2735.J2735VertEvent;

public class ObstacleDetectionBuilder {

    private static final Integer DIST_LOWER_BOUND = 0;
    private static final Integer DIST_UPPER_BOUND = 32767;
    
    private ObstacleDetectionBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735ObstacleDetection genericObstacleDetection(JsonNode obstacleDetection) {

        // Bounds check
        if (obstacleDetection.obDist.asInt() < DIST_LOWER_BOUND || obstacleDetection.obDist.asInt() > DIST_UPPER_BOUND) {
            throw new IllegalArgumentException("Distance out of bounds [0..32767]");
        }

        // Required elements
        J2735ObstacleDetection ob = new J2735ObstacleDetection();
        ob.setObDist(obstacleDetection.obDist.asInt());
        ob.setObDirect(AngleBuilder.genericAngle(obstacleDetection.obDirect));
        ob.setDateTime(DDateTimeBuilder.genericDDateTime(obstacleDetection.dateTime));

        // Optional elements
        if (obstacleDetection.description != null) {
            ob.setDescription(obstacleDetection.description.asInt());
        }
        if (obstacleDetection.locationDetails != null) {
            ob.setLocationDetails(NamedNumberBuilder.genericGenericLocations(obstacleDetection.locationDetails));
        }

        J2735VertEvent event = new J2735VertEvent();

        for (int i = 0; i < obstacleDetection.getVertEvent().getSize(); i++) {
            String eventName = obstacleDetection.getVertEvent().getNamedBits().getMemberName(i);
            Boolean eventStatus = obstacleDetection.getVertEvent().getBit(obstacleDetection.getVertEvent().getSize() - i - 1);

            if (eventName != null) {
                event.put(eventName, eventStatus);
            }
        }

        ob.setVertEvent(event);

        return ob;
    }

}
