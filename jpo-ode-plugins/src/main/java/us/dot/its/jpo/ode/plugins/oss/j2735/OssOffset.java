package us.dot.its.jpo.ode.plugins.oss.j2735;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.Offset_B12;
import us.dot.its.jpo.ode.j2735.dsrc.VertOffset_B07;

public class OssOffset {

	public static BigDecimal genericOffset(VertOffset_B07 offset) {
		if (offset.intValue() != -64)
			return BigDecimal.valueOf(offset.longValue(), 1);
		else
			return null;
	}

	public static BigDecimal genericOffset(Offset_B12 offset) {
		return BigDecimal.valueOf(offset.longValue(), 2);
	}

}
