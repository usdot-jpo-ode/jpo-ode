package us.dot.its.jpo.ode.plugin.serialization;

import us.dot.its.jpo.ode.plugin.types.Asn1Integer;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;

import java.io.IOException;

public abstract class IntegerDeserializer<T extends Asn1Integer> extends StdDeserializer<T> {

    protected final Class<T> thisClass;
    protected abstract T construct();

    protected IntegerDeserializer(Class<T> vc) {
        super(vc);
        this.thisClass = vc;
    }

    @Override
    public T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        T result = null;
        if (jsonParser instanceof FromXmlParser xmlParser) {
            result = construct();
            TreeNode node = xmlParser.getCodec().readTree(xmlParser);
            System.out.printf("type: %s%n", thisClass.getName());
            System.out.printf("node: %s%n", node);
            System.out.printf("node type: %s%n", node.getClass().getName());
            if (node instanceof NumericNode numNode) {
                result.setValue(numNode.longValue());
            } else if (node instanceof TextNode textNode) {
                // Sometimes happens, since XML values are ambiguous between text and numbers
                String textValue = textNode.textValue();
                long value = Long.parseLong(textValue);
                result.setValue(value);
            }
        } else {
            result = jsonParser.getCodec().readValue(jsonParser, thisClass);
        }
        return result;
    }
}
