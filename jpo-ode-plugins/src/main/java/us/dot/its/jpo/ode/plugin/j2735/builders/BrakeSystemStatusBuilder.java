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
            .setWheelBrakes(BrakesAppliedStatusBuilder.genericBrakeAppliedStatus(brakesStatus.get("wheelBrakes")));
      genericBrakesStatus.setTraction(brakesStatus.get("traction").fieldNames().next());
      genericBrakesStatus.setAbs(brakesStatus.get("abs").fieldNames().next());
      genericBrakesStatus.setScs(brakesStatus.get("scs").fieldNames().next());
      genericBrakesStatus.setBrakeBoost(brakesStatus.get("brakeBoost").fieldNames().next());
      genericBrakesStatus.setAuxBrakes(brakesStatus.get("auxBrakes").fieldNames().next());

      return genericBrakesStatus;
   }

}
