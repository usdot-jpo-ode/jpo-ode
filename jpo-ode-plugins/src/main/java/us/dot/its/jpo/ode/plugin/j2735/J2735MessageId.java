package us.dot.its.jpo.ode.plugin.j2735;

public class J2735MessageId extends J2735Choice {
      private static final long serialVersionUID = 5091542631221985473L;

      public String furtherInfoID_chosen;
      public J2735RoadSignId roadSignID_chosen;
      
      public String getFurtherInfoID_chosen() {
         return furtherInfoID_chosen;
      }
      public J2735MessageId setFurtherInfoID_chosen(String furtherInfoID_chosen) {
         this.furtherInfoID_chosen = furtherInfoID_chosen;
         return this;
      }
      public J2735RoadSignId getRoadSignID_chosen() {
         return roadSignID_chosen;
      }
      public J2735MessageId setRoadSignID_chosen(J2735RoadSignId roadSignID_chosen) {
         this.roadSignID_chosen = roadSignID_chosen;
         return this;
      }

}
