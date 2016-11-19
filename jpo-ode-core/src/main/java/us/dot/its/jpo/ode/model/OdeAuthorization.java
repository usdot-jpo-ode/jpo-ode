package us.dot.its.jpo.ode.model;

public class OdeAuthorization extends OdeMsgPayload{

   private static final long serialVersionUID = -8496344900381434672L;

   private String token;
   
   public String getToken() {
      return token;
   }

   public OdeAuthorization setToken(String token) {
      this.token = token;
      return this;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((token == null) ? 0 : token.hashCode());
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
      OdeAuthorization other = (OdeAuthorization) obj;
      if (token == null) {
         if (other.token != null)
            return false;
      } else if (!token.equals(other.token))
         return false;
      return true;
   }
   

}
