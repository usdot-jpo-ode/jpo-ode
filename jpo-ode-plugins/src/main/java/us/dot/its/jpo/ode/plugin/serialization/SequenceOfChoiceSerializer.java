package us.dot.its.jpo.ode.plugin.serialization;

import static us.dot.its.jpo.ode.plugin.utils.XmlUtils.*;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fasterxml.jackson.dataformat.xml.ser.XmlSerializerProvider;
import java.io.IOException;
import lombok.SneakyThrows;
import us.dot.its.jpo.ode.plugin.types.Asn1Choice;
import us.dot.its.jpo.ode.plugin.types.Asn1SequenceOf;

/**
 * Serializer for SEQUENCE-OF CHOICE types.
 * These are unwrapped in XER, but wrapped in JER.
 * @param <S> The Asn1Choice type
 * @param <T> The Asn1SequenceOf type
 */
public class SequenceOfChoiceSerializer<S extends Asn1Choice, T extends Asn1SequenceOf<S>>
    extends StdSerializer<T> {

    protected final Class<S> choiceClass;
    protected final Class<T> sequenceOfClass;

    protected SequenceOfChoiceSerializer(Class<S> choiceClass, Class<T> sequenceOfClass) {
        super(sequenceOfClass);
        this.choiceClass = choiceClass;
        this.sequenceOfClass = sequenceOfClass;
    }

    @SneakyThrows
    @Override
    public void serialize(T sequenceOf, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (serializerProvider instanceof XmlSerializerProvider xmlProvider) {
            // XER: Choice items not wrapped
            var xmlGen = (ToXmlGenerator)jsonGenerator;
            var mapper = SerializationUtil.xmlMapper();

            for (var choiceItem : sequenceOf) {
                String choiceXml = mapper.writeValueAsString(choiceItem);
                String unwrappedXml = stringifyTokens(unwrap(tokenize(choiceXml)));
                xmlGen.writeRaw(unwrappedXml);
            }

        } else {
            // JER: Normal, choice items are wrapped
            jsonGenerator.writeObject(sequenceOf);
        }
    }


}
