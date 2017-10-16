package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735BrakeAppliedStatus;

public class BrakesAppliedStatusBuilder {

   public enum BrakeAppliedStatusNames {
         unavailable, leftFront, leftRear, rightFront, rightRear
   }
   
   private BrakesAppliedStatusBuilder() {
   }

   public static J2735BrakeAppliedStatus genericBrakeAppliedStatus(JsonNode wheelBrakes) {

      J2735BrakeAppliedStatus returnValue = new J2735BrakeAppliedStatus();

      // wheelbrakes is a backwards bitstring
      char[] wb = wheelBrakes.asText().trim().toCharArray();

      for (char i = 0; i < wb.length; i++) {
         String eventName = BrakeAppliedStatusNames.values()[i].name();
         Boolean eventStatus = (wb[i] == '1' ? true : false);
         returnValue.put(eventName, eventStatus);
      }

      return returnValue;
   }

}
