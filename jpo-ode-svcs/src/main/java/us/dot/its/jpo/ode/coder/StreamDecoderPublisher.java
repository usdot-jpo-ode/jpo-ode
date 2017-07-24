package us.dot.its.jpo.ode.coder;

import java.io.IOException;
import java.io.InputStream;

import us.dot.its.jpo.ode.model.OdeObject;

public interface StreamDecoderPublisher {

    public void decodeHexAndPublish(InputStream is) throws IOException;
    public void decodeBinaryAndPublish(InputStream is) throws IOException;
    public void decodeJsonAndPublish(InputStream is) throws IOException;
    public OdeObject decode(String line);
    public OdeObject decode(InputStream is);
    
    public void publish(String msg);
    public void publish(byte[] msg);
    public void publish(OdeObject msg);
}
