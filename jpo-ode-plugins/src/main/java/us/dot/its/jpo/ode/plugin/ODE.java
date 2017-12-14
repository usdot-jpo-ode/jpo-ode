package us.dot.its.jpo.ode.plugin;

import us.dot.its.jpo.ode.model.OdeObject;

public class ODE extends OdeObject {
   private static final long serialVersionUID = 664813454587275001L;
   
   public static final int POST = 0;
   public static final int PUT = 1;

   private int version = 2;
   private int index;
   private int verb;

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
   
   public int getVerb() {
      return verb;
   }

   public void setVerb(int verb) {
      this.verb = verb;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + index;
      result = prime * result + verb;
      result = prime * result + version;
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      ODE other = (ODE) obj;
      if (index != other.index)
         return false;
      if (verb != other.verb)
         return false;
      if (version != other.version)
         return false;
      return true;
   }
}
