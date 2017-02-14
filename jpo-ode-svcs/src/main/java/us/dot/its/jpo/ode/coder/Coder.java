package us.dot.its.jpo.ode.coder;

import java.io.IOException;
import java.io.InputStream;

public interface Coder {

    public void decodeFromHexAndPublish(InputStream is, String topic) throws IOException;

    public void decodeFromStreamAndPublish(InputStream is, String topic) throws IOException;

    public void publish(String topic, String msg);

    public void publish(String topic, byte[] msg);
}
