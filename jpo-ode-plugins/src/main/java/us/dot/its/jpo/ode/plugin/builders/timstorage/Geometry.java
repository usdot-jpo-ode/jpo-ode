package us.dot.its.jpo.ode.plugin.builders.timstorage;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

@JsonPropertyOrder({ "direction", "extent", "laneWidth", "circle" })
public class Geometry extends Asn1Object {
   private static final long serialVersionUID = 1L;

   @JsonProperty("direction")
   private String direction;

   @JsonProperty("extent")
   private String extent;

   @JsonProperty("laneWidth")
   private String laneWidth;

   @JsonProperty("circle")
   private Circle circle;

   public String getExtent() {
      return extent;
   }

   public void setExtent(String extent) {
      this.extent = extent;
   }

   public String getDirection() {
      return direction;
   }

   public void setDirection(String direction) {
      this.direction = direction;
   }

   public Circle getCircle() {
      return circle;
   }

   public void setCircle(Circle circle) {
      this.circle = circle;
   }

   public String getLaneWidth() {
      return laneWidth;
   }

   public void setLaneWidth(String laneWidth) {
      this.laneWidth = laneWidth;
   }
}
