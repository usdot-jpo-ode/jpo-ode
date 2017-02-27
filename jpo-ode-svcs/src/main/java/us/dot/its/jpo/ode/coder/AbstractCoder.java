package us.dot.its.jpo.ode.coder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.SerializableMessageProducerPool;
import us.dot.its.jpo.ode.eventlog.EventLogger;
import us.dot.its.jpo.ode.plugin.PluginFactory;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Plugin;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public abstract class AbstractCoder implements Coder {

    protected static Logger logger = LoggerFactory.getLogger(AbstractCoder.class);

    protected OdeProperties odeProperties;
    protected Asn1Plugin asn1Coder;
    protected SerializableMessageProducerPool<String, byte[]> messageProducerPool;

    protected AbstractCoder() {
        super();
    }

    protected AbstractCoder(OdeProperties properties) {
        super();
        this.odeProperties = properties;
        if (this.asn1Coder == null) {
            logger.info("Loading ASN1 Coder: {}", this.odeProperties.getAsn1CoderClassName());
            try {
                this.asn1Coder = (Asn1Plugin) PluginFactory.getPluginByName(properties.getAsn1CoderClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                logger.error("Unable to load plugin: " + properties.getAsn1CoderClassName(), e);
            }
        }

        messageProducerPool = new SerializableMessageProducerPool<>(odeProperties);
    }

    @Override
    public void decodeFromHexAndPublish(InputStream is, String topic) throws IOException {
       String line = null;
       Asn1Object decoded = null;

       try (Scanner scanner = new Scanner(is)) {

           boolean empty = true;
           while (scanner.hasNextLine()) {
               empty = false;
               line = scanner.nextLine();

               decoded = decode(line);
               publish(topic, decoded);
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
   public void decodeFromStreamAndPublish(InputStream is, String topic) throws IOException {
       Asn1Object decoded;
       

       try {
           do {
               decoded = decode(is);
               if (decoded != null) {
                   logger.debug("Decoded: {}", decoded);
                   publish(topic, decoded);
               }
           } while (decoded != null);

       } catch (Exception e) {
           throw new IOException("Error decoding data.", e);
       }
   }

   @Override
   public void publish(String topic, String msg) {
        MessageProducer
                .defaultStringMessageProducer(odeProperties.getKafkaBrokers(), odeProperties.getKafkaProducerType())
                .send(topic, null, msg);

        logger.debug("Published: {}", msg);
    }

   @Override
    public void publish(String topic, byte[] msg) {
        MessageProducer<String, byte[]> producer = messageProducerPool.checkOut();
        producer.send(topic, null, msg);
        messageProducerPool.checkIn(producer);
    }

    public abstract Asn1Object decode(String line);
    public abstract Asn1Object decode(InputStream is);
    public abstract void publish(String topic, Asn1Object msg);
}
