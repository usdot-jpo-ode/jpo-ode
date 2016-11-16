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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.bah.ode.asn.oss.dsrc.Intersection_;
import com.bah.ode.asn.oss.dsrc.MapData.Intersections_;

import us.dot.its.jpo.ode.util.ASN1Utils;
import us.dot.its.jpo.ode.util.CodecUtils;

public class OdeIntersection {
   private String name;
   private String id;
   private OdePosition3D refPoint;
   private String refInterNum;
   private BigDecimal orientation;
   private Integer laneWidth;
   private OdeIntersectionStatusObject type;
   private List<OdeApproachObject> approaches;
   private List<OdeSignalControlZone> preemptZones;
   private List<OdeSignalControlZone> priorityZones;

   public OdeIntersection() {
      super();
   }

   public OdeIntersection(Intersection_ intsct) {
	   setApproaches(OdeApproachObject.createList(intsct.getApproaches()));
	   
	   if (intsct.id != null)
	      setId(CodecUtils.toHex(intsct.id.byteArrayValue()));
	   
	   if (intsct.hasLaneWidth())
	      setLaneWidth(intsct.getLaneWidth().intValue());
	   
	   if (intsct.hasName())
	      setName(intsct.getName().stringValue());
	   
	   if (intsct.hasOrientation())
	      setOrientation(ASN1Utils.convert(intsct.getOrientation()));
	   
	   if (intsct.hasPreemptZones())
	      setPreemptZones(OdeSignalControlZone.createList(intsct.getPreemptZones().elements));
	   
	   if (intsct.hasPriorityZones())
	      setPriorityZones(OdeSignalControlZone.createList(intsct.getPriorityZones().elements));
	   
	   if (intsct.hasRefInterNum())
	      setRefInterNum(CodecUtils.toHex(intsct.getRefInterNum().byteArrayValue()));
	   
	   if (intsct.hasRefPoint())
	      setRefPoint(new OdePosition3D(intsct.getRefPoint()));
	   
	   if (intsct.hasType())
	      setType(new OdeIntersectionStatusObject(intsct.getType()));
   }

   public static List<OdeIntersection> createList(Intersections_ ints) {
      if (ints == null)
         return null;
      
      ArrayList<OdeIntersection> intersections = new ArrayList<OdeIntersection>();
      
      for (Intersection_ i : ints.elements) {
         if (i != null)
            intersections.add(new OdeIntersection(i));
      }
      return intersections;
   }

   public String getName() {
      return name;
   }

   public OdeIntersection setName(String name) {
      this.name = name;
      return this;
   }

   public String getId() {
      return id;
   }

   public OdeIntersection setId(String id) {
      this.id = id;
      return this;
   }

   public OdePosition3D getRefPoint() {
      return refPoint;
   }

   public OdeIntersection setRefPoint(OdePosition3D refPoint) {
      this.refPoint = refPoint;
      return this;
   }

   public String getRefInterNum() {
      return refInterNum;
   }

   public OdeIntersection setRefInterNum(String refInterNum) {
      this.refInterNum = refInterNum;
      return this;
   }

   public BigDecimal getOrientation() {
      return orientation;
   }

   public OdeIntersection setOrientation(BigDecimal orientation) {
      this.orientation = orientation;
      return this;
   }

   public Integer getLaneWidth() {
      return laneWidth;
   }

   public OdeIntersection setLaneWidth(Integer laneWidthCm) {
      this.laneWidth = laneWidthCm;
      return this;
   }

   public OdeIntersectionStatusObject getType() {
      return type;
   }

   public OdeIntersection setType(OdeIntersectionStatusObject type) {
      this.type = type;
      return this;
   }

   public List<OdeApproachObject> getApproaches() {
      return approaches;
   }

   public OdeIntersection setApproaches(List<OdeApproachObject> approaches) {
      this.approaches = approaches;
      return this;
   }

   public List<OdeSignalControlZone> getPreemptZones() {
      return preemptZones;
   }

   public OdeIntersection setPreemptZones(
         List<OdeSignalControlZone> preemptZones) {
      this.preemptZones = preemptZones;
      return this;
   }

   public List<OdeSignalControlZone> getPriorityZones() {
      return priorityZones;
   }

   public OdeIntersection setPriorityZones(
         List<OdeSignalControlZone> priorityZones) {
      this.priorityZones = priorityZones;
      return this;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result
            + ((approaches == null) ? 0 : approaches.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result
            + ((laneWidth == null) ? 0 : laneWidth.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result
            + ((orientation == null) ? 0 : orientation.hashCode());
      result = prime * result
            + ((preemptZones == null) ? 0 : preemptZones.hashCode());
      result = prime * result
            + ((priorityZones == null) ? 0 : priorityZones.hashCode());
      result = prime * result
            + ((refInterNum == null) ? 0 : refInterNum.hashCode());
      result = prime * result + ((refPoint == null) ? 0 : refPoint.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
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
      OdeIntersection other = (OdeIntersection) obj;
      if (approaches == null) {
         if (other.approaches != null)
            return false;
      } else if (!approaches.equals(other.approaches))
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (laneWidth == null) {
         if (other.laneWidth != null)
            return false;
      } else if (!laneWidth.equals(other.laneWidth))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (orientation == null) {
         if (other.orientation != null)
            return false;
      } else if (!orientation.equals(other.orientation))
         return false;
      if (preemptZones == null) {
         if (other.preemptZones != null)
            return false;
      } else if (!preemptZones.equals(other.preemptZones))
         return false;
      if (priorityZones == null) {
         if (other.priorityZones != null)
            return false;
      } else if (!priorityZones.equals(other.priorityZones))
         return false;
      if (refInterNum == null) {
         if (other.refInterNum != null)
            return false;
      } else if (!refInterNum.equals(other.refInterNum))
         return false;
      if (refPoint == null) {
         if (other.refPoint != null)
            return false;
      } else if (!refPoint.equals(other.refPoint))
         return false;
      if (type == null) {
         if (other.type != null)
            return false;
      } else if (!type.equals(other.type))
         return false;
      return true;
   }

}
