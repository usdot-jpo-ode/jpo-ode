package us.dot.its.jpo.ode.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import us.dot.its.jpo.ode.model.OdeObject;

public class XMLOdeObjectSerializer implements Serializer<OdeObject> {

    private final XmlMapper xmlMapper;

    public XMLOdeObjectSerializer() {
        var builder = new XmlMapper.Builder(new XmlMapper());
        builder.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        builder.defaultUseWrapper(true);
        xmlMapper = builder.build();
    }

    @Override
    public byte[] serialize(String s, OdeObject data) {
        try {
            if (data == null) {
                return new byte[0];
            } else {
                return xmlMapper.writeValueAsBytes(data);
            }
        } catch (JsonProcessingException e) {
            throw new SerializationException("Error when serializing object to XML byte[]", e);
        }
    }
}
