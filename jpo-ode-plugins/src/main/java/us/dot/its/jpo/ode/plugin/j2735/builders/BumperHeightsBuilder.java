package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735BumperHeights;

public class BumperHeightsBuilder {

    private BumperHeightsBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735BumperHeights genericBumperHeights(JsonNode bumperHeights) {

        if (bumperHeights.get("front").asInt() < 0 || bumperHeights.get("rear").asInt() < 0) {
            throw new IllegalArgumentException("Bumper height value below lower bound");
        }

        if (bumperHeights.get("front").asInt() > 127 || bumperHeights.get("rear").asInt() > 127) {
            throw new IllegalArgumentException("Bumper height value above upper bound");
        }

        J2735BumperHeights bhs = new J2735BumperHeights();
        bhs.setFront(BigDecimal.valueOf(bumperHeights.get("front").asLong(), 2));
        bhs.setRear(BigDecimal.valueOf(bumperHeights.get("rear").asLong(), 2));
        return bhs;
    }

}
