package us.dot.its.jpo.ode.plugin.j2735;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735BsmCoreData extends Asn1Object {
	private static final long serialVersionUID = 1L;

	private Integer msgCnt;
	private String id;
	private Integer secMark;

	private J2735Position3D position;
	private J2735AccelerationSet4Way accelSet;
	private J2735PositionalAccuracy accuracy;

	private J2735TransmissionState transmission;
	private BigDecimal speed;
	private BigDecimal heading;
	private BigDecimal angle;
	private J2735BrakeSystemStatus brakes;
	private J2735VehicleSize size;

	public Integer getMsgCnt() {
		return msgCnt;
	}

	public void setMsgCnt(Integer msgCnt) {
		this.msgCnt = msgCnt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getSecMark() {
		return secMark;
	}

	public void setSecMark(Integer secMark) {
		this.secMark = secMark;
	}

	public J2735Position3D getPosition() {
		return position;
	}

	public void setPosition(J2735Position3D position) {
		this.position = position;
	}

	public J2735AccelerationSet4Way getAccelSet() {
		return accelSet;
	}

	public void setAccelSet(J2735AccelerationSet4Way accelSet) {
		this.accelSet = accelSet;
	}

	public J2735PositionalAccuracy getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(J2735PositionalAccuracy accuracy) {
		this.accuracy = accuracy;
	}

	public J2735TransmissionState getTransmission() {
		return transmission;
	}

	public void setTransmission(J2735TransmissionState transmission) {
		this.transmission = transmission;
	}

	public BigDecimal getSpeed() {
		return speed;
	}

	public void setSpeed(BigDecimal speed) {
		this.speed = speed;
	}

	public BigDecimal getHeading() {
		return heading;
	}

	public void setHeading(BigDecimal heading) {
		this.heading = heading;
	}

	public BigDecimal getAngle() {
		return angle;
	}

	public void setAngle(BigDecimal angle) {
		this.angle = angle;
	}

	public J2735BrakeSystemStatus getBrakes() {
		return brakes;
	}

	public void setBrakes(J2735BrakeSystemStatus brakes) {
		this.brakes = brakes;
	}

	public J2735VehicleSize getSize() {
		return size;
	}

	public void setSize(J2735VehicleSize size) {
		this.size = size;
	}

}
