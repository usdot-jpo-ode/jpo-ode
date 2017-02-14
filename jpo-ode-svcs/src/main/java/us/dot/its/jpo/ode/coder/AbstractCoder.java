package us.dot.its.jpo.ode.coder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.SerializableMessageProducerPool;
import us.dot.its.jpo.ode.plugin.PluginFactory;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Plugin;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public abstract class AbstractCoder implements Coder {

    protected static Logger logger = LoggerFactory.getLogger(BsmCoder.class);

    protected OdeProperties odeProperties;
    protected Asn1Plugin asn1Coder;
    protected SerializableMessageProducerPool<String, byte[]> messageProducerPool;

    public AbstractCoder() {
        super();
    }

    public AbstractCoder(OdeProperties properties) {
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

    public void publish(String topic, String msg) {
        MessageProducer
                .defaultStringMessageProducer(odeProperties.getKafkaBrokers(), odeProperties.getKafkaProducerType())
                .send(topic, null, msg);

        logger.debug("Published: {}", msg);
    }

    public void publish(String topic, byte[] msg) {
        MessageProducer<String, byte[]> producer = messageProducerPool.checkOut();
        producer.send(topic, null, msg);
        messageProducerPool.checkIn(producer);
    }

}
