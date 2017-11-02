package us.dot.its.jpo.ode.model;

import us.dot.its.jpo.ode.plugin.j2735.DsrcPosition3D;

public class OdeLogMsgMetadataLocation extends OdeObject {

   private static final long serialVersionUID = 3174683494132372801L;

   String latitude;
   String longitude;
   String elevation;
   String speed;
   String heading;

   public OdeLogMsgMetadataLocation() {
      super();
   }

   public OdeLogMsgMetadataLocation(DsrcPosition3D position, String speed, String heading) {
      super();
      this.latitude = position.getLatitude().toString();
      this.longitude = position.getLongitude().toString();
      this.elevation = position.getElevation().toString();
      this.speed = speed;
      this.heading = heading;
   }

   public String getLatitude() {
      return latitude;
   }

   public void setLatitude(String latitude) {
      this.latitude = latitude;
   }

   public String getLongitude() {
      return longitude;
   }

   public void setLongitude(String longitude) {
      this.longitude = longitude;
   }

   public String getElevation() {
      return elevation;
   }

   public void setElevation(String elevation) {
      this.elevation = elevation;
   }

   public String getSpeed() {
      return speed;
   }

   public void setSpeed(String speed) {
      this.speed = speed;
   }

   public String getHeading() {
      return heading;
   }

   public void setHeading(String heading) {
      this.heading = heading;
   }

}
