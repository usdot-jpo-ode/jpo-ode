package us.dot.its.jpo.ode.model;


public class DdsSubRequest extends DdsRequest {
   private static final long serialVersionUID = -3817847278248921651L;

   private final int vsmType = 31;
   private String systemSubName;

   public int getVsmType() {
      return vsmType;
   }

   public String getSystemSubName() {
      return systemSubName;
   }

   public DdsSubRequest setSystemSubName(String systemSubName) {
      this.systemSubName = systemSubName;
      return this;
   }

   @Override
   public String toString() {
      return "SUBSCRIBE:" + this.toJson(false);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result
            + ((systemSubName == null) ? 0 : systemSubName.hashCode());
      result = prime * result + vsmType;
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      DdsSubRequest other = (DdsSubRequest) obj;
      if (systemSubName == null) {
         if (other.systemSubName != null)
            return false;
      } else if (!systemSubName.equals(other.systemSubName))
         return false;
      if (vsmType != other.vsmType)
         return false;
      return true;
   }

}
