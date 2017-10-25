/*******************************************************************************
 * Copyright (c) 2015 US DOT - Joint Program Office
 *
 * The Government has unlimited rights to all documents/material produced under 
 * this task order. All documents and materials, to include the source code of 
 * any software produced under this contract, shall be Government owned and the 
 * property of the Government with all rights and privileges of ownership/copyright 
 * belonging exclusively to the Government. These documents and materials may 
 * not be used or sold by the Contractor without written permission from the CO.
 * All materials supplied to the Government shall be the sole property of the 
 * Government and may not be used for any other purpose. This right does not 
 * abrogate any other Government rights.
 *
 * Contributors:
 *     Booz | Allen | Hamilton - initial API and implementation
 *******************************************************************************/
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
