package us.dot.its.jpo.ode.model;

public class OdeMessage extends OdeObject {

   private static final long serialVersionUID = 6381260328835278701L;

   private Integer version;

   public OdeMessage() {
      super();
      this.version = 1;
   }

   public Integer getVersion() {
      return version;
   }

   public OdeMessage setVersion(Integer v) {
      this.version = v;
      return this;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((version == null) ? 0 : version.hashCode());
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
      OdeMessage other = (OdeMessage) obj;
      if (version == null) {
         if (other.version != null)
            return false;
      } else if (!version.equals(other.version))
         return false;
      return true;
   }

   
}
