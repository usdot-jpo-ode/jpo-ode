package us.dot.its.jpo.ode.plugin.j2735.builders.timstorage;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class Offset extends Asn1Object {
   private static final long serialVersionUID = 1L;
   private Ll ll;
   private Xy xy;

   public Ll getLl() {
      return ll;
   }

   public void setLl(Ll ll) {
      this.ll = ll;
   }

   public Xy getXy() {
      return xy;
   }

   public void setXy(Xy xy) {
      this.xy = xy;
   }
}
