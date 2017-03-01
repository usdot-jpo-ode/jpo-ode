package us.dot.its.jpo.ode.plugin.j2735;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735AccelerationSet4Way extends Asn1Object {
   private static final long serialVersionUID = 1L;

   private BigDecimal accelLat;
   private BigDecimal accelLong;
   private BigDecimal accelVert;
   private BigDecimal accelYaw;

   public BigDecimal getAccelLat() {
      return accelLat;
   }

   public void setAccelLat(BigDecimal accelLat) {
      this.accelLat = accelLat;
   }

   public BigDecimal getAccelLong() {
      return accelLong;
   }

   public void setAccelLong(BigDecimal accelLong) {
      this.accelLong = accelLong;
   }

   public BigDecimal getAccelVert() {
      return accelVert;
   }

   public void setAccelVert(BigDecimal accelVert) {
      this.accelVert = accelVert;
   }

   public BigDecimal getAccelYaw() {
      return accelYaw;
   }

   public void setAccelYaw(BigDecimal accelYaw) {
      this.accelYaw = accelYaw;
   }

}
