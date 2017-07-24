package us.dot.its.jpo.ode.coder;

import java.io.IOException;
import java.io.InputStream;

import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.Ieee1609Dot2Data;
import us.dot.its.jpo.ode.model.OdeObject;

public interface StreamDecoderPublisher {

    public void decodeHexAndPublish(InputStream is) throws IOException;
    public void decodeBinaryAndPublish(InputStream is) throws IOException;
    public void decodeJsonAndPublish(InputStream is) throws IOException;
    public OdeObject decode(String line);
    public OdeObject decode(InputStream is);
    
    public void publish(String msg, Ieee1609Dot2Data ieee1609dot2Data);
    public void publish(byte[] msg, Ieee1609Dot2Data ieee1609dot2Data);
    public void publish(OdeObject msg, Ieee1609Dot2Data ieee1609dot2Data);
}
