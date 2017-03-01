package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735RoadSignId extends Asn1Object {

   private static final long serialVersionUID = 5729514080285088635L;
   
   public enum OdeMUTCDCode {
      none, regulatory, warning, maintenance, motoristService, guide, rec
   }
   
   private J2735Position3D position;
   private J2735HeadingSlice viewAngle;
   private OdeMUTCDCode mutcdCode;
   
   public J2735RoadSignId() {
      super();
   }

   public J2735Position3D getPosition() {
      return position;
   }
   public void setPosition(J2735Position3D position) {
      this.position = position;
   }
   public J2735HeadingSlice getViewAngle() {
      return viewAngle;
   }

   public void setViewAngle(J2735HeadingSlice viewAngle) {
      this.viewAngle = viewAngle;
   }

   public OdeMUTCDCode getMutcdCode() {
      return mutcdCode;
   }
   public void setMutcdCode(OdeMUTCDCode mutcdCode) {
      this.mutcdCode = mutcdCode;
   }
}
