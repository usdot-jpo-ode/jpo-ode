package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;
import java.util.List;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735SegmentAttributeXYList extends Asn1Object {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<J2735SegmentAttributeXY> segAttrList = new ArrayList<>();

	public List<J2735SegmentAttributeXY> getSegAttrList() {
		return segAttrList;
	}

	public void setSegAttrList(List<J2735SegmentAttributeXY> segAttrList) {
		this.segAttrList = segAttrList;
	}

}
