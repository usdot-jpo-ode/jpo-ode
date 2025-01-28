package us.dot.its.jpo.ode.plugin.serialization;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.IOException;
import java.util.Objects;
import us.dot.its.jpo.ode.plugin.types.Asn1Enumerated;

/**
 * Base class for ENUMERATED value deserializers to produce both XER and JER.
 *
 * @param <T> The ENUMERATED type
 * @author Ivan Yourshaw
 */
public abstract class EnumeratedDeserializer<T extends Enum<?> & Asn1Enumerated>
    extends StdDeserializer<T> {

  protected abstract T[] listEnumValues();

  protected EnumeratedDeserializer(Class<T> valueClass) {
    super(valueClass);
  }

  @Override
  public T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
      throws IOException, JacksonException {
    String name = null;
    if (jsonParser.getCodec() instanceof XmlMapper) {
      // XML
      // The enum in BASIC-XER is an empty element, so Jackson thinks it's an object
      // with a key
      // of that name with no value
      TreeNode node = jsonParser.getCodec().readTree(jsonParser);
      var iterator = node.fieldNames();
      if (iterator.hasNext()) {
        name = node.fieldNames().next();
      }
    } else {
      // JSON
      // Behaves normally: The enum name is the text
      name = jsonParser.getText();
    }
    for (T enumValue : listEnumValues()) {
      if (Objects.equals(enumValue.getName(), name)) {
        return enumValue;
      }
    }

    return null;
  }
}
