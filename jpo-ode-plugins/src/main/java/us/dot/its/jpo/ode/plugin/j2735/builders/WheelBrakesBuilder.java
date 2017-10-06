package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735WheelBrakes;

public class WheelBrakesBuilder {
   
   private WheelBrakesBuilder() {}

   public static J2735WheelBrakes genericWheelBrakes(JsonNode wheelBrakes) {

      J2735WheelBrakes returnValue = new J2735WheelBrakes();

      // wheelbrakes is a backwards bitstring
      char[] wb = wheelBrakes.asText().toCharArray();

      returnValue.put("unavailable", (wb[0] == '1' ? true : false));
      returnValue.put("rightRear", (wb[1] == '1' ? true : false));
      returnValue.put("rightFront", (wb[2] == '1' ? true : false));
      returnValue.put("leftRear", (wb[3] == '1' ? true : false));
      returnValue.put("leftFront", (wb[4] == '1' ? true : false));

      return returnValue;
   }

}
