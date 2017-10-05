package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735BrakeAppliedStatus;
import us.dot.its.jpo.ode.plugin.j2735.J2735BrakeSystemStatus;

public class BrakeSystemStatusBuilder {

    private BrakeSystemStatusBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735BrakeSystemStatus genericBrakeSystemStatus(JsonNode brakesStatus) {
        J2735BrakeSystemStatus genericBrakesStatus = new J2735BrakeSystemStatus();

        J2735BrakeAppliedStatus appliedWheelBrakes = new J2735BrakeAppliedStatus();

        for (int i = 0; i < brakesStatus.wheelBrakes.getSize(); i++) {

            String eventName = brakesStatus.wheelBrakes.getNamedBits().getMemberName(i);
            Boolean eventStatus = brakesStatus.wheelBrakes.getBit(i);

            if (eventName != null) {
                appliedWheelBrakes.put(eventName, eventStatus);
            }
        }

        genericBrakesStatus.setWheelBrakes(appliedWheelBrakes);
        genericBrakesStatus.setTraction(brakesStatus.traction.name());
        genericBrakesStatus.setAbs(brakesStatus.abs.name());
        genericBrakesStatus.setScs(brakesStatus.scs.name());
        genericBrakesStatus.setBrakeBoost(brakesStatus.brakeBoost.name());
        genericBrakesStatus.setAuxBrakes(brakesStatus.auxBrakes.name());

        return genericBrakesStatus;
    }

}
