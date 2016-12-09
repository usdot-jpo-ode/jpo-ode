package us.dot.its.jpo.ode.plugin.j2735;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735BsmCoreData extends Asn1Object {
	private static final long serialVersionUID = 1L;

    public Integer msgCnt;
    public String id;
    public Integer secMark;

    public J2735Position3D position;
	public J2735AccelerationSet4Way accelSet;
	public J2735PositionalAccuracy accuracy;

    public J2735TransmissionState transmission;
    public BigDecimal speed;
    public BigDecimal heading;
    public BigDecimal angle;
    public J2735BrakeSystemStatus brakes;
    public J2735VehicleSize size;

}
