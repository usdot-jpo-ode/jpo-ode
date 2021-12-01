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
package us.dot.its.jpo.ode.plugin.j2735;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

@JsonPropertyOrder({ "lat", "long", "elevation" })
public class DsrcPosition3D extends Asn1Object {
   private static final long serialVersionUID = 1L;
   private Long latitude; // in degrees
   private Long longitude; // in degrees
   private Long elevation; // in meters

   public DsrcPosition3D() {
      super();
   }

   public DsrcPosition3D(Long latitude, Long longitude, Long elevation) {
      super();
      this.latitude = latitude;
      this.longitude = longitude;
      this.elevation = elevation;
   }

   @JsonProperty("lat")
   public Long getLatitude() {
      return latitude;
   }

   public void setLatitude(Long latitude) {
      this.latitude = latitude;
   }

   @JsonProperty("long")
   public Long getLongitude() {
      return longitude;
   }

   public void setLongitude(Long longitude) {
      this.longitude = longitude;
   }

   @JsonProperty("elevation")
   public Long getElevation() {
      return elevation;
   }

   public void setElevation(Long elevation) {
      this.elevation = elevation;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((longitude == null) ? 0 : longitude.hashCode());
      result = prime * result + ((elevation == null) ? 0 : elevation.hashCode());
      result = prime * result + ((latitude == null) ? 0 : latitude.hashCode());
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
      DsrcPosition3D other = (DsrcPosition3D) obj;
      if (longitude == null) {
         if (other.longitude != null)
            return false;
      } else if (!longitude.equals(other.longitude))
         return false;
      if (elevation == null) {
         if (other.elevation != null)
            return false;
      } else if (!elevation.equals(other.elevation))
         return false;
      if (latitude == null) {
         if (other.latitude != null)
            return false;
      } else if (!latitude.equals(other.latitude))
         return false;
      return true;
   }

}
