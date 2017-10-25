package us.dot.its.jpo.ode.plugin.j2735.builders.timstorage;

import us.dot.its.jpo.ode.model.OdeObject;

public class RoadSignID extends OdeObject {
   private static final long serialVersionUID = 1L;

   private Position position;

   private String crc;

   private MutcdCode mutcdCode;

   private String viewAngle;

   public Position getPosition() {
      return position;
   }

   public void setPosition(Position position) {
      this.position = position;
   }

   public String getCrc() {
      return crc;
   }

   public void setCrc(String crc) {
      this.crc = crc;
   }

   public MutcdCode getMutcdCode() {
      return mutcdCode;
   }

   public void setMutcdCode(MutcdCode mutcdCode) {
      this.mutcdCode = mutcdCode;
   }

   public String getViewAngle() {
      return viewAngle;
   }

   public void setViewAngle(String viewAngle) {
      this.viewAngle = viewAngle;
   }
}
