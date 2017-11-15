package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

@JsonPropertyOrder({ "center", "radius", "units" })
public class Circle extends Asn1Object {
   private static final long serialVersionUID = 1L;

   private Position center;

   @JsonProperty("radius")
   private String radius;

   @JsonProperty("units")
   private String units;

   @JsonProperty("position")
   public Position getPosition() {
      return center;
   }

   @JsonProperty("center")
   public void setPosition(Position position) {
      this.center = position;
   }

   public String getRadius() {
      return radius;
   }

   public void setRadius(String radius) {
      this.radius = radius;
   }

   public String getUnits() {
      return units;
   }

   public void setUnits(String units) {
      this.units = units;
   }
}
