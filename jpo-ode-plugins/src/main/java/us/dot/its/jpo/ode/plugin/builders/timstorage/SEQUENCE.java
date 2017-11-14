package us.dot.its.jpo.ode.plugin.builders.timstorage;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class SEQUENCE extends Asn1Object {
   private static final long serialVersionUID = 1L;
   private Item item;

   public Item getItem() {
      return item;
   }

   public void setItem(Item item) {
      this.item = item;
   }
}
