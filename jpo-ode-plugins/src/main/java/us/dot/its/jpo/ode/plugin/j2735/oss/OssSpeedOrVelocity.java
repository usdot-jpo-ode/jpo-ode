package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.Speed;
import us.dot.its.jpo.ode.j2735.dsrc.Velocity;

public class OssSpeedOrVelocity {

    private OssSpeedOrVelocity() {}

    public static BigDecimal genericSpeed(Speed speed) {
        return genericSpeedOrVelocity(speed.intValue());
    }

    public static BigDecimal genericVelocity(Velocity velocity) {
        return genericSpeedOrVelocity(velocity.intValue());
    }

    private static BigDecimal genericSpeedOrVelocity(int speedOrVelocity) {

        if (speedOrVelocity < 0 || speedOrVelocity > 8191) {
            throw new IllegalArgumentException("Speed or velocity out of bounds");
        }

        BigDecimal result = null;

        if (speedOrVelocity != 8191) {
            result = BigDecimal.valueOf(speedOrVelocity * (long) 2, 2);
        }

        return result;

    }
}
