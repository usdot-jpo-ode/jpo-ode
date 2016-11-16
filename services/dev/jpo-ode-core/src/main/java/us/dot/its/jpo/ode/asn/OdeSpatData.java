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

import com.bah.ode.asn.oss.semi.SpatRecord;

import us.dot.its.jpo.ode.model.OdeObject;

public class OdeSpatData extends OdeObject {

   private static final long serialVersionUID = 6674140338314980542L;

   private OdeDateTime dateTime;
   private String timestamp;
   private OdeIntersectionState intersections;

   public OdeSpatData(SpatRecord spatData) {
      if (spatData.timestamp != null) {
         setDateTime(new OdeDateTime(spatData.timestamp));
         setTimestamp(this.getDateTime().getISODateTime());
      }
      if (spatData.intersections != null)
         setIntersections(new OdeIntersectionState(spatData.getIntersections()));
   }

   public OdeDateTime getDateTime() {
      return dateTime;
   }

   public void setDateTime(OdeDateTime dateTime) {
      this.dateTime = dateTime;
   }

   public String getTimestamp() {
      return timestamp;
   }

   public void setTimestamp(String timestamp) {
      this.timestamp = timestamp;
   }

   public OdeIntersectionState getIntersections() {
      return intersections;
   }

   public void setIntersections(OdeIntersectionState intersections) {
      this.intersections = intersections;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((dateTime == null) ? 0 : dateTime.hashCode());
      result = prime * result
            + ((intersections == null) ? 0 : intersections.hashCode());
      result = prime * result
            + ((timestamp == null) ? 0 : timestamp.hashCode());
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
      OdeSpatData other = (OdeSpatData) obj;
      if (dateTime == null) {
         if (other.dateTime != null)
            return false;
      } else if (!dateTime.equals(other.dateTime))
         return false;
      if (intersections == null) {
         if (other.intersections != null)
            return false;
      } else if (!intersections.equals(other.intersections))
         return false;
      if (timestamp == null) {
         if (other.timestamp != null)
            return false;
      } else if (!timestamp.equals(other.timestamp))
         return false;
      return true;
   }

}
