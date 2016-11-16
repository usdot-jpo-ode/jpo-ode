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

import java.util.ArrayList;
import java.util.List;

import com.bah.ode.asn.oss.dsrc.Approach.Barriers;
import com.bah.ode.asn.oss.dsrc.BarrierLane;

public class OdeBarrierLane extends OdeLane {
   private static final long serialVersionUID = -1396239437559684194L;

   private Integer barrierAttributes;

   public OdeBarrierLane() {
      super();
   }

   public OdeBarrierLane(BarrierLane bl) {
      super(bl.getLaneNumber(), bl.getLaneWidth(), bl.getNodeList(), null);
      if (bl.barrierAttributes != null)
         setBarrierAttributes(bl.barrierAttributes.intValue());
   }

   public Integer getBarrierAttributes() {
      return barrierAttributes;
   }

   public OdeLane setBarrierAttributes(Integer barrierAttributes) {
      this.barrierAttributes = barrierAttributes;
      return this;
   }

   public static List<OdeBarrierLane> createList(Barriers barriers) {
      ArrayList<OdeBarrierLane> bls = null;
      if (barriers != null) {
         bls = new ArrayList<OdeBarrierLane>();
         for (BarrierLane bl : barriers.elements) {
            if (bl != null) {
               bls.add(new OdeBarrierLane(bl));
            }
         }
      }
      return bls;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result
            + ((barrierAttributes == null) ? 0 : barrierAttributes.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      OdeBarrierLane other = (OdeBarrierLane) obj;
      if (barrierAttributes == null) {
         if (other.barrierAttributes != null)
            return false;
      } else if (!barrierAttributes.equals(other.barrierAttributes))
         return false;
      return true;
   }

}
