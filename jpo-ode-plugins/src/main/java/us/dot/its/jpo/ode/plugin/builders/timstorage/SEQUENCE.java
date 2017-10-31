package us.dot.its.jpo.ode.plugin.builders.timstorage;

import us.dot.its.jpo.ode.model.OdeObject;

public class SEQUENCE extends OdeObject {
   private static final long serialVersionUID = 1L;
   private Item item;

   public Item getItem() {
      return item;
   }

   public void setItem(Item item) {
      this.item = item;
   }
}
