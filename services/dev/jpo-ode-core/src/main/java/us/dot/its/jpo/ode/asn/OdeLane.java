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

import java.util.List;

import com.bah.ode.asn.oss.dsrc.ConnectsTo;
import com.bah.ode.asn.oss.dsrc.LaneNumber;
import com.bah.ode.asn.oss.dsrc.LaneWidth;
import com.bah.ode.asn.oss.dsrc.NodeList;

import us.dot.its.jpo.ode.model.OdeObject;

public class OdeLane extends OdeObject {
   
   private static final long serialVersionUID = -7836255625114649553L;

   private Integer laneNumber;
   private Integer laneWidth;
   private List<OdeLaneOffsets> nodeList;
   private List<OdeConnectsTo> connectsTo;

   public OdeLane() {
      super();
   }

   public OdeLane(LaneNumber laneNumber2, LaneWidth laneWidth2,
         NodeList nodeList2, ConnectsTo connectsTo2) {
      super();
      if (laneNumber2 != null)
         this.laneNumber = (int) laneNumber2.byteArrayValue()[0];
      
      if (laneWidth2 != null)
         this.laneWidth = laneWidth2.intValue();
      
      if (nodeList2 != null)
         this.nodeList = OdeLaneOffsets.createList(nodeList2);
      
      if (connectsTo2 != null)
         this.connectsTo = OdeConnectsTo.createList(connectsTo2);
   }

   public Integer getLaneNumber() {
      return laneNumber;
   }

   public OdeLane setLaneNumber(Integer laneNumber) {
      this.laneNumber = laneNumber;
      return this;
   }

   public Integer getLaneWidth() {
      return laneWidth;
   }

   public OdeLane setLaneWidth(Integer laneWidthCm) {
      this.laneWidth = laneWidthCm;
      return this;
   }

   public List<OdeLaneOffsets> getNodeList() {
      return nodeList;
   }

   public OdeLane setNodeList(List<OdeLaneOffsets> nodeList) {
      this.nodeList = nodeList;
      return this;
   }

   public List<OdeConnectsTo> getConnectsTo() {
      return connectsTo;
   }

   public OdeLane setConnectsTo(List<OdeConnectsTo> connectsTo) {
      this.connectsTo = connectsTo;
      return this;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result
            + ((connectsTo == null) ? 0 : connectsTo.hashCode());
      result = prime * result
            + ((laneNumber == null) ? 0 : laneNumber.hashCode());
      result = prime * result
            + ((laneWidth == null) ? 0 : laneWidth.hashCode());
      result = prime * result + ((nodeList == null) ? 0 : nodeList.hashCode());
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
      OdeLane other = (OdeLane) obj;
      if (connectsTo == null) {
         if (other.connectsTo != null)
            return false;
      } else if (!connectsTo.equals(other.connectsTo))
         return false;
      if (laneNumber == null) {
         if (other.laneNumber != null)
            return false;
      } else if (!laneNumber.equals(other.laneNumber))
         return false;
      if (laneWidth == null) {
         if (other.laneWidth != null)
            return false;
      } else if (!laneWidth.equals(other.laneWidth))
         return false;
      if (nodeList == null) {
         if (other.nodeList != null)
            return false;
      } else if (!nodeList.equals(other.nodeList))
         return false;
      return true;
   }

}
