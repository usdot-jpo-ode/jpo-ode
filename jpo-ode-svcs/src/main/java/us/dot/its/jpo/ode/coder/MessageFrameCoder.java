package us.dot.its.jpo.ode.coder;

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
       Asn1Object decoded = asn1Coder.UPER_DecodeMessageFrameHex(line);
       return decoded;
    }

    @Override
    public Asn1Object decode(InputStream is) {
       Asn1Object decoded = asn1Coder.UPER_DecodeMessageFrameStream(is);
       return decoded;
    }

    @Override
    public void publish(String topic, Asn1Object msg) {
        J2735MessageFrame msgFrame = (J2735MessageFrame)msg;
        SerializationUtils<J2735Bsm> serializer = new SerializationUtils<J2735Bsm>();
        publish(topic, serializer.serialize(msgFrame.getValue()));
    }
}
