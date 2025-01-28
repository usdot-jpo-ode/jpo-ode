package us.dot.its.jpo.ode.plugin.serialization;

import static us.dot.its.jpo.ode.plugin.utils.XmlUtils.*;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;
import java.io.IOException;
import lombok.SneakyThrows;
import us.dot.its.jpo.ode.plugin.types.Asn1Choice;
import us.dot.its.jpo.ode.plugin.types.Asn1SequenceOf;

/**
 * Deserializer for SEQUENCE-OF CHOICE types.
 * These are unwrapped in XER, but wrapped in JER.
 * @param <S> The Asn1Choice type
 * @param <T> The Asn1SequenceOf type
 */
public abstract class SequenceOfChoiceDeserializer<S extends Asn1Choice, T extends Asn1SequenceOf<S>>
    extends StdDeserializer<T> {

    protected final Class<S> choiceClass;
    protected final Class<T> sequenceOfClass;

    protected abstract T construct();

    protected SequenceOfChoiceDeserializer(Class<S> choiceClass, Class<T> sequenceOfClass) {
        super(sequenceOfClass);
        this.choiceClass = choiceClass;
        this.sequenceOfClass = sequenceOfClass;
    }

    @SneakyThrows
    @Override
    public T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JacksonException {
        T result = construct();
        if (jsonParser instanceof FromXmlParser xmlParser) {
            // XML: expects unwrapped choice items
            // unwrap and deserialize each choice item
            XmlMapper xmlMapper = (XmlMapper)xmlParser.getCodec();
            TreeNode node = xmlMapper.readTree(xmlParser);
            String xml = xmlMapper.writeValueAsString(node);
            var tokens = tokenize(xml);
            var unwrapped = unwrap(tokens);
            var grouped = groupTopLevelTokens(unwrapped);
            for (var group : grouped) {
                var wrappedGroup = wrap(group, choiceClass.getSimpleName());
                S choice = xmlMapper.readValue(stringifyTokens(wrappedGroup), choiceClass);
                result.add(choice);
            }
        } else {
            // JSON: expects wrapped choice items, pass through as normal
            result = jsonParser.getCodec().readValue(jsonParser, sequenceOfClass);
        }
        return result;
    }


}
