package us.dot.its.jpo.ode.plugins.oss.j2735;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.Speed;
import us.dot.its.jpo.ode.j2735.dsrc.Velocity;

public class OssSpeedOrVelocity {

	public static BigDecimal genericSpeed(Speed speed) {
		return genericSpeedOrVelocity(speed.intValue());
	}

	public static BigDecimal genericVelocity(Velocity velocity) {
		return genericSpeedOrVelocity(velocity.intValue());
	}

	private static BigDecimal genericSpeedOrVelocity(int speedOrVelocity) {
		if (speedOrVelocity != 8191)
			return BigDecimal.valueOf(speedOrVelocity * 2, 2);
		else
			return null;
	}
}
