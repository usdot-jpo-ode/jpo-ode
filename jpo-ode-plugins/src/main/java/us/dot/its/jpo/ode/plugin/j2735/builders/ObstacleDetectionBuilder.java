package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735ObstacleDetection;
import us.dot.its.jpo.ode.plugin.j2735.J2735VertEvent;
import us.dot.its.jpo.ode.plugin.j2735.builders.GNSSstatusBuilder.GNSstatusNames;

public class ObstacleDetectionBuilder {

   public enum J2735VertEventNames {
      notEquipped, leftFront, leftRear, rightFront, rightRear
   }

   private static final Integer DIST_LOWER_BOUND = 0;
   private static final Integer DIST_UPPER_BOUND = 32767;

   private ObstacleDetectionBuilder() {
      throw new UnsupportedOperationException();
   }

   public static J2735ObstacleDetection genericObstacleDetection(JsonNode obstacleDetection) {

      // Bounds check
      int obDist = obstacleDetection.get("obDist").asInt();
      if (obDist < DIST_LOWER_BOUND || DIST_UPPER_BOUND < obDist) {
         throw new IllegalArgumentException(
               String.format("Distance out of bounds [%d..%d]", DIST_LOWER_BOUND, DIST_UPPER_BOUND));
      }

      // Required elements
      J2735ObstacleDetection ob = new J2735ObstacleDetection();
      ob.setObDist(obDist);
      ob.setObDirect(AngleBuilder.genericAngle(obstacleDetection.get("obDirect")));
      ob.setDateTime(DDateTimeBuilder.genericDDateTime(obstacleDetection.get("dateTime")));

      // Optional elements
      JsonNode description = obstacleDetection.get("description");

      if (description != null) {
         ob.setDescription(description.asInt());
      }

      JsonNode locationDetails = obstacleDetection.get("locationDetails");
      if (locationDetails != null) {
         ob.setLocationDetails(NamedNumberBuilder.genericGenericLocations(locationDetails));
      }

      J2735VertEvent vertEvent = new J2735VertEvent();

      char[] vertEventBits = obstacleDetection.get("vertEvent").asText().toCharArray();

      for (int i = 0; i < vertEventBits.length; i++) {
         String statusName = GNSstatusNames.values()[i].name();
         Boolean statusValue = (vertEventBits[i] == '1' ? true : false);
         vertEvent.put(statusName, statusValue);

      }

      ob.setVertEvent(vertEvent);

      return ob;
   }

}
