package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735BrakeSystemStatus;

public class BrakeSystemStatusBuilder {

   private BrakeSystemStatusBuilder() {
      throw new UnsupportedOperationException();
   }

   public static J2735BrakeSystemStatus genericBrakeSystemStatus(JsonNode brakesStatus) {
      J2735BrakeSystemStatus genericBrakesStatus = new J2735BrakeSystemStatus();

      genericBrakesStatus
            .setWheelBrakes(WheelBrakesBuilder.genericWheelBrakes(brakesStatus.get("wheelBrakes")));
      genericBrakesStatus.setTraction(brakesStatus.get("traction").asText());
      genericBrakesStatus.setAbs(brakesStatus.get("abs").asText());
      genericBrakesStatus.setScs(brakesStatus.get("scs").asText());
      genericBrakesStatus.setBrakeBoost(brakesStatus.get("brakeBoost").asText());
      genericBrakesStatus.setAuxBrakes(brakesStatus.get("auxBrakes").asText());

      return genericBrakesStatus;
   }

}
