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

@JsonPropertyOrder({ "direction", "extent", "laneWidth", "circle" })
public class GeometricProjection extends Asn1Object {
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
