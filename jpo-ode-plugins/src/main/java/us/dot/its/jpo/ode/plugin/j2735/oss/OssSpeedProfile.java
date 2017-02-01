package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.util.Iterator;

import us.dot.its.jpo.ode.j2735.dsrc.SpeedProfile;
import us.dot.its.jpo.ode.j2735.dsrc.SpeedProfileMeasurement;
import us.dot.its.jpo.ode.plugin.j2735.J2735SpeedProfile;

public class OssSpeedProfile {

    private OssSpeedProfile() {}

    public static J2735SpeedProfile genericSpeedProfile(SpeedProfile speedProfile) {
        J2735SpeedProfile sp = new J2735SpeedProfile();

        Iterator<SpeedProfileMeasurement> iter = speedProfile.speedReports.elements.iterator();

        while (iter.hasNext()) {
            sp.getSpeedReports().add(iter.next().intValue());
        }

        return sp;
    }

}
