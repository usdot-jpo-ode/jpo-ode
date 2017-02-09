package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.BrakeSystemStatus;
import us.dot.its.jpo.ode.plugin.j2735.J2735BrakeAppliedStatus;
import us.dot.its.jpo.ode.plugin.j2735.J2735BrakeSystemStatus;

public class OssBrakeSystemStatus {

    private OssBrakeSystemStatus() {}

    public static J2735BrakeSystemStatus genericBrakeSystemStatus(BrakeSystemStatus brakesStatus) {
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
