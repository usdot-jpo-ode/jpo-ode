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

@JsonPropertyOrder({ "center", "radius", "units" })
public class Circle extends Asn1Object {
   private static final long serialVersionUID = 1L;

   private Position center;

   @JsonProperty("radius")
   private String radius;

   @JsonProperty("units")
   private DistanceUnits units;

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

  public Position getCenter() {
    return center;
  }

  public void setCenter(Position center) {
    this.center = center;
  }

  public DistanceUnits getUnits() {
    return units;
  }

  public void setUnits(DistanceUnits units) {
    this.units = units;
  }

}
