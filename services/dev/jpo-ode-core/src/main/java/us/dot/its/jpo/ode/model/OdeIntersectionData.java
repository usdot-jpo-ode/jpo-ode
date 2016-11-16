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
package us.dot.its.jpo.ode.model;

import java.time.ZonedDateTime;

import com.bah.ode.asn.oss.semi.GeoRegion;
import com.bah.ode.asn.oss.semi.GroupID;
import com.bah.ode.asn.oss.semi.IntersectionSituationData;
import com.bah.ode.asn.oss.semi.TimeToLive;

import us.dot.its.jpo.ode.asn.OdeMapData;
import us.dot.its.jpo.ode.asn.OdeSpatData;
import us.dot.its.jpo.ode.util.DateTimeUtils;

public final class OdeIntersectionData extends DotWarehouseData
      implements HasTimestamp {
   private static final long serialVersionUID = -8672926422209668605L;

   private OdeMapData mapData;
   private OdeSpatData spatData;

   public OdeIntersectionData() {
      super();
   }

   public OdeIntersectionData(String serialId, GroupID groupID,
         TimeToLive timeToLive, GeoRegion serviceRegion) {
      super(serialId, groupID, timeToLive, serviceRegion);
   }

   public OdeIntersectionData(String streamId, long bundleId, long recordId) {
      super(streamId, bundleId, recordId);
   }

   public OdeIntersectionData(String serialId) {
      super(serialId);
   }

   public OdeIntersectionData(String serialId, IntersectionSituationData isd) {

      super(serialId, isd.getGroupID(), isd.getTimeToLive(), isd.getServiceRegion());

      if (isd.intersectionRecord != null) {
         if (isd.intersectionRecord.mapData != null)
            this.setMapData(new OdeMapData(isd.intersectionRecord.mapData));
         if (isd.intersectionRecord.spatData != null)
            this.setSpatData(new OdeSpatData(isd.intersectionRecord.spatData));
      }
   }

   public OdeMapData getMapData() {
      return mapData;
   }

   public OdeIntersectionData setMapData(OdeMapData mapData) {
      this.mapData = mapData;
      return this;
   }

   public OdeSpatData getSpatData() {
      return spatData;
   }

   public OdeIntersectionData setSpatData(OdeSpatData spatData) {
      this.spatData = spatData;
      return this;
   }

   @Override
   public ZonedDateTime getTimestamp() {
      return spatData.getDateTime().getZonedDateTime();
   }

   @Override
   public boolean isOnTime(ZonedDateTime start, ZonedDateTime end) {
      return DateTimeUtils.isBetweenTimesInclusive(getTimestamp(), start, end);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((mapData == null) ? 0 : mapData.hashCode());
      result = prime * result + ((spatData == null) ? 0 : spatData.hashCode());
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
      OdeIntersectionData other = (OdeIntersectionData) obj;
      if (mapData == null) {
         if (other.mapData != null)
            return false;
      } else if (!mapData.equals(other.mapData))
         return false;
      if (spatData == null) {
         if (other.spatData != null)
            return false;
      } else if (!spatData.equals(other.spatData))
         return false;
      return true;
   }

}
