package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.ValidRegion;
import us.dot.its.jpo.ode.plugin.j2735.J2735Extent;
import us.dot.its.jpo.ode.plugin.j2735.J2735ValidRegion;

public class OssValidRegion {

   private OssValidRegion() {
   }

   public static J2735ValidRegion genericValidRegion(ValidRegion region) {
      J2735ValidRegion gvr = new J2735ValidRegion();
      
       if (region.area != null)
          gvr.setArea(OssArea.genericArea(region.area));
       if (region.direction != null)
          gvr.setDirection(OssHeadingSlice.genericHeadingSlice(region.direction));
       if (region.hasExtent())
          gvr.setExtent(J2735Extent.valueOf(region.getExtent().name()));
      return gvr;
   }

}
