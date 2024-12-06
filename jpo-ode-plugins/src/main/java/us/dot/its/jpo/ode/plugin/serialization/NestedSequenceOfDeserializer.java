package us.dot.its.jpo.ode.plugin.serialization;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;
import java.io.IOException;
import us.dot.its.jpo.ode.plugin.types.Asn1SequenceOf;

/**
 * Deserializer for nested, anonymous SEQUENCE-OF types. Handles XER's way of
 * wrapping these.
 *
 * @param <T> The Sequence Of type
 * @author Ivan Yourshaw
 */
public class NestedSequenceOfDeserializer<T extends Asn1SequenceOf<?>> extends StdDeserializer<T> {

  protected final Class<T> thisClass;
  protected final String wrapped;

  protected NestedSequenceOfDeserializer(Class<T> vc, String wrapped) {
    super(vc);
    this.thisClass = vc;
    this.wrapped = wrapped;
  }

  @Override
  public T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
      throws IOException, JacksonException {
    T result = null;
    if (jsonParser instanceof FromXmlParser xmlParser) {
      // For XML, we need to remove the wrapper and distinguish between single items
      // and arrays
      XmlMapper xmlMapper = (XmlMapper) xmlParser.getCodec();
      TreeNode node = xmlParser.getCodec().readTree(xmlParser);

      if (node instanceof ObjectNode objectNode) {
        JsonNode unwrapped = objectNode.findValue(wrapped);
        if (unwrapped instanceof ObjectNode unwrappedObject) {

          // Single item not identified as array, so put it in an array
          ArrayNode arrayNode = xmlMapper.createArrayNode();
          arrayNode.add(unwrappedObject);
          result = xmlMapper.convertValue(arrayNode, thisClass);

        } else if (unwrapped instanceof ArrayNode arrayNode) {

          result = xmlMapper.convertValue(arrayNode, thisClass);
        }
      }
    } else {
      result = jsonParser.getCodec().readValue(jsonParser, thisClass);
    }
    return result;
  }
}
