package us.dot.its.jpo.ode.plugin;

import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.plugin.j2735.J2735GeoRegion;

public class SituationDataWarehouse {
   public static class SDW extends OdeObject {

      private static final long serialVersionUID = -7731139391317960325L;

      public enum TimeToLive {
         ONEMINUTE, THIRTYMINUTES, ONEDAY, ONEWEEK, ONEMONTH, ONEYEAR
      }

      private J2735GeoRegion serviceRegion;
      private TimeToLive ttl = TimeToLive.THIRTYMINUTES;

      public J2735GeoRegion getServiceRegion() {
         return serviceRegion;
      }

      public void setServiceRegion(J2735GeoRegion serviceRegion) {
         this.serviceRegion = serviceRegion;
      }

      public TimeToLive getTtl() {
         return ttl;
      }

      public void setTtl(TimeToLive ttl) {
         this.ttl = ttl;
      }
   }

   private SituationDataWarehouse() {
      throw new UnsupportedOperationException();
   }
}
