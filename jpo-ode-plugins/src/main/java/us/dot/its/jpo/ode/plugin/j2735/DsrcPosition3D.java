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

import com.google.gson.annotations.SerializedName;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class DsrcPosition3D extends Asn1Object {
   private static final long serialVersionUID = 1L;

   @SerializedName("lat")
   private Long lat; // in degrees
   @SerializedName("long")
   private Long _long; // in degrees
   @SerializedName("elevation")
   private Long elevation; // in meters

   public DsrcPosition3D() {
      super();
   }

   public DsrcPosition3D(Long latitude, Long longitude, Long elevation) {
      super();
      this.lat = latitude;
      this._long = longitude;
      this.elevation = elevation;
   }

   public Long getLatitude() {
      return lat;
   }

   public void setLatitude(Long latitude) {
      this.lat = latitude;
   }

   public Long getLongitude() {
      return _long;
   }

   public void setLongitude(Long longitude) {
      this._long = longitude;
   }

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
      result = prime * result + ((_long == null) ? 0 : _long.hashCode());
      result = prime * result + ((elevation == null) ? 0 : elevation.hashCode());
      result = prime * result + ((lat == null) ? 0 : lat.hashCode());
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
      if (_long == null) {
         if (other._long != null)
            return false;
      } else if (!_long.equals(other._long))
         return false;
      if (elevation == null) {
         if (other.elevation != null)
            return false;
      } else if (!elevation.equals(other.elevation))
         return false;
      if (lat == null) {
         if (other.lat != null)
            return false;
      } else if (!lat.equals(other.lat))
         return false;
      return true;
   }

}
