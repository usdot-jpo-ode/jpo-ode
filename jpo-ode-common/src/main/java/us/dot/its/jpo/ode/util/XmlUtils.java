package us.dot.its.jpo.ode.util;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class XmlUtils<T> {
   private JAXBContext jc;
   private Marshaller marshaller;
   private Unmarshaller unmarshaller;
   
   public XmlUtils(Class<T> clazz) throws JAXBException {
      jc = JAXBContext.newInstance(clazz);
      marshaller = jc.createMarshaller();
      unmarshaller = jc.createUnmarshaller();
   }
   
   public String toXml(T o) throws JAXBException {
      StringWriter writer = new StringWriter();
      
      marshaller.marshal(o, writer);
      
      return writer.toString();
   }

   @SuppressWarnings("unchecked")
   public T fromXml(String xml) throws JAXBException {
      StringReader reader = new StringReader(xml);
      
      return (T)unmarshaller.unmarshal(reader);
   }
}
