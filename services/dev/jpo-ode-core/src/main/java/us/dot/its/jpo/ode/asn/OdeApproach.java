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

import com.bah.ode.asn.oss.dsrc.Approach;

import us.dot.its.jpo.ode.model.OdeObject;

public class OdeApproach  extends OdeObject {
   private static final long serialVersionUID = 4358013875768916528L;
   
   public String name;
   public Integer id;
   public List<OdeVehicleReferenceLane> drivingLanes;
   public List<OdeVehicleComputedLane> computedLanes;
   public List<OdeSpecialLane> trainsAndBuses;
   public List<OdeBarrierLane> barriers;
   public List<OdeCrosswalkLane> crosswalks;

   public OdeApproach() {
      super();
   }

   public OdeApproach(Approach a) {
      if (a.hasBarriers())
         setBarriers(OdeBarrierLane.createList(a.getBarriers()));
      if (a.hasComputedLanes())
         setComputedLanes(OdeVehicleComputedLane.createList(a.getComputedLanes()));
      if (a.hasCrosswalks())
         setCrosswalks(OdeCrosswalkLane.createList(a.getCrosswalks()));
      if (a.hasDrivingLanes())
         setDrivingLanes(OdeVehicleReferenceLane.createList(a.getDrivingLanes()));
      if (a.hasTrainsAndBuses())
         setTrainsAndBuses(OdeSpecialLane.createList(a.getTrainsAndBuses()));
      if (a.hasName())
         setName(a.getName().stringValue());
      
      if (a.id != null)
         setId(a.id.intValue());
   }

   public String getName() {
      return name;
   }

   public OdeApproach setName(String name) {
      this.name = name;
      return this;
   }

   public Integer getId() {
      return id;
   }

   public OdeApproach setId(Integer id) {
      this.id = id;
      return this;
   }

   public List<OdeVehicleReferenceLane> getDrivingLanes() {
      return drivingLanes;
   }

   public OdeApproach setDrivingLanes(
         List<OdeVehicleReferenceLane> drivingLanes) {
      this.drivingLanes = drivingLanes;
      return this;
   }

   public List<OdeVehicleComputedLane> getComputedLanes() {
      return computedLanes;
   }

   public OdeApproach setComputedLanes(
         List<OdeVehicleComputedLane> computedLanes) {
      this.computedLanes = computedLanes;
      return this;
   }

   public List<OdeSpecialLane> getTrainsAndBuses() {
      return trainsAndBuses;
   }

   public OdeApproach setTrainsAndBuses(List<OdeSpecialLane> trainsAndBuses) {
      this.trainsAndBuses = trainsAndBuses;
      return this;
   }

   public List<OdeBarrierLane> getBarriers() {
      return barriers;
   }

   public OdeApproach setBarriers(List<OdeBarrierLane> barriers) {
      this.barriers = barriers;
      return this;
   }

   public List<OdeCrosswalkLane> getCrosswalks() {
      return crosswalks;
   }

   public OdeApproach setCrosswalks(List<OdeCrosswalkLane> crosswalks) {
      this.crosswalks = crosswalks;
      return this;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((barriers == null) ? 0 : barriers.hashCode());
      result = prime * result
            + ((computedLanes == null) ? 0 : computedLanes.hashCode());
      result = prime * result
            + ((crosswalks == null) ? 0 : crosswalks.hashCode());
      result = prime * result
            + ((drivingLanes == null) ? 0 : drivingLanes.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result
            + ((trainsAndBuses == null) ? 0 : trainsAndBuses.hashCode());
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
      OdeApproach other = (OdeApproach) obj;
      if (barriers == null) {
         if (other.barriers != null)
            return false;
      } else if (!barriers.equals(other.barriers))
         return false;
      if (computedLanes == null) {
         if (other.computedLanes != null)
            return false;
      } else if (!computedLanes.equals(other.computedLanes))
         return false;
      if (crosswalks == null) {
         if (other.crosswalks != null)
            return false;
      } else if (!crosswalks.equals(other.crosswalks))
         return false;
      if (drivingLanes == null) {
         if (other.drivingLanes != null)
            return false;
      } else if (!drivingLanes.equals(other.drivingLanes))
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (trainsAndBuses == null) {
         if (other.trainsAndBuses != null)
            return false;
      } else if (!trainsAndBuses.equals(other.trainsAndBuses))
         return false;
      return true;
   }

}
