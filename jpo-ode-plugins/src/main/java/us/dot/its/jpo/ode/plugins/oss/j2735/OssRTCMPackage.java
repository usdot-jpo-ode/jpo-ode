package us.dot.its.jpo.ode.plugins.oss.j2735;

import java.util.Iterator;

import us.dot.its.jpo.ode.j2735.dsrc.RTCMPackage;
import us.dot.its.jpo.ode.j2735.dsrc.RTCMmessage;
import us.dot.its.jpo.ode.plugin.j2735.J2735RTCMPackage;
import us.dot.its.jpo.ode.util.CodecUtils;

public class OssRTCMPackage {

	public static J2735RTCMPackage genericRTCMPackage(RTCMPackage theRTCM) {
		J2735RTCMPackage rtcm = new J2735RTCMPackage();
		
		Iterator<RTCMmessage> iter = theRTCM.msgs.elements.iterator();
		
		while (iter.hasNext()) {
			rtcm.msgs.add(CodecUtils.toHex(iter.next().byteArrayValue()));
		}
		rtcm.rtcmHeader = OssRTCMheader.genericRTCMheader(theRTCM.rtcmHeader);
		
		return rtcm ;
	}

}
