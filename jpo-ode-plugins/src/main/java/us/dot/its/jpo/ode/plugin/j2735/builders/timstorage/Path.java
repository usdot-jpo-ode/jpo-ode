package us.dot.its.jpo.ode.plugin.j2735.builders.timstorage;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class Path extends Asn1Object {
   private static final long serialVersionUID = 1L;

   private String scale;

   private Offset offset;

   public String getScale() {
      return scale;
   }

   public void setScale(String scale) {
      this.scale = scale;
   }

   public Offset getOffset() {
      return offset;
   }

   public void setOffset(Offset offset) {
      this.offset = offset;
   }
}
