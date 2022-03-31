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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "lat", "llong", "elevation" })
public class Position extends Asn1Object {
   private static final long serialVersionUID = 1L;

   @JsonProperty("lat")
   private String lat;
   @JsonProperty("long")
   private String llong;
   @JsonProperty("elevation")
   private String elevation;

   public String getElevation() {
      return elevation;
   }

   public void setElevation(String elevation) {
      this.elevation = elevation;
   }

   public String getLlong() {
      return llong;
   }

   public void setLlong(String lon) {
      this.llong = lon;
   }

   public String getLat() {
      return lat;
   }

   public void setLat(String lat) {
      this.lat = lat;
   }
}
