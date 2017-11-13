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
   }
   private SituationDataWarehouse() {
      throw new UnsupportedOperationException();
   }
}
