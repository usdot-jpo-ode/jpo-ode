package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735PathHistoryPoint;

public class PathHistoryPointBuilder {

   private PathHistoryPointBuilder() {
      throw new UnsupportedOperationException();
   }

   public static J2735PathHistoryPoint genericPathHistoryPoint(JsonNode pathHistoryPoint) {

      J2735PathHistoryPoint php = new J2735PathHistoryPoint();

      long latOffset = pathHistoryPoint.get("latOffset").asLong();
      long lonOffset = pathHistoryPoint.get("lonOffset").asLong();
      long elevationOffset = pathHistoryPoint.get("elevationOffset").asLong();

      // Required elements
      if (latOffset == -131072) {
         php.setLatOffset(null);
      } else if (latOffset < -131072) {
         php.setLatOffset(BigDecimal.valueOf(-0.0131071));
      } else if (latOffset > 131071) {
         php.setLatOffset(BigDecimal.valueOf(0.0131071));
      } else {
         php.setLatOffset(BigDecimal.valueOf(latOffset, 7));
      }

      if (lonOffset == -131072) {
         php.setLonOffset(null);
      } else if (lonOffset < -131072) {
         php.setLonOffset(BigDecimal.valueOf(-0.0131071));
      } else if (lonOffset > 131071) {
         php.setLonOffset(BigDecimal.valueOf(0.0131071));
      } else {
         php.setLonOffset(BigDecimal.valueOf(lonOffset, 7));
      }

      if (elevationOffset == -2048) {
         php.setElevationOffset(null);
      } else if (elevationOffset < -2048) {
         php.setElevationOffset(BigDecimal.valueOf(-204.7));
      } else if (elevationOffset > 2047) {
         php.setElevationOffset(BigDecimal.valueOf(204.7));
      } else {
         php.setElevationOffset(BigDecimal.valueOf(elevationOffset, 1));
      }

      php.setTimeOffset(TimeOffsetBuilder.genericTimeOffset(pathHistoryPoint.get("timeOffset")));

      // Optional elements
      if (pathHistoryPoint.get("speed") != null) {
         php.setSpeed(SpeedOrVelocityBuilder.genericSpeed(pathHistoryPoint.get("speed")));
      }
      if (pathHistoryPoint.get("posAccuracy") != null) {
         php.setPosAccuracy(PositionalAccuracyBuilder.genericPositionalAccuracy(pathHistoryPoint.get("posAccuracy")));
      }
      if (pathHistoryPoint.get("heading") != null) {
         php.setHeading(HeadingBuilder.genericHeading(pathHistoryPoint.get("heading")));
      }

      return php;
   }

}
