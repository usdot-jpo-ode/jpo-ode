package us.dot.its.jpo.ode.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class XmlUtils {
   private        XmlMapper xmlMapper = new XmlMapper();
   private static XmlMapper staticXmlMapper = new XmlMapper();
   
   public String toXml(Object o) throws JsonProcessingException {
      String xml = xmlMapper.writeValueAsString(o);
      return xml;
   }

   public Object fromXml(String xml, Class<?> clazz) throws JsonParseException, JsonMappingException, IOException {
      return xmlMapper.readValue(xml, clazz);
   }

   public static String toXmlS(Object o) throws JsonProcessingException {
      String xml = staticXmlMapper.writeValueAsString(o);
      return xml;
   }

   public Object fromXmlS(String xml, Class<?> clazz) throws JsonParseException, JsonMappingException, IOException {
      return staticXmlMapper.readValue(xml, clazz);
   }
}

