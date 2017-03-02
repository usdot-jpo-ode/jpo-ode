package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.util.ArrayList;

import us.dot.its.jpo.ode.j2735.dsrc.TravelerInformation.Regional;
import us.dot.its.jpo.ode.j2735.dsrc.TravelerInformation.Regional.Sequence_;
import us.dot.its.jpo.ode.plugin.j2735.J2735RegionalContent;
import us.dot.its.jpo.ode.util.CodecUtils;

public class OssRegionalContent {

   private OssRegionalContent() {
   }

   public static ArrayList<J2735RegionalContent> genericRegionalContent(Regional regional) {
      ArrayList<J2735RegionalContent> agrc = new ArrayList<J2735RegionalContent>();
      for (Sequence_ element : regional.elements) {
         agrc.add(OssRegionalContent.genericRegionalContent(element));
      }
      return agrc;
   }

   public static J2735RegionalContent genericRegionalContent(Sequence_ element) {
      J2735RegionalContent grc = new J2735RegionalContent();
      grc.setId(element.getRegionId().intValue());
      grc.setHexValue(CodecUtils.toHex(element.getRegExtValue().getEncodedValue()));
      return grc;
   }

}
