package us.dot.its.jpo.ode.model;

import java.math.BigDecimal;

public class OdePoint extends OdeObject {

   private static final long serialVersionUID = 6030294798158488311L;

   private BigDecimal latitude;
   private BigDecimal longitude;
   public OdePoint() {
      super();
   }
   public OdePoint(BigDecimal latitude, BigDecimal longitude) {
      super();
      this.latitude = latitude;
      this.longitude = longitude;
   }
   public BigDecimal getLatitude() {
      return latitude;
   }
   public OdePoint setLatitude(BigDecimal latitude) {
      this.latitude = latitude;
      return this;
   }
   public BigDecimal getLongitude() {
      return longitude;
   }
   public OdePoint setLongitude(BigDecimal longitude) {
      this.longitude = longitude;
      return this;
   }
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((latitude == null) ? 0 : latitude.hashCode());
      result = prime * result
            + ((longitude == null) ? 0 : longitude.hashCode());
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
      OdePoint other = (OdePoint) obj;
      if (latitude == null) {
         if (other.latitude != null)
            return false;
      } else if (!latitude.equals(other.latitude))
         return false;
      if (longitude == null) {
         if (other.longitude != null)
            return false;
      } else if (!longitude.equals(other.longitude))
         return false;
      return true;
   }
   
   
}
