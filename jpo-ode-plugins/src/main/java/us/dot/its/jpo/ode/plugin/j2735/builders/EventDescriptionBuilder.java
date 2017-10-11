package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.j2735.dsrc.EventDescription;
import us.dot.its.jpo.ode.j2735.dsrc.EventDescription.Description;
import us.dot.its.jpo.ode.j2735.itis.ITIScodes;
import us.dot.its.jpo.ode.plugin.j2735.J2735EventDescription;
import us.dot.its.jpo.ode.plugin.j2735.J2735Extent;
import us.dot.its.jpo.ode.plugin.j2735.J2735HeadingSlice;
import us.dot.its.jpo.ode.plugin.j2735.J2735RegionalContent;
import us.dot.its.jpo.ode.util.CodecUtils;

public class EventDescriptionBuilder {
    
    private EventDescriptionBuilder() {
       throw new UnsupportedOperationException();
    }

    public static J2735EventDescription genericEventDescription(JsonNode description) {

        J2735EventDescription desc = new J2735EventDescription();

        // Required element
        desc.setTypeEvent(description.typeEvent.asInt());

        // Optional elements
        if (description.hasDescription()) {
            desc.setDescription(buildDescription(description.description));
        }
        if (description.hasPriority()) {
            desc.setPriority(CodecUtils.toHex(description.priority.byteArrayValue()));
        }
        if (description.hasHeading()) {
            
            J2735HeadingSlice headingSlice = new J2735HeadingSlice();
            
            for (int i = 0; i < description.heading.getSize(); i++) {

                String headingBitName = description.heading.getNamedBits().getMemberName(i);
                Boolean headingBitStatus = description.heading.getBit(i);

                if (headingBitName != null) {
                    headingSlice.put(headingBitName, headingBitStatus);
                }
            }
            
            desc.setHeading(headingSlice);
            
        }
        if (description.hasExtent()) {
            desc.setExtent(J2735Extent.values()[description.extent.indexOf()]);
        }
        if (description.hasRegional()) {
            while (description.regional.elements().hasMoreElements()) {
                us.dot.its.jpo.ode.j2735.dsrc.EventDescription.Regional.Sequence_ element = 
                        (us.dot.its.jpo.ode.j2735.dsrc.EventDescription.Regional.Sequence_) description.regional
                        .elements().nextElement();
                desc.getRegional().add(new J2735RegionalContent().setId(element.regionId.asInt())
                        .setValue(element.regExtValue.getEncodedValue()));
            }
        }

        return desc;
    }

    private static List<Integer> buildDescription(Description description) {
        List<Integer> desc = new ArrayList<>();

        Iterator<ITIScodes> iter = description.elements.iterator();

        while (iter.hasNext()) {
            desc.add(iter.next().asInt());
        }

        return desc;
    }

}
