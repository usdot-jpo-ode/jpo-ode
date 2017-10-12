package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735EventDescription;
import us.dot.its.jpo.ode.plugin.j2735.J2735Extent;
import us.dot.its.jpo.ode.plugin.j2735.J2735HeadingSlice;
import us.dot.its.jpo.ode.plugin.j2735.J2735RegionalContent;
import us.dot.its.jpo.ode.util.CodecUtils;

public class EventDescriptionBuilder {

   public enum J2735HeadingSliceNames {
      FROM000_0TO022_5DEGREES, FROM022_5TO045_0DEGREES, FROM045_0TO067_5DEGREES, FROM067_5TO090_0DEGREES, FROM090_0TO112_5DEGREES, FROM112_5TO135_0DEGREES, FROM135_0TO157_5DEGREES, FROM157_5TO180_0DEGREES, FROM180_0TO202_5DEGREES, FROM202_5TO225_0DEGREES, FROM225_0TO247_5DEGREES, FROM247_5TO270_0DEGREES, FROM270_0TO292_5DEGREES, FROM292_5TO315_0DEGREES, FROM315_0TO337_5DEGREES, FROM337_5TO360_0DEGREES
   }

   private EventDescriptionBuilder() {
      throw new UnsupportedOperationException();
   }

   public static J2735EventDescription genericEventDescription(JsonNode description) {

      J2735EventDescription desc = new J2735EventDescription();

      // Required element
      desc.setTypeEvent(description.get("typeEvent").asInt());

      // Optional elements
      if (description.get("description") != null) {
         desc.setDescription(buildDescription(description.get("description")));
      }
      if (description.get("priority") != null) {
         desc.setPriority(description.get("priority").asText());
      }
      if (description.get("heading") != null) {

         J2735HeadingSlice headingSlice = new J2735HeadingSlice();

         char[] headingSliceBits = description.get("heading").asText().toCharArray();

         for (int i = 0; i < headingSliceBits.length; i++) {

            String eventName = J2735HeadingSliceNames.values()[i].name();
            Boolean eventStatus = (headingSliceBits[i] == '1' ? true : false);
            headingSlice.put(eventName, eventStatus);

         }

         desc.setHeading(headingSlice);

      }
      if (description.get("extent") != null) {
         desc.setExtent(J2735Extent.valueOf(description.get("extent").asText().replaceAll("-", "_").toUpperCase()));
      }

      JsonNode regional = description.get("regional");
      if (regional != null) {

         if (regional.isArray()) {
            Iterator<JsonNode> elements = regional.elements();

            while (elements.hasNext()) {
               JsonNode element = elements.next();

               desc.getRegional().add(new J2735RegionalContent().setId(element.get("regionId").asInt())
                     .setValue(CodecUtils.fromHex(element.get("regExtValue").asText())));
            }
         }
      }

      return desc;
   }

   private static List<Integer> buildDescription(JsonNode description) {

      List<Integer> desc = new ArrayList<>();

      if (description.isArray()) {

         Iterator<JsonNode> iter = description.elements();

         while (iter.hasNext()) {
            desc.add(iter.next().asInt());
         }
      }

      return desc;
   }

}
