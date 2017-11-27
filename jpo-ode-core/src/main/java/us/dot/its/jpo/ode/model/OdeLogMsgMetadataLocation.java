package us.dot.its.jpo.ode.model;

import java.math.BigDecimal;

public class OdeLogMsgMetadataLocation extends OdeObject {

   private static final long serialVersionUID = 3174683494132372801L;

   BigDecimal latitude;
   BigDecimal longitude;
   BigDecimal elevation;
   BigDecimal speed;
   BigDecimal heading;

   public OdeLogMsgMetadataLocation() {
      super();
   }

   public OdeLogMsgMetadataLocation(BigDecimal latitude, BigDecimal longitude, BigDecimal elevation, BigDecimal speed, BigDecimal heading) {
      super();
      this.latitude = latitude;
      this.longitude = longitude;
      this.elevation = elevation;
      this.speed = speed;
      this.heading = heading;
   }

   public BigDecimal getLatitude() {
      return latitude;
   }

   public void setLatitude(BigDecimal latitude) {
      this.latitude = latitude;
   }

   public BigDecimal getLongitude() {
      return longitude;
   }

   public void setLongitude(BigDecimal longitude) {
      this.longitude = longitude;
   }

   public BigDecimal getElevation() {
      return elevation;
   }

   public void setElevation(BigDecimal elevation) {
      this.elevation = elevation;
   }

   public BigDecimal getSpeed() {
      return speed;
   }

   public void setSpeed(BigDecimal speed) {
      this.speed = speed;
   }

   public BigDecimal getHeading() {
      return heading;
   }

   public void setHeading(BigDecimal heading) {
      this.heading = heading;
   }

}
