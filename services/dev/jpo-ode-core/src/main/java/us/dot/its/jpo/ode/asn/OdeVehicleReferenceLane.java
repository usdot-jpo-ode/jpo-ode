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

import com.bah.ode.asn.oss.dsrc.Approach.DrivingLanes;
import com.bah.ode.asn.oss.dsrc.VehicleReferenceLane;

public class OdeVehicleReferenceLane extends OdeLane {
   
   private static final long serialVersionUID = -7303552550764116223L;
   
   private OdeVehicleLaneAttributes laneAttributes;
   private List<OdeLaneOffsets> keepOutList;

   public OdeVehicleReferenceLane() {
      super();
   }

   public OdeVehicleReferenceLane(VehicleReferenceLane dl) {
      super(dl.getLaneNumber(), dl.getLaneWidth(),
            dl.getNodeList(), dl.getConnectsTo());

      setLaneAttributes(new OdeVehicleLaneAttributes(
            dl.laneAttributes != null ? dl.laneAttributes.intValue()
                  : OdeVehicleLaneAttributes.NO_LANE_DATA));
      
      if (dl.hasKeepOutList())
         setKeepOutList(OdeLaneOffsets.createList(dl.getKeepOutList()));
   }

   public static List<OdeVehicleReferenceLane> createList(DrivingLanes drivingLanes) {
      if (drivingLanes == null)
         return null;

      ArrayList<OdeVehicleReferenceLane> rls = new ArrayList<OdeVehicleReferenceLane>();

      for (VehicleReferenceLane rl : drivingLanes.elements) {
         if (null != rl)
            rls.add(new OdeVehicleReferenceLane(rl));
      }
      
      return rls;
   }

   public OdeVehicleLaneAttributes getLaneAttributes() {
      return laneAttributes;
   }

   public OdeVehicleReferenceLane setLaneAttributes(
         OdeVehicleLaneAttributes laneAttributes) {
      this.laneAttributes = laneAttributes;
      return this;
   }

   public List<OdeLaneOffsets> getKeepOutList() {
      return keepOutList;
   }

   public OdeVehicleReferenceLane setKeepOutList(
         List<OdeLaneOffsets> keepOutList) {
      this.keepOutList = keepOutList;
      return this;
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
      OdeVehicleReferenceLane other = (OdeVehicleReferenceLane) obj;
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
