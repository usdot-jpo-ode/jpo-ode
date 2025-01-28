package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735NodeXY extends Asn1Object {
    private static final long serialVersionUID = 1L;

    private J2735NodeOffsetPointXY delta;
    private J2735NodeAttributeSet attributes;

    public J2735NodeOffsetPointXY getDelta() {
        return delta;
    }

    public void setDelta(J2735NodeOffsetPointXY delta) {
        this.delta = delta;
    }

    public J2735NodeAttributeSet getAttributes() {
        return attributes;
    }

    public void setAttributes(J2735NodeAttributeSet attributes) {
        this.attributes = attributes;
    }
}
