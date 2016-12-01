package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;
import java.util.List;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735RTCMPackage implements Asn1Object {

	public List<String> msgs = new ArrayList<String>();
	public J2735RTCMheader rtcmHeader;

}
