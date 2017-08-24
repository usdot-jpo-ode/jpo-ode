package us.dot.its.jpo.ode.plugin.j2735;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735LaneWidth extends Asn1Object {
   
   private static final long serialVersionUID = -9053953086636595479L;
   
   private BigDecimal width;
   
   public J2735LaneWidth() {
      super();
   }
   
   public J2735LaneWidth(BigDecimal width) {
      super();
      this.setWidth(width);
   }

   public BigDecimal getWidth() {
      return width;
   }

   public void setWidth(BigDecimal width) {
      this.width = width;
   }

}
