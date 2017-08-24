package us.dot.its.jpo.ode.wrapper.serdes;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;

import us.dot.its.jpo.ode.util.SerializationUtils;

public class MessagingDeserializer<T> implements Deserializer<T> {

    SerializationUtils<T> deserializer = new SerializationUtils<T>();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // nothing to do
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        return deserializer.deserialize(data);
    }

    @Override
    public void close() {
        // nothing to do
    }

}
