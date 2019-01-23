/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.ode.plugin;

import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.plugin.j2735.OdeGeoRegion;

public class SituationDataWarehouse {
   public static class SDW extends OdeObject {

      private static final long serialVersionUID = -7731139391317960325L;

      public enum TimeToLive {
         oneminute, 
         thirtyminutes, 
         oneday, 
         oneweek, 
         onemonth, 
         oneyear
      }

      private OdeGeoRegion serviceRegion;
      private TimeToLive ttl = null;
      private String groupID = null;
      private String deliverystart;
      private String deliverystop;
      private String recordId = null;

      public OdeGeoRegion getServiceRegion() {
         return serviceRegion;
      }

      public void setServiceRegion(OdeGeoRegion serviceRegion) {
         this.serviceRegion = serviceRegion;
      }

      public TimeToLive getTtl() {
         if (ttl == null) return TimeToLive.thirtyminutes;
         return ttl;
      }

      public void setTtl(TimeToLive ttl) {
         this.ttl = ttl;
      }

      public String getGroupID() {
         return groupID;
      }

      public void setGroupID(String groupID) {
         this.groupID = groupID;
      }

      public String getDeliverystop() {
         return deliverystop;
      }

      public void setDeliverystop(String deliverystop) {
         this.deliverystop = deliverystop;
      }

      public String getDeliverystart() {
         return deliverystart;
      }

      public void setDeliverystart(String deliverystart) {
         this.deliverystart = deliverystart;
      }

      public String getRecordId() {
         return recordId;
      }

      public void setRecordId(String recordId) {
         this.recordId = recordId;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((deliverystart == null) ? 0 : deliverystart.hashCode());
         result = prime * result + ((deliverystop == null) ? 0 : deliverystop.hashCode());
         result = prime * result + ((groupID == null) ? 0 : groupID.hashCode());
         result = prime * result + ((recordId == null) ? 0 : recordId.hashCode());
         result = prime * result + ((serviceRegion == null) ? 0 : serviceRegion.hashCode());
         result = prime * result + ((ttl == null) ? 0 : ttl.hashCode());
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
         SDW other = (SDW) obj;
         if (deliverystart == null) {
            if (other.deliverystart != null)
               return false;
         } else if (!deliverystart.equals(other.deliverystart))
            return false;
         if (deliverystop == null) {
            if (other.deliverystop != null)
               return false;
         } else if (!deliverystop.equals(other.deliverystop))
            return false;
         if (groupID == null) {
            if (other.groupID != null)
               return false;
         } else if (!groupID.equals(other.groupID))
            return false;
         if (recordId == null) {
            if (other.recordId != null)
               return false;
         } else if (!recordId.equals(other.recordId))
            return false;
         if (serviceRegion == null) {
            if (other.serviceRegion != null)
               return false;
         } else if (!serviceRegion.equals(other.serviceRegion))
            return false;
         if (ttl != other.ttl)
            return false;
         return true;
      }

      
   }
   private SituationDataWarehouse() {
      throw new UnsupportedOperationException();
   }
}
