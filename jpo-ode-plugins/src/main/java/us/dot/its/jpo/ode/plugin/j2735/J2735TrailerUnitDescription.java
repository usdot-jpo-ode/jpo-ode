package us.dot.its.jpo.ode.plugin.j2735;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735TrailerUnitDescription extends Asn1Object {
	private static final long serialVersionUID = 1L;

	public J2735BumperHeights bumperHeights;
	public BigDecimal centerOfGravity;
	public List<J2735TrailerHistoryPoint> crumbData = 
			new ArrayList<J2735TrailerHistoryPoint>();
	public BigDecimal elevationOffset;
	public J2735PivotPointDescription frontPivot;
	public BigDecimal height;
	public Boolean isDolly;
	public Integer length;
	public Integer mass;
	public J2735Node_XY positionOffset;
	public J2735PivotPointDescription rearPivot;
	public BigDecimal rearWheelOffset;
	public Integer width;

}
