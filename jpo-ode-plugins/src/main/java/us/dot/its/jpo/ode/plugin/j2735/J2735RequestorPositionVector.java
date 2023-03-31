package us.dot.its.jpo.ode.plugin.j2735;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735RequestorPositionVector extends Asn1Object {

    private static final long serialVersionUID = 1L;
    private OdePosition3D position;
    private BigDecimal heading;
    private J2735TransmissionAndSpeed speed;

    public OdePosition3D getPosition() {
        return position;
    }

    public void setPosition(OdePosition3D position) {
        this.position = position;
    }

    public BigDecimal getHeading() {
        return heading;
    }

    public void setHeading(BigDecimal heading) {
        this.heading = heading;
    }

    public J2735TransmissionAndSpeed getSpeed() {
        return speed;
    }

    public void setSpeed(J2735TransmissionAndSpeed speed) {
        this.speed = speed;
    }
}