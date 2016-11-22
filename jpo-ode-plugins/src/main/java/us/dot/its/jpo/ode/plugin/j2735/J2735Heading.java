package us.dot.its.jpo.ode.plugin.j2735;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735Heading implements Asn1Object {
	private BigDecimal heading;
	
	J2735Heading(int heading) {
		BigDecimal.valueOf(heading * 125, 4);
	}

	public BigDecimal getHeading() {
		return heading;
	}

	public void setHeading(BigDecimal heading) {
		this.heading = heading;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((heading == null) ? 0 : heading.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		J2735Heading other = (J2735Heading) obj;
		if (heading == null) {
			if (other.heading != null)
				return false;
		} else if (!heading.equals(other.heading))
			return false;
		return true;
	}

	
}
