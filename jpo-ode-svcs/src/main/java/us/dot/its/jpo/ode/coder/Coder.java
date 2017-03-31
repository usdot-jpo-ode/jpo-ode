package us.dot.its.jpo.ode.coder;

import java.io.IOException;
import java.io.InputStream;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public interface Coder {

    public void decodeFromHexAndPublish(InputStream is, String topic) throws IOException;
    public void decodeFromStreamAndPublish(InputStream is, String topic) throws IOException;
    public Asn1Object decode(String line);
    public Asn1Object decode(InputStream is);
    
    public void publish(String topic, String msg);
    public void publish(String topic, byte[] msg);
    public void publish(String topic, Asn1Object msg);
}
