package us.dot.its.jpo.ode.coder;

import java.io.IOException;
import java.io.InputStream;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public interface Coder {

    public void decodeHexAndPublish(InputStream is) throws IOException;
    public void decodeBinaryAndPublish(InputStream is) throws IOException;
    public void decodeJsonAndPublish(InputStream is) throws IOException;
    public Asn1Object decode(String line);
    public Asn1Object decode(InputStream is);
    
    public void publish(String msg);
    public void publish(byte[] msg);
    public void publish(Asn1Object msg);
}
