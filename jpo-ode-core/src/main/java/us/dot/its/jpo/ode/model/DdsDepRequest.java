package us.dot.its.jpo.ode.model;


/**
 * "DEPOSIT: { \"systemDepositName\": \"%s\", \"encodeType\": \"%s\", \"encodedMsg\": \"%s\" }"
 *
 */
public class DdsDepRequest extends DdsRequest {

   private static final long serialVersionUID = 6066887685895268828L;

   private String systemDepositName;
   private String encodeType;
   private String encodedMsg;

   
   public String getSystemDepositName() {
      return systemDepositName;
   }

   public DdsDepRequest setSystemDepositName(String systemDepositName) {
      this.systemDepositName = systemDepositName;
      return this;
   }

   public String getEncodeType() {
      return encodeType;
   }

   public DdsDepRequest setEncodeType(String encodeType) {
      this.encodeType = encodeType;
      return this;
   }

   public String getEncodedMsg() {
      return encodedMsg;
   }

   public DdsDepRequest setEncodedMsg(String encodedMsg) {
      this.encodedMsg = encodedMsg;
      return this;
   }

   @Override
   public String toString() {
      return "DEPOSIT:" + this.toJson();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result
            + ((encodeType == null) ? 0 : encodeType.hashCode());
      result = prime * result
            + ((encodedMsg == null) ? 0 : encodedMsg.hashCode());
      result = prime * result
            + ((systemDepositName == null) ? 0 : systemDepositName.hashCode());
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
      DdsDepRequest other = (DdsDepRequest) obj;
      if (encodeType == null) {
         if (other.encodeType != null)
            return false;
      } else if (!encodeType.equals(other.encodeType))
         return false;
      if (encodedMsg == null) {
         if (other.encodedMsg != null)
            return false;
      } else if (!encodedMsg.equals(other.encodedMsg))
         return false;
      if (systemDepositName == null) {
         if (other.systemDepositName != null)
            return false;
      } else if (!systemDepositName.equals(other.systemDepositName))
         return false;
      return true;
   }

}
