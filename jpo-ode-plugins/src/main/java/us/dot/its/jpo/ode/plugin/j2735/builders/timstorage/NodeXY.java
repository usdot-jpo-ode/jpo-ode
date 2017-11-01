package us.dot.its.jpo.ode.plugin.j2735.builders.timstorage;

import us.dot.its.jpo.ode.model.OdeObject;

public class NodeXY extends OdeObject {
   private static final long serialVersionUID = 1L;
   private Delta delta;

   public Delta getDelta() {
      return delta;
   }

   public void setDelta(Delta delta) {
      this.delta = delta;
   }
}
