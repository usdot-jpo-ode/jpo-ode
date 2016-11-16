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

import com.bah.ode.asn.oss.dsrc.VehicleLaneAttributes;

public class OdeVehicleLaneAttributes {
   public static final long NO_LANE_DATA = 0;
   public static final long EGRESS_PATH = 1; // a two-way path or an outbound
                                             // path is described
   public static final long MANEUVER_STRAIGHT_ALLOWED = 2;
   public static final long MANEUVER_LEFT_ALLOWED = 4;
   public static final long MANEUVER_RIGHT_ALLOWED = 8;
   public static final long YIELD = 16;
   public static final long MANEUVER_NO_U_TURN = 32;
   public static final long MANEUVER_NO_TURN_ON_RED = 64;
   public static final long MANEUVER_NO_STOP = 128;
   public static final long NO_STOP = 256;
   public static final long NO_TURN_ON_RED = 512;
   public static final long HOV_LANE = 1024;
   public static final long BUS_ONLY = 2048;
   public static final long BUS_AND_TAXI_ONLY = 4096;
   public static final long MANEUVER_HOV_LANE = 8192;
   public static final long MANEUVER_SHARED_LANE = 16384; // a "TWLTL" (two way
                                                          // left turn lane)
   public static final long MANEUVER_BIKE_LANE = 32768;

   private long attributes;

   public OdeVehicleLaneAttributes() {
      super();
   }

   public OdeVehicleLaneAttributes(long attributes) {
      super();
      this.attributes = attributes;
   }

   public OdeVehicleLaneAttributes(VehicleLaneAttributes laneAttributes) {
      setAttributes(laneAttributes.longValue());
   }

   public long getAttributes() {
      return attributes;
   }

   public void setAttributes(long attributes) {
      this.attributes = attributes;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (attributes ^ (attributes >>> 32));
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
      OdeVehicleLaneAttributes other = (OdeVehicleLaneAttributes) obj;
      if (attributes != other.attributes)
         return false;
      return true;
   }
   
   
}
