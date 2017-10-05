package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735SpeedProfile;

public class SpeedProfileBuilder {

    private SpeedProfileBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735SpeedProfile genericSpeedProfile(JsonNode speedProfile) {
        J2735SpeedProfile sp = new J2735SpeedProfile();

        Iterator<JsonNode> iter = speedProfile.speedReports.elements.iterator();

        while (iter.hasNext()) {
            sp.getSpeedReports().add(iter.next().intValue());
        }

        return sp;
    }

}
