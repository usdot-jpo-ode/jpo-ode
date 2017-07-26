package us.dot.its.jpo.ode.coder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.slf4j.Logger;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.plugin.PluginFactory;
import us.dot.its.jpo.ode.plugin.asn1.J2735Plugin;
import us.dot.its.jpo.ode.plugin.j2735.oss.Oss1609dot2Coder;
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
        if (this.j2735Coder == null) {
            logger.info("Loading ASN1 Coder: {}", this.odeProperties.getJ2735CoderClassName());
            try {
                this.j2735Coder = (J2735Plugin) PluginFactory.getPluginByName(properties.getJ2735CoderClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                logger.error("Unable to load plugin: " + properties.getJ2735CoderClassName(), e);
            }
        }
        
        this.ieee1609dotCoder = new Oss1609dot2Coder();
    }

    @Override
    public void decodeHexAndPublish(InputStream is) throws IOException {
        String line = null;
        OdeData decoded = null;

        try (Scanner scanner = new Scanner(is)) {

            boolean empty = true;
            while (scanner.hasNextLine()) {
                empty = false;
                line = scanner.nextLine();

                decoded = decode(line);
                publish(decoded);
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

    @Override
    public void decodeBinaryAndPublish(InputStream is) throws IOException {
        OdeData decoded;

        try {
            do {
                decoded = decode(is);
                if (decoded != null) {
                    logger.debug("Decoded: {}", decoded);
                    publish(decoded);
                }
            } while (decoded != null);
        } catch (Exception e) {
            throw new IOException("Error decoding data." + e);
        }
    }

    @Override
    public void decodeBytesAndPublish(byte[] bytes) throws IOException {
        OdeData decoded;

        try {
            decoded = decode(bytes);
            if (decoded != null) {
                logger.debug("Decoded: {}", decoded);
                publish(decoded);
            }
        } catch (Exception e) {
            throw new IOException("Error decoding data." + e);
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

    protected abstract Logger getLogger();
}
