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

import com.bah.ode.asn.oss.dsrc.ConnectsTo;

import us.dot.its.jpo.ode.model.OdeObject;

public class OdeConnectsTo extends OdeObject {
   private static final long serialVersionUID = -6187876493611955973L;

   public enum LaneManeuverCode {
      UNKNOWN, U_TURN, LEFT_TURN, RIGHT_TURN, STRAIGHT_AHEAD, SOFT_LEFT_TURN, SOFT_RIGHT_TURN;

      public static final LaneManeuverCode[] values = values();
   }

   private Integer laneNumber;
   private LaneManeuverCode laneManeuverCode;

   public OdeConnectsTo() {
      super();
   }

   public OdeConnectsTo(Integer laneNumber, LaneManeuverCode laneManeuverCode) {
      super();
      this.laneNumber = laneNumber;
      this.laneManeuverCode = laneManeuverCode;
   }

   public static List<OdeConnectsTo> createList(ConnectsTo connectsTo) {
      if (connectsTo == null)
         return null;
      
      byte[] bytes = connectsTo.byteArrayValue();
      ArrayList<OdeConnectsTo> odeConnectsTos = new ArrayList<OdeConnectsTo>();

      for (int i = 0; i < bytes.length; i += 2) {
         odeConnectsTos.add(new OdeConnectsTo((int) bytes[i],
               LaneManeuverCode.values[bytes[i + 1]]));
      }

      return odeConnectsTos;
   }

   public Integer getLaneNumber() {
      return laneNumber;
   }

   public OdeConnectsTo setLaneNumber(Integer laneNumber) {
      this.laneNumber = laneNumber;
      return this;
   }

   public LaneManeuverCode getLaneManeuverCode() {
      return laneManeuverCode;
   }

   public OdeConnectsTo setLaneManeuverCode(
         LaneManeuverCode laneManeuverCode) {
      this.laneManeuverCode = laneManeuverCode;
      return this;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result
            + ((laneManeuverCode == null) ? 0 : laneManeuverCode.hashCode());
      result = prime * result
            + ((laneNumber == null) ? 0 : laneNumber.hashCode());
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
      OdeConnectsTo other = (OdeConnectsTo) obj;
      if (laneManeuverCode != other.laneManeuverCode)
         return false;
      if (laneNumber == null) {
         if (other.laneNumber != null)
            return false;
      } else if (!laneNumber.equals(other.laneNumber))
         return false;
      return true;
   }

}
