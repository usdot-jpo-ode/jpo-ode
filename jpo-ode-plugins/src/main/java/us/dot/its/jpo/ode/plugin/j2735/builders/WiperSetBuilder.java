package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735WiperSet;
import us.dot.its.jpo.ode.plugin.j2735.J2735WiperStatus;

public class WiperSetBuilder {

   private static final int STATUS_LOWER_BOUND = 0;
   private static final int STATUS_UPPER_BOUND = 6;
   private static final int RATE_LOWER_BOUND = 0;
   private static final int RATE_UPPER_BOUND = 127;

   private WiperSetBuilder() {
      throw new UnsupportedOperationException();
   }

   public static J2735WiperSet genericWiperSet(JsonNode wiperSet) {

      J2735WiperSet gws = new J2735WiperSet();

      // statusFront and rateFront are required elements
      int statusFront = wiperSet.get("statusFront").asInt();
      int rateFront = wiperSet.get("rateFront").asInt();

      if (statusFront < STATUS_LOWER_BOUND || STATUS_UPPER_BOUND < statusFront) {
         throw new IllegalArgumentException(
               String.format("Front wiper status out of bounds [%d..%d]", STATUS_LOWER_BOUND, STATUS_UPPER_BOUND));
      }

      gws.setStatusFront(J2735WiperStatus.values()[statusFront]);

      if (rateFront < RATE_LOWER_BOUND || RATE_UPPER_BOUND < rateFront) {
         throw new IllegalArgumentException(
               String.format("Front wiper rate out of bounds [%d..%d]", RATE_LOWER_BOUND, RATE_UPPER_BOUND));
      }

      gws.setRateFront(rateFront);

      // statusRear and rateRear are optional elements
      if (wiperSet.get("statusRear") != null) {
         int statusRear = wiperSet.get("statusRear").asInt();

         if (statusRear < STATUS_LOWER_BOUND || STATUS_UPPER_BOUND < statusRear) {
            throw new IllegalArgumentException(String.format("Rear wiper status value out of bounds [%d..%d]",
                  STATUS_LOWER_BOUND, STATUS_UPPER_BOUND));
         }

         gws.setStatusRear(J2735WiperStatus.values()[statusRear]);
      }

      if (wiperSet.get("rateRear") != null) {
         int rateRear = wiperSet.get("rateRear").asInt();

         if (rateRear < RATE_LOWER_BOUND || RATE_UPPER_BOUND < rateRear) {
            throw new IllegalArgumentException(
                  String.format("Rear wiper rate out of bounds [%d..%d]", RATE_LOWER_BOUND, RATE_UPPER_BOUND));
         }

         gws.setRateRear(rateRear);
      }

      return gws;
   }

}
