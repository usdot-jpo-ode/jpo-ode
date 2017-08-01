package us.dot.its.jpo.ode.coder;

import java.io.InputStream;

import us.dot.its.jpo.ode.model.OdeData;

public interface StreamDecoderPublisher {

    public void decodeHexAndPublish(InputStream is) throws Exception;
    public void decodeBinaryAndPublish(InputStream is) throws Exception;
    public void decodeJsonAndPublish(InputStream is) throws Exception;
    public void decodeBytesAndPublish(byte[] is) throws Exception;
    public OdeData decode(String hexEncodedData) throws Exception;
    public OdeData decode(InputStream is) throws Exception;
    public OdeData decode(byte[] bytes) throws Exception;
    
    public void publish(OdeData msg);
}
