package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735MsgId extends Asn1Object {
    private static final long serialVersionUID = 1L;

    private String furtherInfoID;
    private J2735RoadSignId roadSignID;

    public String getFurtherInfoId() {
		return furtherInfoID;
	}

	public void setFurtherInfoId(String furtherInfoID) {
		this.furtherInfoID = furtherInfoID;
	}

    public J2735RoadSignId getRoadSignID() {
		return roadSignID;
	}

	public void setRoadSignID(J2735RoadSignId roadSignID) {
		this.roadSignID = roadSignID;
	}
}
