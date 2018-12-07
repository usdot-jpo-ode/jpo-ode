package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

@JsonPropertyOrder({ "position", "viewAngle", "mutcdCode", "crc" })
public class RoadSignID extends Asn1Object {
   private static final long serialVersionUID = 1L;

   @JsonProperty("position")
   private Position position;

   @JsonProperty("viewAngle")
   private String viewAngle;

   @JsonProperty("mutcdCode")
   private MutcdCode mutcdCode;

   @JsonProperty("crc")
   private String crc;

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
