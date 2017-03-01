package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735ValidRegion extends Asn1Object {
   private static final long serialVersionUID = 5029639469363555078L;
   
   public enum OdeExtent {
      useInstantlyOnly, 
      useFor3meters, 
      useFor10meters, 
      useFor50meters, 
      useFor100meters, 
      useFor500meters, 
      useFor1000meters, 
      useFor5000meters, 
      useFor10000meters, 
      useFor50000meters, 
      useFor100000meters, 
      forever
   }

   private J2735HeadingSlice direction;
   private OdeExtent extent;
   private J2735Area area;
   
   //TODO Move to us.dot.its.jpo.ode.plugin.j2735.oss.OssValidRegion class
//   public OdeValidRegion(ValidRegion element) {
//      if (element.area != null)
//         setArea(new OdeArea(element.area));
//      if (element.direction != null)
//         setDirections(J2735HeadingSlice.SliceMask.getHeadingSlices(element.direction));
//      if (element.hasExtent())
//         setExtent(OdeExtent.valueOf(element.getExtent().name()));
//   }
   public J2735HeadingSlice getDirection() {
      return direction;
   }
   public void setDirections(J2735HeadingSlice direction) {
      this.direction = direction;
   }
   public OdeExtent getExtent() {
      return extent;
   }
   public void setExtent(OdeExtent extent) {
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
