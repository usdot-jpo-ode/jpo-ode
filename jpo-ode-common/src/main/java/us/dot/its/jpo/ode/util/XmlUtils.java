package us.dot.its.jpo.ode.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
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
   }
   
   
   public XmlUtils() {
      super();
      xmlMapper = new XmlMapper();
      xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
   }

   public String toXml(Object o) throws JsonProcessingException {
      String xml = xmlMapper.writeValueAsString(o);
      return xml;
   }

   public Object fromXml(String xml, Class<?> clazz) throws XmlUtilsException {
      try {
         return xmlMapper.readValue(xml, clazz);
      } catch (Exception e) {
         throw new XmlUtilsException("Error decoding "
               + xml + " to "
               + clazz.getName(), e);
      }
   }

   public static String toXmlS(Object o) throws XmlUtilsException {
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
         return (ObjectNode) staticXmlMapper.readTree(xml);
      } catch (Exception e) {
         throw new XmlUtilsException("Error decoding "
               + xml + "to ObjectNode", e);
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

}

