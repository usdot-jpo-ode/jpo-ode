package us.dot.its.jpo.ode.plugin.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fasterxml.jackson.dataformat.xml.ser.XmlSerializerProvider;
import java.io.IOException;
import javax.xml.namespace.QName;
import us.dot.its.jpo.ode.plugin.types.Asn1SequenceOf;

/**
 * Serializer for nested, anonymous SEQUENCT-OF types.
 *
 * @param <T> The Sequence-of type
 * @author Ivan Yourshaw
 */
public class NestedSequenceOfSerializer<T extends Asn1SequenceOf<?>> extends StdSerializer<T> {

  protected final QName wrapped;

  protected NestedSequenceOfSerializer(Class<T> t, String wrapped) {
    super(t);
    this.wrapped = new QName(wrapped);
  }

  @Override
  public void serialize(T t, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) 
      throws IOException {
    if (serializerProvider instanceof XmlSerializerProvider) {
      // Wrapped XER
      var xmlGen = (ToXmlGenerator) jsonGenerator;
      for (var item : t) {

        xmlGen.writeRaw(String.format("<%s>", wrapped));
        var mapper = SerializationUtil.xmlMapper();
        String itemXml = mapper.writeValueAsString(item);

        // Horrible hack to write the item value without being wrapped by the class
        // name.
        // Probably a better way exists, but this works.
        String itemClassName = item.getClass().getSimpleName();
        String startElement = String.format("<%s>", itemClassName);
        String endElement = String.format("</%s>", itemClassName);
        String strippedXml = itemXml.replace(startElement, "").replace(endElement, "");

        xmlGen.writeRaw(strippedXml);

        xmlGen.writeRaw(String.format("</%s>", wrapped));
      }

    } else {
      // Pass through JER
      jsonGenerator.writeObject(t);
    }
  }
}
