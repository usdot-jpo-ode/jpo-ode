package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735RequestorPositionVector extends Asn1Object {

    private static final long serialVersionUID = 1L;
    private J2735Position3D position;
    private Integer heading;
    private J2735TransmissionAndSpeed speed;

    public J2735Position3D getPosition() {
        return position;
    }

    public void setPosition(J2735Position3D position) {
        this.position = position;
    }

    public Integer getHeading() {
        return heading;
    }

    public void setHeading(Integer heading) {
        this.heading = heading;
    }

    public J2735TransmissionAndSpeed getSpeed() {
        return speed;
    }

    public void setSpeed(J2735TransmissionAndSpeed speed) {
        this.speed = speed;
    }
}