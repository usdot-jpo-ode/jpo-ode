package us.dot.its.jpo.ode.plugin.j2735.oss;

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
    
    private OssEventDescription() {}

    public static J2735EventDescription genericEventDescription(EventDescription description) {

        J2735EventDescription desc = new J2735EventDescription();

        // Required element
        desc.typeEvent = description.typeEvent.intValue();

        // Optional elements
        if (description.hasDescription()) {
            desc.description = buildDescription(description.description);
        }
        if (description.hasPriority()) {
            desc.priority = CodecUtils.toHex(description.priority.byteArrayValue());
        }
        if (description.hasHeading()) {
            OssHeadingSlice.genericHeadingSlice(description.heading);
        }
        if (description.hasExtent()) {
            desc.extent = J2735Extent.values()[description.extent.indexOf()];
        }
        if (description.hasRegional()) {
            while (description.regional.elements().hasMoreElements()) {
                us.dot.its.jpo.ode.j2735.dsrc.EventDescription.Regional.Sequence_ element = 
                        (us.dot.its.jpo.ode.j2735.dsrc.EventDescription.Regional.Sequence_) description.regional
                        .elements().nextElement();
                desc.regional.add(new J2735RegionalContent().setId(element.regionId.intValue())
                        .setHexValue(CodecUtils.toHex(element.regExtValue.getEncodedValue())));
            }
        }

        return desc;
    }

    private static List<Integer> buildDescription(Description description) {
        List<Integer> desc = new ArrayList<Integer>();

        Iterator<ITIScodes> iter = description.elements.iterator();

        while (iter.hasNext()) {
            desc.add(iter.next().intValue());
        }

        return desc;
    }

}
