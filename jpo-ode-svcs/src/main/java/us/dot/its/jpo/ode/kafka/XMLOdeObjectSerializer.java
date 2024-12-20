package us.dot.its.jpo.ode.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import us.dot.its.jpo.ode.model.OdeObject;

/**
 * A serializer implementation that converts {@link OdeObject} instances into XML byte arrays using
 * the Jackson XmlMapper. This class is intended for use in scenarios where objects need to be
 * serialized into XML format for transmission or storage.
 */
public class XMLOdeObjectSerializer implements Serializer<OdeObject> {

  private final XmlMapper xmlMapper;

  public XMLOdeObjectSerializer(XmlMapper xmlMapper) {
    this.xmlMapper = xmlMapper;
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
