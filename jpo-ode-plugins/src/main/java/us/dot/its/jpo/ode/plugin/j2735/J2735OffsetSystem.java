package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735OffsetSystem extends Asn1Object {
    private static final long serialVersionUID = 1L;

    private int scale;
    private J2735Offset offset;

    public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

    public J2735Offset getOffset() {
		return offset;
	}

	public void setOffset(J2735Offset offset) {
		this.offset = offset;
	}
}
