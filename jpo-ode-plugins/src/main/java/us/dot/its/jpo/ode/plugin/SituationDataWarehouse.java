package us.dot.its.jpo.ode.plugin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.plugin.j2735.J2735GeoRegion;

public class SituationDataWarehouse {
   public static class SDW extends OdeObject {

      private static final long serialVersionUID = -7731139391317960325L;

      public enum TimeToLive {
         @JsonProperty("oneminute")
         @SerializedName("oneminute")
         ONEMINUTE, 
         @JsonProperty("thirtyminutes")
         @SerializedName("thirtyminutes")
         THIRTYMINUTES, 
         @JsonProperty("oneday")
         @SerializedName("oneday")
         ONEDAY, 
         @JsonProperty("oneweek")
         @SerializedName("oneweek")
         ONEWEEK, 
         @JsonProperty("onemonth")
         @SerializedName("onemonth")
         ONEMONTH, 
         @JsonProperty("oneyear")
         @SerializedName("oneyear")
         ONEYEAR
      }

      private J2735GeoRegion serviceRegion;
      private TimeToLive ttl = null;

      public J2735GeoRegion getServiceRegion() {
         return serviceRegion;
      }

      public void setServiceRegion(J2735GeoRegion serviceRegion) {
         this.serviceRegion = serviceRegion;
      }

      public TimeToLive getTtl() {
         if (ttl == null) return TimeToLive.THIRTYMINUTES;
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
