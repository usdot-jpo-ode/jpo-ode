package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class Node_LatLon extends Asn1Object {

   private static final long serialVersionUID = 1L;

   private String lon;

   private String lat;

   public String getLon() {
      return lon;
   }

   public void setLon(String lon) {
      this.lon = lon;
   }

   public String getLat() {
      return lat;
   }

   public void setLat(String lat) {
      this.lat = lat;
   }
}
