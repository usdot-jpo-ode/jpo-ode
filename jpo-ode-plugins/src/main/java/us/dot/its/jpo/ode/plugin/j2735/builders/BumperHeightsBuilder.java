package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.BumperHeights;
import us.dot.its.jpo.ode.plugin.j2735.J2735BumperHeights;

public class BumperHeightsBuilder {

    private BumperHeightsBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735BumperHeights genericBumperHeights(BumperHeights bumperHeights) {

        if (bumperHeights.front.asInt() < 0 || bumperHeights.rear.asInt() < 0) {
            throw new IllegalArgumentException("Bumper height value below lower bound");
        }

        if (bumperHeights.front.asInt() > 127 || bumperHeights.rear.asInt() > 127) {
            throw new IllegalArgumentException("Bumper height value above upper bound");
        }

        J2735BumperHeights bhs = new J2735BumperHeights();
        bhs.setFront(BigDecimal.valueOf(bumperHeights.front.asLong(), 2));
        bhs.setRear(BigDecimal.valueOf(bumperHeights.rear.asLong(), 2));
        return bhs;
    }

}
