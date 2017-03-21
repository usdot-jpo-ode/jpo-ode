package us.dot.its.jpo.ode.plugin.j2735;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735BumperHeights extends Asn1Object {
	private static final long serialVersionUID = 1L;

	private BigDecimal front;
	private BigDecimal rear;

	public BigDecimal getFront() {
		return front;
	}

	public void setFront(BigDecimal front) {
		this.front = front;
	}

	public BigDecimal getRear() {
		return rear;
	}

	public void setRear(BigDecimal rear) {
		this.rear = rear;
	}
}
