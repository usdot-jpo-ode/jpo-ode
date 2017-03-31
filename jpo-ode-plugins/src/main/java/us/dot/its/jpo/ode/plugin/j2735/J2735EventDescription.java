package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;
import java.util.List;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735EventDescription extends Asn1Object {
   private static final long serialVersionUID = 1L;

   private List<Integer> description;
   private J2735Extent extent;
   private J2735HeadingSlice heading;
   private String priority;
   private List<J2735RegionalContent> regional = new ArrayList<>();
   private Integer typeEvent;

   public List<Integer> getDescription() {
      return description;
   }

   public void setDescription(List<Integer> description) {
      this.description = description;
   }

   public J2735Extent getExtent() {
      return extent;
   }

   public void setExtent(J2735Extent extent) {
      this.extent = extent;
   }

   public J2735HeadingSlice getHeading() {
      return heading;
   }

   public void setHeading(J2735HeadingSlice heading) {
      this.heading = heading;
   }

   public String getPriority() {
      return priority;
   }

   public void setPriority(String priority) {
      this.priority = priority;
   }

   public List<J2735RegionalContent> getRegional() {
      return regional;
   }

   public void setRegional(List<J2735RegionalContent> regional) {
      this.regional = regional;
   }

   public Integer getTypeEvent() {
      return typeEvent;
   }

   public void setTypeEvent(Integer typeEvent) {
      this.typeEvent = typeEvent;
   }

}
