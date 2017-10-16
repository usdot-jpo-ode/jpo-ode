package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735PathPrediction;

public class PathPredictionBuilder {

   private PathPredictionBuilder() {
      throw new UnsupportedOperationException();
   }

   public static J2735PathPrediction genericPathPrediction(JsonNode pathPrediction) {
      J2735PathPrediction pp = new J2735PathPrediction();

      JsonNode radiusOfCurve = pathPrediction.get("radiusOfCurve");
      if (null != radiusOfCurve && (radiusOfCurve.asLong() >= 32767 || radiusOfCurve.asLong() < -32767)) {
         pp.setRadiusOfCurve(BigDecimal.ZERO.setScale(1));
      } else {
         pp.setRadiusOfCurve(BigDecimal.valueOf(radiusOfCurve.asLong(), 1));
      }

      JsonNode confidence = pathPrediction.get("confidence");
      if (null != confidence && (confidence.asLong() < 0 || confidence.asLong() > 200)) {
         throw new IllegalArgumentException("Confidence value out of bounds [0.200]");
      } else {
         pp.setConfidence(BigDecimal.valueOf(confidence.asLong() * 5, 1));
      }

      return pp;
   }

}
