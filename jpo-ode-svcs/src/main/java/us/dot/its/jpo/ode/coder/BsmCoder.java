package us.dot.its.jpo.ode.coder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.util.JsonUtils;
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

    @Override
    public void decodeJsonAndPublish(InputStream is) throws IOException {
        String line = null;
        Asn1Object decoded = null;

        try (Scanner scanner = new Scanner(is)) {

            boolean empty = true;
            while (scanner.hasNextLine()) {
                empty = false;
                line = scanner.nextLine();

                decoded = (Asn1Object) JsonUtils.fromJson(line, J2735Bsm.class);
                publish(decoded);
                publish(decoded.toJson());
            }
            if (empty) {
                EventLogger.logger.info("Empty file received");
                throw new IOException("Empty file received");
            }
        } catch (IOException e) {
            EventLogger.logger.info("Error occurred while decoding message: {}", line);
            throw new IOException("Error decoding data: " + line, e);
        }
    }


}
