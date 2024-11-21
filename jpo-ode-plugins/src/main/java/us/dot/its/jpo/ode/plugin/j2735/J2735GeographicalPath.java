package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735GeographicalPath extends Asn1Object {
    private static final long serialVersionUID = 1L;

    private String name;
    private J2735RoadSegmentReferenceID id;
    private OdePosition3D anchor;
    private int laneWidth;
    private J2735DirectionOfUse directionality;
    private boolean closedPath;
    private J2735Description description;

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public J2735RoadSegmentReferenceID getId() {
		return id;
	}

	public void setId(J2735RoadSegmentReferenceID id) {
		this.id = id;
	}

    public OdePosition3D getAnchor() {
		return anchor;
	}

	public void setAnchor(OdePosition3D anchor) {
		this.anchor = anchor;
	}

    public int getLaneWidth() {
		return laneWidth;
	}

	public void setLaneWidth(int laneWidth) {
		this.laneWidth = laneWidth;
	}

    public J2735DirectionOfUse getDirectionality() {
		return directionality;
	}

	public void setDirectionality(J2735DirectionOfUse directionality) {
		this.directionality = directionality;
	}

    public boolean getClosedPath() {
		return closedPath;
	}

	public void setClosedPath(boolean closedPath) {
		this.closedPath = closedPath;
	}

    public J2735Description getDescription() {
		return description;
	}

	public void setDescription(J2735Description description) {
		this.description = description;
	}
}
