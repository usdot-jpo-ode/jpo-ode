package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735HumanPropelledType;
import us.dot.its.jpo.ode.plugin.j2735.J2735AnimalPropelledType;
import us.dot.its.jpo.ode.plugin.j2735.J2735MotorizedPropelledType;

import us.dot.its.jpo.ode.plugin.j2735.J2735PropelledInformation;

public class PropelledInformationBuilder {

    private PropelledInformationBuilder() {
        throw new UnsupportedOperationException();
     }

    public static J2735PropelledInformation genericPropelledInformation(JsonNode propelledInformation) {
        J2735PropelledInformation pi = new J2735PropelledInformation();

        JsonNode human = propelledInformation.get("human");
        if (human != null){
			pi.setHuman(J2735HumanPropelledType.valueOf(human.asText().toUpperCase()));
        }

        JsonNode animal = propelledInformation.get("animal");
        if (animal != null){
			pi.setAnimal(J2735AnimalPropelledType.valueOf(animal.asText().toUpperCase()));
        }

        JsonNode motor = propelledInformation.get("motor");
        if (motor != null){
			pi.setMotor(J2735MotorizedPropelledType.valueOf(motor.asText().toUpperCase()));
        }

        return pi;
    }

}
