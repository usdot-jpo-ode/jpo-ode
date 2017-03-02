package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735RoadSignId extends Asn1Object {

   private static final long serialVersionUID = 5729514080285088635L;
   
   public enum J2735MUTCDCode {
      none, regulatory, warning, maintenance, motoristService, guide, rec
   }
   
   private J2735Position3D position;
   private J2735HeadingSlice viewAngle;
   private J2735MUTCDCode mutcdCode;
   private String crc;
   
   public J2735RoadSignId() {
      super();
   }

   public J2735Position3D getPosition() {
      return position;
   }
   public J2735RoadSignId setPosition(J2735Position3D position) {
      this.position = position;
      return this;
   }
   public J2735HeadingSlice getViewAngle() {
      return viewAngle;
   }

   public void setViewAngle(J2735HeadingSlice viewAngle) {
      this.viewAngle = viewAngle;
   }

   public J2735MUTCDCode getMutcdCode() {
      return mutcdCode;
   }
   public void setMutcdCode(J2735MUTCDCode mutcdCode) {
      this.mutcdCode = mutcdCode;
   }

   public String getCrc() {
      return crc;
   }

   public void setCrc(String crc) {
      this.crc = crc;
   }
   
   
}
