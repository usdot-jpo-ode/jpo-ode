package us.dot.its.jpo.ode.coder;

import java.io.IOException;
import java.io.InputStream;

import us.dot.its.jpo.ode.model.OdeData;

public interface StreamDecoderPublisher {

    public void decodeHexAndPublish(InputStream is) throws IOException;
    public void decodeBinaryAndPublish(InputStream is) throws IOException;
    public void decodeJsonAndPublish(InputStream is) throws IOException;
    public void decodeBytesAndPublish(byte[] is) throws IOException;
    public OdeData decode(String hexEncodedData);
    public OdeData decode(InputStream is);
    public OdeData decode(byte[] bytes);
    
    public void publish(String msg);
    public void publish(byte[] msg);
    public void publish(OdeData msg);
}
