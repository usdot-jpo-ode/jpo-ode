package us.dot.its.jpo.ode.plugin.serialization;

import static us.dot.its.jpo.ode.plugin.annotations.Asn1ParameterizedTypes.IdType.INTEGER;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import us.dot.its.jpo.ode.plugin.annotations.Asn1ParameterizedTypes;
import us.dot.its.jpo.ode.plugin.types.Asn1Sequence;

/**
 * Deserialize a parameterized SEQUENCE type.
 * Determines the subtype to deserialize to using the
 * {@link Asn1ParameterizedTypes} annotation that
 * must be present.
 *
 * @param <T> The Sequence Type
 *
 * @author Ivan Yourshaw
 */
@SuppressWarnings({ "unchecked" })
public abstract class ParameterizedTypeDeserializer<T extends Asn1Sequence> 
    extends StdDeserializer<T> {

  protected final Class<T> thisClass;

  protected ParameterizedTypeDeserializer(Class<T> vc) {
    super(vc);
    thisClass = vc;
  }

  @Override
  public T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
      throws IOException, JacksonException {
    final var typeAnnot = thisClass.getAnnotation(Asn1ParameterizedTypes.class);
    if (typeAnnot == null) {
      throw new RuntimeException("Missing Asn1ParameterizedTypes annotation.");
    }
    final String idPropName = typeAnnot.idProperty();
    final Asn1ParameterizedTypes.IdType idType = typeAnnot.idType();
    final Asn1ParameterizedTypes.Type[] types = typeAnnot.value();
    if (types == null || types.length == 0) {
      throw new RuntimeException("No Types are defined in the Asn1ParameterizedTypes annotation.");
    }
    TreeNode node = jsonParser.getCodec().readTree(jsonParser);
    if (node instanceof ObjectNode objectNode) {
      JsonNode idPropNode = objectNode.findValue(idPropName);
      final Object id = (idType == INTEGER) ? idPropNode.asInt() : idPropNode.asText();
      Class<?> subType = getSubtypeForId(id, idType, types);
      return (T) jsonParser.getCodec().readValue(jsonParser, subType);
    } else {
      throw new RuntimeException("Not instance of object");
    }
  }

  private Class<?> getSubtypeForId(final Object id, Asn1ParameterizedTypes.IdType idType,
      Asn1ParameterizedTypes.Type[] types) {
    for (var theType : types) {
      Object idValue = (idType == INTEGER) ? theType.intId() : theType.stringId();
      if (id.equals(idValue)) {
        return theType.value();
      }
    }
    throw new RuntimeException(String.format("Id %s not found in list of types", id));
  }
}
