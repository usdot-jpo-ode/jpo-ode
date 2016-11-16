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

import com.bah.ode.asn.oss.dsrc.ApproachObject;
import com.bah.ode.asn.oss.dsrc.Intersection_.Approaches;

import us.dot.its.jpo.ode.model.OdeObject;

public class OdeApproachObject extends OdeObject {

   private static final long serialVersionUID = 4326844331427902719L;
   
   public OdePosition3D refPoint;
   public Integer laneWidthCm;
   public OdeApproach approach;
   public OdeApproach egress;
   
	public OdeApproachObject() {
	   super();
   }

	public OdeApproachObject(ApproachObject approachObj) {
	   if (approachObj.hasApproach())
	      setApproach(new OdeApproach(approachObj.getApproach()));
	   
	   if (approachObj.hasEgress())
	      setEgress(new OdeApproach(approachObj.getEgress()));
	   
	   if (approachObj.hasLaneWidth())
	      setLaneWidthCm(approachObj.getLaneWidth().intValue());
	   
	   if (approachObj.hasRefPoint())
	      setRefPoint(new OdePosition3D(approachObj.getRefPoint()));
   }

   public OdePosition3D getRefPoint() {
		return refPoint;
	}

	public OdeApproachObject setRefPoint(OdePosition3D refPoint) {
		this.refPoint = refPoint;
		return this;
	}

	public Integer getLaneWidthCm() {
		return laneWidthCm;
	}

	public OdeApproachObject setLaneWidthCm(Integer laneWidthCm) {
		this.laneWidthCm = laneWidthCm;
		return this;
	}

	public OdeApproach getApproach() {
		return approach;
	}

	public OdeApproachObject setApproach(OdeApproach approach) {
		this.approach = approach;
		return this;
	}

	public OdeApproach getEgress() {
		return egress;
	}

	public OdeApproachObject setEgress(OdeApproach egress) {
		this.egress = egress;
		return this;
	}

   public static List<OdeApproachObject> createList(Approaches approaches) {

      List<OdeApproachObject> aos = null;
      if (approaches != null) {
         aos = new ArrayList<OdeApproachObject>();
         for (ApproachObject ao : approaches.elements) {
            if (ao != null) {
               aos.add(new OdeApproachObject(ao));
            }
         }
      }
      return aos;
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((approach == null) ? 0 : approach.hashCode());
      result = prime * result + ((egress == null) ? 0 : egress.hashCode());
      result = prime * result
            + ((laneWidthCm == null) ? 0 : laneWidthCm.hashCode());
      result = prime * result + ((refPoint == null) ? 0 : refPoint.hashCode());
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
      OdeApproachObject other = (OdeApproachObject) obj;
      if (approach == null) {
         if (other.approach != null)
            return false;
      } else if (!approach.equals(other.approach))
         return false;
      if (egress == null) {
         if (other.egress != null)
            return false;
      } else if (!egress.equals(other.egress))
         return false;
      if (laneWidthCm == null) {
         if (other.laneWidthCm != null)
            return false;
      } else if (!laneWidthCm.equals(other.laneWidthCm))
         return false;
      if (refPoint == null) {
         if (other.refPoint != null)
            return false;
      } else if (!refPoint.equals(other.refPoint))
         return false;
      return true;
   }

   
}
