package us.dot.its.jpo.ode.plugin.j2735.builders.timstorage;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import us.dot.its.jpo.ode.model.OdeObject;

@JsonPropertyOrder({ "lat", "llong", "elevation" })
public class Anchor extends OdeObject {
   private static final long serialVersionUID = 1L;

   @JsonProperty("lat")
   private String lat;

   @JsonProperty("long")
   private String llong; // TODO needs to be "long"

   @JsonProperty("elevation")
   private String elevation;

   public String getElevation() {
      return elevation;
   }

   public void setElevation(String elevation) {
      this.elevation = elevation;
   }

   public String getlon() {
      return llong;
   }

   public void setlon(String lon) {
      this.llong = lon;
   }

   public String getLat() {
      return lat;
   }

   public void setLat(String lat) {
      this.lat = lat;
   }

}