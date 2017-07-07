package us.dot.its.jpo.ode.wrapper;

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;

import us.dot.its.jpo.ode.util.SerializationUtils;

public class MessagingSerializer<T> implements Serializer<T> {

    SerializationUtils<T> serializer = new SerializationUtils<T>();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, T data) {
        return serializer.serialize(data);
    }

    @Override
    public void close() {
        // nothing to do
    }

}
