package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;
import java.math.RoundingMode;

import us.dot.its.jpo.ode.j2735.dsrc.PositionalAccuracy;
import us.dot.its.jpo.ode.plugin.j2735.J2735PositionalAccuracy;

public class OssPositionalAccuracy {
    
    private OssPositionalAccuracy() {
       throw new UnsupportedOperationException();
    }
    
    public static J2735PositionalAccuracy genericPositionalAccuracy(PositionalAccuracy positionalAccuracy) {

        if (positionalAccuracy.semiMajor.intValue() < 0 || positionalAccuracy.semiMajor.intValue() > 255) {
            throw new IllegalArgumentException("SemiMajorAccuracy value out of bounds");
        }

        if (positionalAccuracy.semiMinor.intValue() < 0 || positionalAccuracy.semiMinor.intValue() > 255) {
            throw new IllegalArgumentException("SemiMinorAccuracy value out of bounds");
        }

        if (positionalAccuracy.orientation.intValue() < 0 || positionalAccuracy.orientation.intValue() > 65535) {
            throw new IllegalArgumentException("SemiMajorOrientation value out of bounds");
        }

        J2735PositionalAccuracy genericPositionalAccuracy = new J2735PositionalAccuracy();

        if (positionalAccuracy.semiMajor.intValue() != 255) {
            genericPositionalAccuracy.setSemiMajor(BigDecimal.valueOf(positionalAccuracy.semiMajor.intValue() * (long)5, 2));
        }

        if (positionalAccuracy.semiMinor.intValue() != 255) {
            genericPositionalAccuracy.setSemiMinor(BigDecimal.valueOf(positionalAccuracy.semiMinor.intValue() * (long)5, 2));
        }

        if (positionalAccuracy.orientation.intValue() != 65535) {

            genericPositionalAccuracy.setOrientation(BigDecimal
                    .valueOf((0.0054932479) * (double) (positionalAccuracy.orientation.longValue()))
                    .setScale(10, RoundingMode.HALF_EVEN));

        }
        return genericPositionalAccuracy;

    }

}
