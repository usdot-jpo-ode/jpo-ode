package us.dot.its.jpo.ode.coder;

import java.io.IOException;
import java.io.InputStream;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public interface Coder {

    public void decodeFromHexAndPublish(InputStream is) throws IOException;
    public void decodeFromStreamAndPublish(InputStream is) throws IOException;
    public Asn1Object decode(String line);
    public Asn1Object decode(InputStream is);
    
    public void publishJson(String msg);
    public void publishByte(byte[] msg);
    public void publishRaw(Asn1Object msg);
}
