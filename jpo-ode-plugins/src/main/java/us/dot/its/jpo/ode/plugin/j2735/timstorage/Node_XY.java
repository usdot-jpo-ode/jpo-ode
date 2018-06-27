package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class Node_XY extends Asn1Object {
   private static final long serialVersionUID = 1L;

   private String x;

   private String y;

   public String getX() {
      return x;
   }

   public void setX(String x) {
      this.x = x;
   }

   public String getY() {
      return y;
   }

   public void setY(String y) {
      this.y = y;
   }

}
