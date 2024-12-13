package us.dot.its.jpo.ode.plugin.serialization;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;
import java.io.IOException;
import java.util.Objects;
import us.dot.its.jpo.ode.plugin.types.Asn1Enumerated;
import us.dot.its.jpo.ode.plugin.types.Asn1SequenceOf;

/**
 * Base class for deserializers for SEQUENCE-OF ENUMERATED types.
 *
 * @param <S> The Sequence Of ENUMERATED type.
 * @author Ivan Yourshaw
 */
public abstract class SequenceOfEnumeratedDeserializer<S extends Enum<?> & Asn1Enumerated, 
    T extends Asn1SequenceOf<S>> extends StdDeserializer<T> {

  protected final Class<T> thisClass;
  protected final Class<S> enumClass;

  protected abstract S[] listEnumValues();

  protected abstract T construct();

  protected SequenceOfEnumeratedDeserializer(Class<T> sequenceOfEnumType, Class<S> enumType) {
    super(sequenceOfEnumType);
    this.thisClass = sequenceOfEnumType;
    this.enumClass = enumType;
  }

  @Override
  public T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
      throws IOException, JacksonException {
    T result = null;
    if (jsonParser instanceof FromXmlParser xmlParser) {
      // Unwrapped enum items
      result = construct();
      TreeNode node = xmlParser.getCodec().readTree(xmlParser);

      var fieldNameIterator = node.fieldNames();
      while (fieldNameIterator.hasNext()) {
        String name = fieldNameIterator.next();

        for (S enumValue : listEnumValues()) {
          if (Objects.equals(enumValue.getName(), name)) {
            result.add(enumValue);
          }
        }
      }
    } else {
      result = jsonParser.getCodec().readValue(jsonParser, thisClass);
    }
    return result;
  }
}
