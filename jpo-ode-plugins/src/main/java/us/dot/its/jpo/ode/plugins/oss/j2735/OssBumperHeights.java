package us.dot.its.jpo.ode.plugins.oss.j2735;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.BumperHeights;
import us.dot.its.jpo.ode.plugin.j2735.J2735BumperHeights;

public class OssBumperHeights {

	public static J2735BumperHeights genericBumperHeights(BumperHeights bumperHeights) {
		J2735BumperHeights bhs = new J2735BumperHeights();
		bhs.front = BigDecimal.valueOf(bumperHeights.front.longValue() * 15, 1);
		bhs.rear = BigDecimal.valueOf(bumperHeights.rear.longValue() * 15, 1);
		return bhs;
	}

}
