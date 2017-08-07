package us.dot.its.jpo.ode.coder;

import java.io.InputStream;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.plugin.PluginFactory;
import us.dot.its.jpo.ode.plugin.asn1.J2735Plugin;
import us.dot.its.jpo.ode.plugin.j2735.oss.Oss1609dot2Coder;
import us.dot.its.jpo.ode.util.CodecUtils;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public abstract class AbstractStreamDecoderPublisher implements StreamDecoderPublisher {

    protected final Logger logger;

    protected OdeProperties odeProperties;
    protected J2735Plugin j2735Coder;
    protected Oss1609dot2Coder ieee1609dotCoder;
    protected MessageProducer<String, String> stringProducer;
    protected MessageProducer<String, byte[]> byteArrayProducer;
    protected MessageProducer<String, OdeObject> objectProducer;

    protected AbstractStreamDecoderPublisher(
        OdeProperties properties, String valueSerializerFQN) {
        super();
        this.odeProperties = properties;
        this.objectProducer = new MessageProducer<String, OdeObject>(
                odeProperties.getKafkaBrokers(),
                odeProperties.getKafkaProducerType(),
                null,
                valueSerializerFQN);
        
        logger = getLogger();
            logger.info("Loading ASN1 Coder: {}", this.odeProperties.getJ2735CoderClassName());
            try {
                this.j2735Coder = (J2735Plugin) PluginFactory.getPluginByName(properties.getJ2735CoderClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                logger.error("Unable to load plugin: " + properties.getJ2735CoderClassName(), e);
            }
        
        this.ieee1609dotCoder = new Oss1609dot2Coder();
    }

    @Override
    public void decodeHexAndPublish(InputStream is) throws Exception {
        String line = null;
        OdeData decoded = null;

        try (Scanner scanner = new Scanner(is)) {

            boolean empty = true;
            while (scanner.hasNextLine()) {
                try {
                    empty = false;
                    line = scanner.nextLine();

                    decoded = decode(line);
                    if (decoded != null) {
                        logger.debug("Decoded: {}", decoded);
                        publish(decoded);
                    } else {
                        logger.debug("Failed to decode, {}.", line);
                    }
                } catch (Exception e) {
                    String msg = "Error decoding and publishing data.";
                    EventLogger.logger.error(msg, e);
                    logger.error(msg, e);
                }
            }

            if (empty) {
                EventLogger.logger.info("Empty file received");
                throw new Exception("Empty file received");
            }
        } catch (Exception e) {
            String msg = "Error decoding and publishing data: ";
            EventLogger.logger.error(msg, e);
            throw new Exception(msg + line, e);
        }
    }

    @Override
    public void decodeBinaryAndPublish(InputStream is) throws Exception {
        OdeData decoded = null;

        do {
            try {
                decoded = decode(is);
                if (decoded != null) {
                    logger.debug("Decoded: {}", decoded);
                    publish(decoded);
                } else {
                   logger.debug("Failed to decode stream data");
                }
            } catch (Exception e) {
                String msg = "Error decoding and publishing data.";
                EventLogger.logger.error(msg, e);
                logger.error(msg, e);
            }
        } while (decoded != null);
    }

    @Override
    public void decodeBytesAndPublish(byte[] bytes) throws Exception {
        OdeData decoded;

        try {
            decoded = decode(bytes);
            if (decoded != null) {
                logger.debug("Decoded: {}", decoded);
                publish(decoded);
            } else {
               logger.debug("Failed to decode {}.", CodecUtils.toHex(bytes));
            }
        } catch (Exception e) {
            String msg = "Error decoding and publishing data.";
            EventLogger.logger.error(msg, e);
            throw new Exception("Error decoding and publishing data.", e);
        }
    }

    public J2735Plugin getJ2735Coder() {
        return j2735Coder;
    }

    public void setJ2735Coder(J2735Plugin j2735Coder) {
        this.j2735Coder = j2735Coder;
    }

    public Oss1609dot2Coder getIeee1609dotCoder() {
        return ieee1609dotCoder;
    }

    public void setIeee1609dotCoder(Oss1609dot2Coder ieee1609dotCoder) {
        this.ieee1609dotCoder = ieee1609dotCoder;
    }

    protected Logger getLogger() {
       return LoggerFactory.getLogger(this.getClass());
    }
}
