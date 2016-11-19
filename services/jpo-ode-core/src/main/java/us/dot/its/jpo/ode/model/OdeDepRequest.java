package us.dot.its.jpo.ode.model;

public class OdeDepRequest extends OdeRequest {

   private static final long serialVersionUID = -6766743372518752149L;
   
   private String encodeType;
   private String data;

   
   public String getEncodeType() {
      return encodeType;
   }

   public void setEncodeType(String encodeType) {
      this.encodeType = encodeType;
   }

   public String getData() {
      return data;
   }

   public void setData(String data) {
      this.data = data;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((data == null) ? 0 : data.hashCode());
      result = prime * result
            + ((encodeType == null) ? 0 : encodeType.hashCode());
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
      OdeDepRequest other = (OdeDepRequest) obj;
      if (data == null) {
         if (other.data != null)
            return false;
      } else if (!data.equals(other.data))
         return false;
      if (encodeType == null) {
         if (other.encodeType != null)
            return false;
      } else if (!encodeType.equals(other.encodeType))
         return false;
      return true;
   }

   
}
