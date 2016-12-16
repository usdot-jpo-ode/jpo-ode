package us.dot.its.jpo.ode.plugins.oss.j2735;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import us.dot.its.jpo.ode.j2735.dsrc.EventDescription;
import us.dot.its.jpo.ode.j2735.dsrc.EventDescription.Description;
import us.dot.its.jpo.ode.j2735.itis.ITIScodes;
import us.dot.its.jpo.ode.plugin.j2735.J2735EventDescription;
import us.dot.its.jpo.ode.plugin.j2735.J2735Extent;
import us.dot.its.jpo.ode.plugin.j2735.J2735RegionalContent;
import us.dot.its.jpo.ode.util.CodecUtils;

public class OssEventDescription {

	public static J2735EventDescription genericEventDescription(EventDescription description) {
		
		J2735EventDescription desc = new J2735EventDescription();
		
		desc.description = buildDescription(description.description);
		desc.extent = J2735Extent.values()[description.extent.indexOf()];
		desc.heading = OssBitString.genericBitString(description.heading);
		desc.priority = CodecUtils.toHex(description.priority.byteArrayValue());
		
		while (description.regional.elements().hasMoreElements()) {
			us.dot.its.jpo.ode.j2735.dsrc.EventDescription.Regional.Sequence_ element = 
					(us.dot.its.jpo.ode.j2735.dsrc.EventDescription.Regional.Sequence_) 
					description.regional.elements().nextElement();
			desc.regional.add(new J2735RegionalContent()
					.setId(element.regionId.intValue())
					.setValue(element.regExtValue.getEncodedValue())
					);
		}
		
		desc.typeEvent = description.typeEvent.intValue();

		return desc ;
	}

	private static List<Integer> buildDescription(Description description) {
		List<Integer> desc = new ArrayList<Integer>();
		
		Iterator<ITIScodes> iter = description.elements.iterator();
		
		while (iter.hasNext()) {
			desc.add(iter.next().intValue());
		}
		
		return desc ;
	}

}
