package us.dot.its.jpo.ode.coder;

import java.io.InputStream;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.util.SerializationUtils;

public class BsmCoder extends AbstractCoder {

    public BsmCoder() {
        super();
    }

    public BsmCoder(OdeProperties properties) {
        super(properties);
    }

    @Override
    public Asn1Object decode(String line) {
        return asn1Coder.decodeUPERBsmHex(line);
    }

    @Override
    public Asn1Object decode(InputStream is) {
        return asn1Coder.decodeUPERBsmStream(is);
    }

    @Override
    public void publish(Asn1Object msg) {
        J2735Bsm bsm = (J2735Bsm) msg;
        SerializationUtils<J2735Bsm> serializer = new SerializationUtils<>();
        publish(serializer.serialize(bsm));
    }

}
