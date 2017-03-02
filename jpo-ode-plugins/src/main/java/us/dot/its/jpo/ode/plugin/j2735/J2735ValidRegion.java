package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735ValidRegion extends Asn1Object {
   private static final long serialVersionUID = 5029639469363555078L;
   
   private J2735HeadingSlice direction;
   private J2735Extent extent;
   private J2735Area area;
   
   public J2735HeadingSlice getDirection() {
      return direction;
   }
   public void setDirection(J2735HeadingSlice direction) {
      this.direction = direction;
   }
   public J2735Extent getExtent() {
      return extent;
   }
   public void setExtent(J2735Extent extent) {
      this.extent = extent;
   }
   public J2735Area getArea() {
      return area;
   }
   public void setArea(J2735Area area) {
      this.area = area;
   }
   
   //TODO Move to us.dot.its.jpo.ode.plugin.j2735.oss.OssRegions class
//   public static ArrayList<OdeValidRegion> createList(Regions regions) {
//      if (regions == null)
//         return null;
//      
//      ArrayList<ValidRegion> elements = regions.elements;
//      ArrayList<OdeValidRegion> vrs = new ArrayList<OdeValidRegion>();
//      for (ValidRegion vr : elements) {
//         if (vr != null)
//            vrs.add(new OdeValidRegion(vr));
//      }
//      return vrs;
//   }

}
