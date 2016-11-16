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
package us.dot.its.jpo.ode.asn;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.bah.ode.asn.oss.dsrc.Position3D;

import us.dot.its.jpo.ode.model.OdeObject;

public class OdePosition3D extends OdeObject {
   
   private static final long serialVersionUID = -119625427641036294L;

   private BigDecimal latitude;  // in degrees
   private BigDecimal longitude; // in degrees
   private BigDecimal elevation; // in meters

   public OdePosition3D(BigDecimal latitude, BigDecimal longitude, BigDecimal elevation) {
      super();
      this.latitude = latitude;
      this.longitude = longitude;
      this.elevation = elevation;
   }

   public OdePosition3D(Position3D pos) {
      // private OdePosition3D position;
      //    Position3D ::=  SEQUENCE {
      //       lat         Latitude,   -- in 1/10th micro degrees
      //       long        Longitude,  -- in 1/10th micro degrees
      //       elevation   Elevation   OPTIONAL  
      //       }
      //Latitude ::= INTEGER (-900000000..900000001)  
      //      -- LSB = 1/10 micro degree
      //      -- Providing a range of plus-minus 90 degrees
      //Longitude ::= INTEGER (-1800000000..1800000001)  
      //      -- LSB = 1/10 micro degree
      //      -- Providing a range of plus-minus 180 degrees
      // Elevation ::= OCTET STRING (SIZE(2))
      // -- 1 decimeter LSB (10 cm) 
      // -- Encode elevations from 0 to 6143.9 meters 
      // -- above the reference ellipsoid as 0x0000 to 0xEFFF.  
      // -- Encode elevations from -409.5 to -0.1 meters, 
      // -- i.e. below the reference ellipsoid, as 0xF001 to 0xFFFF
      // -- unknown as 0xF000
      
      if (pos != null) {
         setLatitude(pos.lat != null ? BigDecimal.valueOf(pos.lat.longValue(),
               7) : null);
         setLongitude(pos._long != null ? BigDecimal.valueOf(
               pos._long.longValue(), 7) : null);
         if (pos.elevation != null) {
            int elev = ByteBuffer.wrap(pos.elevation.byteArrayValue()).order(ByteOrder.BIG_ENDIAN).getShort();
            if (elev == 0xF000) {
               setElevation(null);
            } else if (elev >= 0x0000 && elev <= 0xEFFF) {
               setElevation(BigDecimal.valueOf(elev, 1));
            } else {
               setElevation(BigDecimal.valueOf(-elev, 1));
            }
         } else {
            setElevation(null);
         }
      }
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
      result = prime * result
            + ((elevation == null) ? 0 : elevation.hashCode());
      result = prime * result + ((latitude == null) ? 0 : latitude.hashCode());
      result = prime * result
            + ((longitude == null) ? 0 : longitude.hashCode());
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
      OdePosition3D other = (OdePosition3D) obj;
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
