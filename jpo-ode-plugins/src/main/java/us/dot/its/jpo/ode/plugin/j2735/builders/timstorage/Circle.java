package us.dot.its.jpo.ode.plugin.j2735.builders.timstorage;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import us.dot.its.jpo.ode.model.OdeObject;

@JsonPropertyOrder({"center", "radius", "units"})
public class Circle extends OdeObject {
   private static final long serialVersionUID = 1L;

   @JsonProperty("position")
   private Position center;

   @JsonProperty("radius")
   private String radius;

   @JsonProperty("units")
   private String units;

   public Position getPosition() {
      return center;
   }

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
