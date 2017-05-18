package us.dot.its.jpo.ode.coder;

import java.io.IOException;
import java.io.InputStream;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735MessageFrame;
import us.dot.its.jpo.ode.util.SerializationUtils;

public class MessageFrameCoder extends AbstractCoder {
    
    public MessageFrameCoder() {
        super();
    }
    
    public MessageFrameCoder(OdeProperties properties) {
        super(properties);
    }

    @Override
    public Asn1Object decode(String line) {
       return asn1Coder.decodeUPERMessageFrameHex(line);
    }

    @Override
    public Asn1Object decode(InputStream is) {
       return asn1Coder.decodeUPERMessageFrameStream(is);
    }

    @Override
    public void publish(Asn1Object msg) {
        J2735MessageFrame msgFrame = (J2735MessageFrame)msg;
        SerializationUtils<J2735Bsm> serializer = new SerializationUtils<>();
        publish(serializer.serialize(msgFrame.getValue()));
    }

    @Override
    public void decodeJsonAndPublish(InputStream is) throws IOException {
        // TODO Auto-generated method stub
        
    }
}
