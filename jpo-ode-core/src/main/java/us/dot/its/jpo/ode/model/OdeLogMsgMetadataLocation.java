package us.dot.its.jpo.ode.model;

public class OdeLogMsgMetadataLocation extends OdeObject {

   private static final long serialVersionUID = 3174683494132372801L;

   String latitude;
   String longitude;
   String elevation;
   String speed;
   String heading;

   public OdeLogMsgMetadataLocation(String latitude, String longitude, String elevation, String speed, String heading) {
      super();
      this.latitude = latitude;
      this.longitude = longitude;
      this.elevation = elevation;
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
