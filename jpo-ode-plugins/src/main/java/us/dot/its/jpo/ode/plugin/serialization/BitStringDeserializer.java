package us.dot.its.jpo.ode.plugin.serialization;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.IOException;
import java.util.Map;
import us.dot.its.jpo.ode.plugin.types.Asn1Bitstring;

/**
 * Deserialize an ASN.1 Bitstring from XER or JER.
 * 
 * <p>Note that this deserializer expects ODE JSON, not standard JER.
 *
 * @param <T> The bitstring type.
 * @author Ivan Yourshaw
 */
public abstract class BitStringDeserializer<T extends Asn1Bitstring> extends StdDeserializer<T> {

  protected abstract T construct();

  protected BitStringDeserializer(Class<?> valueClass) {
    super(valueClass);
  }

  @Override
  public T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
      throws IOException, JacksonException {
    T bitstring = construct();
    if (jsonParser.getCodec() instanceof XmlMapper) {
      // XML: binary
      String str = jsonParser.getText();
      bitstring.fromBinaryString(str);
    } else {
      // ODE JSON dialect: read verbose map
      TypeReference<Map<String, Boolean>> boolMapType = new TypeReference<>() {
      };
      Map<String, Boolean> map = jsonParser.readValueAs(boolMapType);
      for (var keyValue : map.entrySet()) {
        bitstring.set(keyValue.getKey(), keyValue.getValue());
      }
    }
    return bitstring;
  }

}
