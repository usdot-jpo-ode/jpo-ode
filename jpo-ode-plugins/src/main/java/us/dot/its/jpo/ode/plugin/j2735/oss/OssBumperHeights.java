package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.BumperHeights;
import us.dot.its.jpo.ode.plugin.j2735.J2735BumperHeights;

public class OssBumperHeights {

    private OssBumperHeights() {}

    public static J2735BumperHeights genericBumperHeights(BumperHeights bumperHeights) {

        if (bumperHeights.front.intValue() < 0 || bumperHeights.rear.intValue() < 0) {
            throw new IllegalArgumentException("Bumper height value below lower bound");
        }

        if (bumperHeights.front.intValue() > 127 || bumperHeights.rear.intValue() > 127) {
            throw new IllegalArgumentException("Bumper height value above upper bound");
        }

        J2735BumperHeights bhs = new J2735BumperHeights();
        bhs.setFront(BigDecimal.valueOf(bumperHeights.front.longValue(), 2));
        bhs.setRear(BigDecimal.valueOf(bumperHeights.rear.longValue(), 2));
        return bhs;
    }

}
