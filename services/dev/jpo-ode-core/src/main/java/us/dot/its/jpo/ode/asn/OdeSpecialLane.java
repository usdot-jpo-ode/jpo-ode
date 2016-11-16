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

import com.bah.ode.asn.oss.dsrc.Approach.TrainsAndBuses;
import com.bah.ode.asn.oss.dsrc.SpecialLane;

public class OdeSpecialLane extends OdeLane {
   private static final long serialVersionUID = 4565607877091972560L;

   public class Attributes {
      public static final long NO_DATA = 0;
      public static final long EGRESS_PATH = 1;
      public static final long RAILROAD_TRACK = 2;
      public static final long TRANSIT_ONLY_LANE = 4;
      public static final long HOV_LANE = 8;
      public static final long BUS_ONLY = 16;
      public static final long VEHICLES_ENTERING = 32;
      public static final long VEHICLES_LEAVING = 64;
      public static final long RESERVED = 128;

      private long attributes;

      public Attributes() {
         super();
      }

      public Attributes(long attributes) {
         super();
         this.attributes = attributes;
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
         result = prime * result + getOuterType().hashCode();
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
         Attributes other = (Attributes) obj;
         if (!getOuterType().equals(other.getOuterType()))
            return false;
         if (attributes != other.attributes)
            return false;
         return true;
      }

      private OdeSpecialLane getOuterType() {
         return OdeSpecialLane.this;
      }

      
   }
   
   private Attributes laneAttributes;
   private List<OdeLaneOffsets> keepOutList;

   public OdeSpecialLane() {
      super();
   }

   public OdeSpecialLane(SpecialLane sl) {
      super(sl.getLaneNumber(), sl.getLaneWidth(), sl.getNodeList(),
            sl.getConnectsTo());

      if (sl.laneAttributes != null)
         setLaneAttributes(new Attributes(sl.laneAttributes.longValue()));

      if (sl.hasKeepOutList())
         setKeepOutList(OdeLaneOffsets.createList(sl.getKeepOutList()));
   }

   public Attributes getLaneAttributes() {
      return laneAttributes;
   }

   public OdeSpecialLane setLaneAttributes(
         Attributes laneAttributes) {
      this.laneAttributes = laneAttributes;
      return this;
   }

   public List<OdeLaneOffsets> getKeepOutList() {
      return keepOutList;
   }

   public OdeSpecialLane setKeepOutList(List<OdeLaneOffsets> keepOutList) {
      this.keepOutList = keepOutList;
      return this;
   }

   public static List<OdeSpecialLane> createList(
         TrainsAndBuses trainsAndBuses) {
      if (trainsAndBuses == null)
         return null;

      ArrayList<OdeSpecialLane> sls = new ArrayList<OdeSpecialLane>();

      if (trainsAndBuses != null) {
         for (SpecialLane sl : trainsAndBuses.elements) {
            if (sl != null) {
               sls.add(new OdeSpecialLane(sl));
            }
         }
      }
      return sls;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result
            + ((keepOutList == null) ? 0 : keepOutList.hashCode());
      result = prime * result
            + ((laneAttributes == null) ? 0 : laneAttributes.hashCode());
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
      OdeSpecialLane other = (OdeSpecialLane) obj;
      if (keepOutList == null) {
         if (other.keepOutList != null)
            return false;
      } else if (!keepOutList.equals(other.keepOutList))
         return false;
      if (laneAttributes == null) {
         if (other.laneAttributes != null)
            return false;
      } else if (!laneAttributes.equals(other.laneAttributes))
         return false;
      return true;
   }

}
