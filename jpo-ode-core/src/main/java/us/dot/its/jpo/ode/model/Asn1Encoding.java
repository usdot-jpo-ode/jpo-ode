/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.ode.model;

import com.fasterxml.jackson.databind.JsonNode;

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

   public Asn1Encoding (JsonNode encoding) {
      this(encoding.get("elementName").asText(), encoding.get("elementType").asText(), EncodingRule.valueOf(encoding.get("encodingRule").asText()));
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
