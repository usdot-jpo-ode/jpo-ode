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

import us.dot.its.jpo.ode.j2735.dsrc.IntersectionStatusObject;
import us.dot.its.jpo.ode.util.CodecUtils;

public class OdeIntersectionStatusObject {
   public static final byte NONE = 0;
   public static final byte MANUAL_CONTROL_ENABLED = 1; // Manual Control is
                                                        // enabled.
                                                        // Timing reported is
                                                        // per
                                                        // programmed values,
                                                        // etc but person at
                                                        // cabinet can
                                                        // manually request that
                                                        // certain intervals are
                                                        // terminated
                                                        // early (e.g. green).
   public static final byte STOP_TIME_ACTIVATED = 2; // Stop Time is activated
                                                     // and all counting/timing
                                                     // has stopped.

   public static final byte IN_CONFLICT_PATH = 4; // Intersection is in Conflict
                                                  // Flash.
   public static final byte PREEMPT_IS_ACTIVE = 8; // Preempt is Active
   public static final byte TRANSIT_SIGNAL_PRIORITY_IS_ACTIVE = 16; // Transit
                                                                    // Signal
                                                                    // Priority
                                                                    // (TSP) is
                                                                    // Active
   public static final byte RESERVED_1 = 32; // Reserved
   public static final byte RESERVED_2 = 64; // Reserved
   public static final byte RESERVED_3 = (byte) 128; // Reserved as zero

   private String status;

   public OdeIntersectionStatusObject() {
      super();
   }

   public OdeIntersectionStatusObject(IntersectionStatusObject status) {
      super();

      if (status != null)
         setStatus(CodecUtils.toHex(status.byteArrayValue()));
   }

   public String getStatus() {
      return status;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((status == null) ? 0 : status.hashCode());
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
      OdeIntersectionStatusObject other = (OdeIntersectionStatusObject) obj;
      if (status == null) {
         if (other.status != null)
            return false;
      } else if (!status.equals(other.status))
         return false;
      return true;
   }

   
}
