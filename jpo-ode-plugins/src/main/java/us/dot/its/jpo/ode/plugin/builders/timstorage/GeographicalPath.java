package us.dot.its.jpo.ode.plugin.builders.timstorage;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

@JsonPropertyOrder({ "name", "id", "anchor", "laneWidth", "directionality", "closedPath", "direction", "description" })
public class GeographicalPath extends Asn1Object {
   private static final long serialVersionUID = 1L;

   @JsonProperty("name")
   private String name;

   @JsonProperty("id")
   private Id id;

   @JsonProperty("anchor")
   private Anchor anchor;

   @JsonProperty("laneWidth")
   private String laneWidth;

   @JsonProperty("directionality")
   private Directionality directionality;

   @JsonProperty("closedPath")
   private String closedPath;

   @JsonProperty("direction")
   private String direction;

   @JsonProperty("description")
   private Description description;

   public Id getId() {
      return id;
   }

   public void setId(Id id) {
      this.id = id;
   }

   public String isClosedPath() {
      return closedPath;
   }

   public void setClosedPath(String closedPath) {
      this.closedPath = closedPath;
   }

   public String getDirection() {
      return direction;
   }

   public void setDirection(String direction) {
      this.direction = direction;
   }

   public Description getDescription() {
      return description;
   }

   public void setDescription(Description description) {
      this.description = description;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Directionality getDirectionality() {
      return directionality;
   }

   public void setDirectionality(Directionality directionality) {
      this.directionality = directionality;
   }

   public String getLaneWidth() {
      return laneWidth;
   }

   public void setLaneWidth(String laneWidth) {
      this.laneWidth = laneWidth;
   }

   public Anchor getAnchor() {
      return anchor;
   }

   public void setAnchor(Anchor anchor) {
      this.anchor = anchor;
   }
}
