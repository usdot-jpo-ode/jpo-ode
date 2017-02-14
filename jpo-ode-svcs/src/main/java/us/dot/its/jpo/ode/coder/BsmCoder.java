package us.dot.its.jpo.ode.coder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.util.SerializationUtils;

public class BsmCoder extends AbstractCoder {
    
    public BsmCoder() {
        super();
    }
    
    public BsmCoder(OdeProperties properties) {
        super(properties);
    }

    public void decodeFromHexAndPublish(InputStream is, String topic) throws IOException {
        String line = null;
        J2735Bsm decoded = null;

        try (Scanner scanner = new Scanner(is)) {

            boolean empty = true;
            while (scanner.hasNextLine()) {
                empty = false;
                line = scanner.nextLine();

                decoded = (J2735Bsm) asn1Coder.UPER_DecodeBsmHex(line);
                logger.debug("Decoded: {}", decoded);
                if (!OdeProperties.KAFKA_TOPIC_J2735_BSM.endsWith("json"))
                    publish(topic, decoded);
                else
                    publish(topic, decoded.toJson());
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

    public void decodeFromStreamAndPublish(InputStream is, String topic) throws IOException {
        J2735Bsm decoded = null;

        try {
            do {
                decoded = (J2735Bsm) asn1Coder.UPER_DecodeBsmStream(is);
                if (decoded != null) {
                    logger.debug("Decoded: {}", decoded);
                    if (!OdeProperties.KAFKA_TOPIC_J2735_BSM.endsWith("json"))
                        publish(topic, decoded);
                    else
                        publish(topic, decoded.toJson());
                }
            } while (decoded != null);

        } catch (Exception e) {
            throw new IOException("Error decoding data.", e);
        }
    }

    public void publish(String topic, J2735Bsm msg) {
        SerializationUtils<J2735Bsm> serializer = new SerializationUtils<>();
        publish(topic, serializer.serialize(msg));
        logger.debug("Published: {}", msg.toJson());
    }

}
