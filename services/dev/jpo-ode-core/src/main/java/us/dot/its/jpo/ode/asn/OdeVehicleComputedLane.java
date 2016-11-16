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

import com.bah.ode.asn.oss.dsrc.Approach.ComputedLanes;
import com.bah.ode.asn.oss.dsrc.VehicleComputedLane;

public class OdeVehicleComputedLane extends OdeLane {
   private static final long serialVersionUID = 2620159885524160754L;

   private OdeVehicleLaneAttributes laneAttributes;
   private Integer refLaneNum;
   private Integer lineOffset;
   private List<OdeLaneOffsets> keepOutList;

   public OdeVehicleComputedLane() {
      super();
   }

   public OdeVehicleComputedLane(VehicleComputedLane vcl) {
      super(vcl.getLaneNumber(), vcl.getLaneWidth(), vcl.getKeepOutList(),
            vcl.getConnectsTo());
      
      if (vcl.hasLaneAttributes())
         setLaneAttributes(new OdeVehicleLaneAttributes(vcl.getLaneAttributes()));
      
      if (vcl.refLaneNum != null)
         setRefLaneNum((int) vcl.refLaneNum.byteArrayValue()[0]);
      
      if (vcl.lineOffset != null)
         setLineOffset(vcl.lineOffset.intValue());
      
      if (vcl.hasKeepOutList())
         setKeepOutList(OdeLaneOffsets.createList(vcl.getKeepOutList()));
   }

   public OdeVehicleLaneAttributes getLaneAttributes() {
      return laneAttributes;
   }

   public OdeVehicleComputedLane setLaneAttributes(
         OdeVehicleLaneAttributes laneAttributes) {
      this.laneAttributes = laneAttributes;
      return this;
   }

   public Integer getRefLaneNum() {
      return refLaneNum;
   }

   public OdeVehicleComputedLane setRefLaneNum(Integer refLaneNum) {
      this.refLaneNum = refLaneNum;
      return this;
   }

   public Integer getLineOffset() {
      return lineOffset;
   }

   public OdeVehicleComputedLane setLineOffset(Integer lineOffset) {
      this.lineOffset = lineOffset;
      return this;
   }

   public List<OdeLaneOffsets> getKeepOutList() {
      return keepOutList;
   }

   public OdeVehicleComputedLane setKeepOutList(
         List<OdeLaneOffsets> keepOutList) {
      this.keepOutList = keepOutList;
      return this;
   }

   public static List<OdeVehicleComputedLane> createList(ComputedLanes computedLanes) {
      if (computedLanes == null)
         return null;

      ArrayList<OdeVehicleComputedLane> vcls = new ArrayList<OdeVehicleComputedLane>();

      for (VehicleComputedLane vcl : computedLanes.elements) {
         if (vcl != null) {
            vcls.add(new OdeVehicleComputedLane(vcl));
         }
      }
      return vcls;
   }

}
