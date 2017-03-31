package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.Angle;

public class OssAngle {
    
    private OssAngle() {
       throw new UnsupportedOperationException();
    }

    public static BigDecimal genericAngle(Angle angle) {

        if (angle.intValue() < 0 || angle.intValue() > 28800) {
            throw new IllegalArgumentException("Angle value out of bounds");
        }

        BigDecimal result = null;

        if (angle.longValue() != 28800) {
            result = longToDecimal(angle.longValue());
        }

        return result;
    }

    public static BigDecimal longToDecimal(long longValue) {
        
        BigDecimal result = null;
        
        if (longValue != 28800) {
            result = BigDecimal.valueOf(longValue * 125, 4);
        }
        
        return result;
    }

}
