package us.dot.its.jpo.ode.plugin.j2735;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735PathHistoryPoint extends Asn1Object {
    private static final long serialVersionUID = 1L;

    private BigDecimal elevationOffset;
    private BigDecimal heading;
    private BigDecimal latOffset;
    private BigDecimal lonOffset;
    private J2735PositionalAccuracy posAccuracy;
    private BigDecimal speed;
    private BigDecimal timeOffset;

    public BigDecimal getElevationOffset() {
        return elevationOffset;
    }

    public void setElevationOffset(BigDecimal elevationOffset) {
        this.elevationOffset = elevationOffset;
    }

    public BigDecimal getHeading() {
        return heading;
    }

    public void setHeading(BigDecimal heading) {
        this.heading = heading;
    }

    public BigDecimal getLatOffset() {
        return latOffset;
    }

    public void setLatOffset(BigDecimal latOffset) {
        this.latOffset = latOffset;
    }

    public BigDecimal getLonOffset() {
        return lonOffset;
    }

    public void setLonOffset(BigDecimal lonOffset) {
        this.lonOffset = lonOffset;
    }

    public J2735PositionalAccuracy getPosAccuracy() {
        return posAccuracy;
    }

    public void setPosAccuracy(J2735PositionalAccuracy posAccuracy) {
        this.posAccuracy = posAccuracy;
    }

    public BigDecimal getSpeed() {
        return speed;
    }

    public void setSpeed(BigDecimal speed) {
        this.speed = speed;
    }

    public BigDecimal getTimeOffset() {
        return timeOffset;
    }

    public void setTimeOffset(BigDecimal timeOffset) {
        this.timeOffset = timeOffset;
    }

}
