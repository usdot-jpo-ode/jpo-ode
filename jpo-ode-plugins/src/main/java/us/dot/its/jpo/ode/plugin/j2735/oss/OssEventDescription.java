package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import us.dot.its.jpo.ode.j2735.dsrc.EventDescription;
import us.dot.its.jpo.ode.j2735.dsrc.EventDescription.Description;
import us.dot.its.jpo.ode.j2735.itis.ITIScodes;
import us.dot.its.jpo.ode.plugin.j2735.J2735EventDescription;
import us.dot.its.jpo.ode.plugin.j2735.J2735Extent;
import us.dot.its.jpo.ode.plugin.j2735.J2735HeadingSlice;
import us.dot.its.jpo.ode.plugin.j2735.J2735RegionalContent;
import us.dot.its.jpo.ode.util.CodecUtils;

public class OssEventDescription {

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
            //desc.heading = OssBitString.genericBitString(description.heading);
            //desc.heading = new J2735HeadingSlice(description.heading.byteArrayValue());
            
            J2735HeadingSlice headingSlice = new J2735HeadingSlice();
            
            for (int i = 0; i < description.heading.getSize(); i++) {

                String headingBitName = description.heading.getNamedBits().getMemberName(i);
                Boolean headingBitStatus = description.heading.getBit(description.heading.getSize() - i - 1);

                if (headingBitName != null) {
                    headingSlice.put(headingBitName, headingBitStatus);
                }
            }
            
            desc.heading = headingSlice;
            
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
                        .setValue(element.regExtValue.getEncodedValue()));
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
