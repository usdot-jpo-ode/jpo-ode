/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.ode.plugin.j2735.timstorage;

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
   private DirectionOfUse directionality;

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

   public String getClosedPath() {
     return closedPath;
   }

   public void setClosedPath(String closedPath) {
     this.closedPath = closedPath;
   }


  public DirectionOfUse getDirectionality() {
    return directionality;
  }

  public void setDirectionality(DirectionOfUse directionality) {
    this.directionality = directionality;
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((anchor == null) ? 0 : anchor.hashCode());
    result = prime * result + ((closedPath == null) ? 0 : closedPath.hashCode());
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((direction == null) ? 0 : direction.hashCode());
    result = prime * result + ((directionality == null) ? 0 : directionality.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((laneWidth == null) ? 0 : laneWidth.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    GeographicalPath other = (GeographicalPath) obj;
    if (anchor == null) {
      if (other.anchor != null)
        return false;
    } else if (!anchor.equals(other.anchor))
      return false;
    if (closedPath == null) {
      if (other.closedPath != null)
        return false;
    } else if (!closedPath.equals(other.closedPath))
      return false;
    if (description == null) {
      if (other.description != null)
        return false;
    } else if (!description.equals(other.description))
      return false;
    if (direction == null) {
      if (other.direction != null)
        return false;
    } else if (!direction.equals(other.direction))
      return false;
    if (directionality == null) {
      if (other.directionality != null)
        return false;
    } else if (!directionality.equals(other.directionality))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (laneWidth == null) {
      if (other.laneWidth != null)
        return false;
    } else if (!laneWidth.equals(other.laneWidth))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }
}
