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

import com.bah.ode.asn.oss.dsrc.Approach.Crosswalks;
import com.bah.ode.asn.oss.dsrc.CrosswalkLane;

public class OdeCrosswalkLane extends OdeLane {
   private static final long serialVersionUID = 2544575524269832634L;

   public class CrosswalkLaneAttributes {
      public static final long NO_DATA = 0;
      public static final long TWO_WAY_PATH = 1;
      public static final long PEDESTRIAN_CROSSWALK = 2;
      public static final long BIKE_LANE = 4;
      public static final long RAILROAD_TRACK_PRESENT = 8;
      public static final long ONE_WAY_PATH_OF_TRAVEL = 16;
      public static final long PEDESTRIAN_CROSSWALK_TYPE_A = 32;
      public static final long PEDESTRIAN_CROSSWALK_TYPE_B = 64;
      public static final long PEDESTRIAN_CROSSWALK_TYPE_C = 128;

      private long attributes;

      public CrosswalkLaneAttributes() {
         super();
      }

      public CrosswalkLaneAttributes(long attributes) {
         super();
         this.attributes = attributes;
      }

      public long getAttributes() {
         return attributes;
      }

      public void setAttributes(long attributes) {
         this.attributes = attributes;
      }

   }
   
   public CrosswalkLaneAttributes laneAttributes;
   public List<OdeLaneOffsets> keepOutList;

   public OdeCrosswalkLane() {
      super();
   }


   public OdeCrosswalkLane(CrosswalkLane cwl) {
      super(cwl.getLaneNumber(), cwl.getLaneWidth(), cwl.getNodeList(), cwl.getConnectsTo());
      if (cwl.laneAttributes != null)
         this.laneAttributes = new CrosswalkLaneAttributes(cwl.laneAttributes.longValue());
      if (cwl.hasKeepOutList())
         this.keepOutList = OdeLaneOffsets.createList(cwl.getKeepOutList());
   }

   public CrosswalkLaneAttributes getLaneAttributes() {
      return laneAttributes;
   }

   public OdeCrosswalkLane setLaneAttributes(
         CrosswalkLaneAttributes laneAttributes) {
      this.laneAttributes = laneAttributes;
      return this;
   }

   public List<OdeLaneOffsets> getKeepOutList() {
      return keepOutList;
   }

   public OdeCrosswalkLane setKeepOutList(List<OdeLaneOffsets> keepOutList) {
      this.keepOutList = keepOutList;
      return this;
   }

   public static List<OdeCrosswalkLane> createList(Crosswalks crosswalks) {
      if (crosswalks == null)
         return null;
      
      ArrayList<OdeCrosswalkLane> cwls = new ArrayList<OdeCrosswalkLane>();

      if (crosswalks != null) {
         for (CrosswalkLane cwl : crosswalks.elements) {
            if (cwl != null) {
               cwls.add(new OdeCrosswalkLane(cwl));
            }
         }
      }
      return cwls;
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
      OdeCrosswalkLane other = (OdeCrosswalkLane) obj;
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
