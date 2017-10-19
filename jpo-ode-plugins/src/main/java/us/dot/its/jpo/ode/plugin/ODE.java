package us.dot.its.jpo.ode.plugin;

import us.dot.its.jpo.ode.model.OdeObject;

public class ODE extends OdeObject {
   private static final long serialVersionUID = 664813454587275001L;

   private int version = 1;
   private int index;

   public int getVersion() {
      return version;
   }

   public void setVersion(int version) {
      this.version = version;
   }

   public int getIndex() {
      return index;
   }

   public void setIndex(int index) {
      this.index = index;
   }
   
   
}
