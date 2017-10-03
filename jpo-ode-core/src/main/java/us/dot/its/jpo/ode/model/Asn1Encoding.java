package us.dot.its.jpo.ode.model;

public class Asn1Encoding extends OdeObject {

   public enum EncodingRule {UPER, COER}
   
   private static final long serialVersionUID = 411367830731507211L;
   
   private String elementName;
   private String elementType;
   private EncodingRule encodingRule;
   
   
   public Asn1Encoding() {
      super();
   }
   
   
   public Asn1Encoding(String elementName, String elementType, EncodingRule encodingRule) {
      super();
      this.elementName = elementName;
      this.elementType = elementType;
      this.encodingRule = encodingRule;
   }


   public String getElementName() {
      return elementName;
   }
   public void setElementName(String elementName) {
      this.elementName = elementName;
   }
   public String getElementType() {
      return elementType;
   }
   public void setElementType(String elementType) {
      this.elementType = elementType;
   }
   public EncodingRule getEncodingRule() {
      return encodingRule;
   }
   public void setEncodingRule(EncodingRule encodingRule) {
      this.encodingRule = encodingRule;
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((elementName == null) ? 0 : elementName.hashCode());
      result = prime * result + ((elementType == null) ? 0 : elementType.hashCode());
      result = prime * result + ((encodingRule == null) ? 0 : encodingRule.hashCode());
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
      Asn1Encoding other = (Asn1Encoding) obj;
      if (elementName == null) {
         if (other.elementName != null)
            return false;
      } else if (!elementName.equals(other.elementName))
         return false;
      if (elementType == null) {
         if (other.elementType != null)
            return false;
      } else if (!elementType.equals(other.elementType))
         return false;
      if (encodingRule != other.encodingRule)
         return false;
      return true;
   }
   
   

}
