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
package us.dot.its.jpo.ode.util;

import org.json.JSONObject;
import org.json.XML;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class XmlUtils {
   public static class XmlUtilsException extends Exception {

      private static final long serialVersionUID = 1L;

      public XmlUtilsException(String string) {
         super(string);
      }

      public XmlUtilsException(String string, Exception e) {
         super(string, e);
      }

   }

   private        XmlMapper xmlMapper = new XmlMapper();
   private static XmlMapper staticXmlMapper;
   
   static {
      staticXmlMapper = new XmlMapper();
      staticXmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      staticXmlMapper.setDefaultUseWrapper(true);
   }
   
   
   public XmlUtils() {
      super();
      xmlMapper = new XmlMapper();
      xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      xmlMapper.setDefaultUseWrapper(true);
   }

   public String toXml(Object o) throws JsonProcessingException {
      String xml = xmlMapper.writeValueAsString(o);
      return xml;
   }

//   public static String toXml(Object o) throws XmlUtilsException {
//      try {
//         JSONObject root = new JSONObject();
//         JSONObject object = new JSONObject(o);
//         root.put(o.getClass().getSimpleName(), object);
//         return XML.toString(root);
//      } catch (JSONException e) {
//         throw new XmlUtilsException("Error encoding object to XML", e);
//      }
//   }

   public Object fromXml(String xml, Class<?> clazz) throws XmlUtilsException {
      try {
         return xmlMapper.readValue(xml, clazz);
      } catch (Exception e) {
         throw new XmlUtilsException("Error decoding "
               + xml + " to "
               + clazz.getName(), e);
      }
   }

   /**
    * Embeds the arrayNode into an ObjectNode with the given childKey. By default a JSON array
    * such as {"parent":[1, 2, 3,]} will be converted to:
    *     <ObjectNode><parent>1</parent><parent>2</parent><parent>3</parent></ObjectNode>.
    * This is not often desired as there is no paren object to encompass the array. By calling
    * this method given childKey = "child" and arrayNode = [1, 2, 3,], method will return 
    * {"parent":{"child":[1, 2, 3,]}} which as a result will be encoded to
    *     <ObjectNode><parent><child>1</child><child>2</child><child>3</child></parent></ObjectNode>.
    * Which is a more representative of the JSON ObjectNode.
    *  
    * @param childKey: The key to be given to the child array object
    * @param arrayNode: The array node to be embedded in a ObjectNode
    * @return OBjectNode representation of the given arrayNode redy to be converted to XML
    */
   public static ObjectNode createEmbeddedJsonArrayForXmlConversion(String childKey, ArrayNode arrayNode) {
       ObjectNode childNode = staticXmlMapper.createObjectNode();
       childNode.set(childKey, arrayNode);
       return childNode;
   }

   public static String toXmlStatic(Object o) throws XmlUtilsException {
     String xml;
     try {
        xml = staticXmlMapper.writeValueAsString(o);
     } catch (Exception e) {
        throw new XmlUtilsException("Error encoding object to XML", e);
     }
     return xml;
   }

   public static Object fromXmlS(String xml, Class<?> clazz) throws XmlUtilsException {
      try {
         return staticXmlMapper.readValue(xml, clazz);
      } catch (Exception e) {
         throw new XmlUtilsException("Error decoding "
               + xml + " to "
               + clazz.getName(), e);
      }
   }

   public static ObjectNode toObjectNode(String xml) throws XmlUtilsException {
      try {
         JSONObject jsonObject = XML.toJSONObject(xml);
         String jsonString = jsonObject.toString();
         return JsonUtils.toObjectNode(jsonString);
         
         /*
          * Due to issues with XmlMapper converting "xml arrays" to a valid DOM collection
          * we could not use it in this context. Hence the above workaround was adopted.
          * See: https://github.com/FasterXML/jackson-dataformat-xml/issues/187
          *      https://github.com/FasterXML/jackson-dataformat-xml/issues/205
          */
         //return (ObjectNode) staticXmlMapper.readTree(xml);
      } catch (Exception e) {
         throw new XmlUtilsException("Error decoding " + xml + "to ObjectNode", e);
      }
   }


   public static JSONObject toJSONObject(String xml) throws XmlUtilsException {
      try {
         return XML.toJSONObject(xml);
      } catch (Exception e) {
         throw new XmlUtilsException("Error decoding " + xml + "to JSONObject", e);
      }
   }


   public static JsonNode getJsonNode(String tree, String fieldName) throws XmlUtilsException {
      JsonNode jsonNode;
      try {
         jsonNode = staticXmlMapper.readTree(tree);
      } catch (Exception e) {
         throw new XmlUtilsException("Error getting field name "
               + fieldName + " from "
               + tree, e);
      }
      return jsonNode.get(fieldName);
   }

   public XmlMapper getXmlMapper() {
      return xmlMapper;
   }

   public static XmlMapper getStaticXmlMapper() {
      return staticXmlMapper;
   }

}

