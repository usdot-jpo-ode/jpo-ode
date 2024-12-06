package us.dot.its.jpo.ode.plugin.serialization;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.IOException;
import us.dot.its.jpo.ode.plugin.types.Asn1Boolean;

/**
 * Deserializer for ASN.1 Boolean types to XER or JER
 *
 * @author Ivan Yourshaw
 */
@SuppressWarnings({ "unchecked" })
public class BooleanDeserializer<T extends Asn1Boolean> extends StdDeserializer<T> {

  protected Asn1Boolean construct() {
    return new Asn1Boolean();
  }

  public BooleanDeserializer() {
    super(Asn1Boolean.class);
  }

  protected BooleanDeserializer(Class<T> valueType) {
    super(Asn1Boolean.class);
  }

  @Override
  public T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
      throws IOException, JacksonException {
    Asn1Boolean result = construct();
    if (jsonParser.getCodec() instanceof XmlMapper) {
      // XML: unwrap empty element
      TreeNode node = jsonParser.getCodec().readTree(jsonParser);
      var iterator = node.fieldNames();
      if (iterator.hasNext()) {
        String str = node.fieldNames().next();
        result.setValue(Boolean.parseBoolean(str));
      }
    } else {
      // JSON
      result.setValue(jsonParser.getBooleanValue());
    }
    return (T) result;
  }
}
