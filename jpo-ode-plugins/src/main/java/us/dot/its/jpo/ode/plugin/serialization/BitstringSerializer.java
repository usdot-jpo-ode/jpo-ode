package us.dot.its.jpo.ode.plugin.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.ser.XmlSerializerProvider;
import java.io.IOException;
import us.dot.its.jpo.ode.plugin.types.Asn1Bitstring;

/**
 * Serializer for ASN.1 Bitstring types to XER or JER
 *
 * <p>Note that this serializer writes ODE JSON, not standard JER.
 *
 * @author Ivan Yourshaw
 */
public class BitstringSerializer extends StdSerializer<Asn1Bitstring> {

  protected BitstringSerializer() {
    super(Asn1Bitstring.class);
  }

  @Override
  public void serialize(Asn1Bitstring asn1Bitstring, JsonGenerator jsonGenerator, 
      SerializerProvider serializerProvider) throws IOException {
    if (serializerProvider instanceof XmlSerializerProvider) {
      // XER serializes bitstrings as binary strings
      jsonGenerator.writeString(asn1Bitstring.binaryString());
    } else {
      // ODE JSON dialect serializes bitstrings as verbose maps
      jsonGenerator.writeStartObject();
      for (int i = 0; i < asn1Bitstring.size(); i++) {
        String name = asn1Bitstring.name(i);
        boolean isSet = asn1Bitstring.get(i);
        jsonGenerator.writeBooleanField(name, isSet);
      }
      jsonGenerator.writeEndObject();
    }
  }
}
