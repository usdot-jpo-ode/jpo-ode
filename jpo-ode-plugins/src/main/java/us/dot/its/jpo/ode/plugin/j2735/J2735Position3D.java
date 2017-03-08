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

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.Elevation;
import us.dot.its.jpo.ode.j2735.dsrc.Latitude;
import us.dot.its.jpo.ode.j2735.dsrc.Longitude;
import us.dot.its.jpo.ode.j2735.dsrc.Position3D;
import us.dot.its.jpo.ode.j2735.dsrc.Position3D.Regional;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssPosition3D;

public class J2735Position3D extends Asn1Object {
   private static final long serialVersionUID = 1L;

   private BigDecimal latitude; // in degrees
   private BigDecimal longitude; // in degrees
   private BigDecimal elevation; // in meters

   public J2735Position3D() {
      super();
   }

   public J2735Position3D(BigDecimal latitude, BigDecimal longitude, BigDecimal elevation) {
      super();
      this.latitude = latitude;
      this.longitude = longitude;
      this.elevation = elevation;
   }

   public J2735Position3D(Long lat, Long lon, Long elev) {
      J2735Position3D gpos = OssPosition3D.geneticPosition3D(
            new Position3D(new Latitude(lat), new Longitude(lon), new Elevation(elev), new Regional()));
      this.latitude = gpos.latitude;
      this.longitude = gpos.longitude;
      this.elevation = gpos.elevation;
   }

   public BigDecimal getLatitude() {
      return latitude;
   }

   public void setLatitude(BigDecimal latitude) {
      this.latitude = latitude;
   }

   public BigDecimal getLongitude() {
      return longitude;
   }

   public void setLongitude(BigDecimal longitude) {
      this.longitude = longitude;
   }

   public BigDecimal getElevation() {
      return elevation;
   }

   public void setElevation(BigDecimal elevation) {
      this.elevation = elevation;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((elevation == null) ? 0 : elevation.hashCode());
      result = prime * result + ((latitude == null) ? 0 : latitude.hashCode());
      result = prime * result + ((longitude == null) ? 0 : longitude.hashCode());
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
      J2735Position3D other = (J2735Position3D) obj;
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
      if (longitude == null) {
         if (other.longitude != null)
            return false;
      } else if (!longitude.equals(other.longitude))
         return false;
      return true;
   }

}
