package us.dot.its.jpo.ode.plugin;

import us.dot.its.jpo.ode.model.OdeObject;

public class ODE extends OdeObject {
   private static final long serialVersionUID = 664813454587275001L;

   private int version = 1;

   public int getVersion() {
      return version;
   }

   public void setVersion(int version) {
      this.version = version;
   }
}
