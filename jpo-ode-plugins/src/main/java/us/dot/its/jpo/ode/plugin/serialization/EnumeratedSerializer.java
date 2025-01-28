package us.dot.its.jpo.ode.plugin.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.ser.XmlSerializerProvider;
import java.io.IOException;
import us.dot.its.jpo.ode.plugin.types.Asn1Enumerated;

/**
 * Base class for ENUMERATED value serializers to produce both XER and JER.
 *
 * @param <T> The ENUMERATED type
 * @author Ivan Yourshaw
 */
@SuppressWarnings({ "rawtypes" })
public class EnumeratedSerializer<T extends Enum & Asn1Enumerated> extends StdSerializer<T> {

  protected EnumeratedSerializer(Class<T> t) {
    super(t);
  }

  @Override
  public void serialize(T t, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) 
      throws IOException {
    if (serializerProvider instanceof XmlSerializerProvider) {
      jsonGenerator.writeStartObject();

      //
      // BASIC-XER's weird way of writing enums.
      //
      // Ref. ITU-T X.693 (02/2021) Sec. 8.3.7 which says:
      //
      // The "XMLEnumeratedValue" specified in Rec. ITU-T X.680 | ISO/IEC 8824-1,
      // 20.8, shall only be
      // "EmptyElementEnumerated"
      //
      // and ITU-T X.680 (02/2021) Sec. 20.8 which says:
      //
      // EmptyElementEnumerated ::= "<" & identifier "/>"
      //
      jsonGenerator.writeRaw(String.format("<%s/>", t.getName()));

      jsonGenerator.writeEndObject();
    } else {
      //
      // JER: Just write the enum value as a string like a normal person.
      // Does not handle TEXT encoding instructions per X.697 sec 22.2
      //
      jsonGenerator.writeString(t.getName());
    }
  }
}