package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735WheelBrakes;

public class BrakeAppliedStatusBuilder {

   public static J2735WheelBrakes genericBrakeAppliedStatus(JsonNode brakeAppliedStatus) {
      
      J2735WheelBrakes returnValue = new J2735WheelBrakes();
      
      returnValue.setLeftFront(brakeAppliedStatus.get("leftFront").asBoolean());
      returnValue.setLeftRear(brakeAppliedStatus.get("leftRear").asBoolean());
      returnValue.setRightFront(brakeAppliedStatus.get("rightFront").asBoolean());
      returnValue.setRightRear(brakeAppliedStatus.get("rightRear").asBoolean());
      returnValue.setUnvailable(brakeAppliedStatus.get("unavailable").asBoolean());
      
      return null;
   }

}
