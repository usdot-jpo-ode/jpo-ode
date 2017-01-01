package us.dot.its.jpo.ode.bsm;

import java.io.InputStream;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SerializationUtils;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.SerializableMessageProducerPool;
import us.dot.its.jpo.ode.plugin.PluginFactory;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Plugin;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class BsmCoder {

   private static Logger logger = LoggerFactory.getLogger(BsmCoder.class);

   private OdeProperties odeProperties;
   private Asn1Plugin asn1Coder;
   private SerializableMessageProducerPool<String, byte[]> messageProducerPool;
   
   public BsmCoder() {
      super();
   }

   public BsmCoder(OdeProperties properties) {
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
      
      messageProducerPool = 
            new SerializableMessageProducerPool<String, byte[]>(odeProperties);
   }

   public void decodeFromHexAndPublish(InputStream is, String topic)
         throws ClassNotFoundException, InstantiationException, IllegalAccessException {
      String line = null;
      J2735Bsm decoded = null;

      try (Scanner scanner = new Scanner(is)) {
         while (scanner.hasNextLine()) {
            line = scanner.nextLine();

            decoded = (J2735Bsm) asn1Coder.UPER_DecodeHex(line);
            logger.debug("Decoded: {}", decoded);
            if (topic == OdeProperties.KAFKA_TOPIC_J2735_BSM)
               publish(topic, decoded);
            else
               publish(topic, decoded.toJson());
         }
      } catch (Exception e) {
         logger.error("Error decoding data: " + line, e);
      }
   }

   public void publish(String topic, J2735Bsm msg) {
      publish(topic, SerializationUtils.serialize(msg));
   }

   public void publish(String topic, String msg) {
	        MessageProducer.defaultStringMessageProducer(
	        		odeProperties.getKafkaBrokers(), 
	        		odeProperties.getKafkaProducerType())
	        	.send(topic, null, msg);
	        
	        logger.debug("Published: {}", msg);
   }

   public void publish(String topic, byte[] msg) {
      MessageProducer<String, byte[]> producer = messageProducerPool.checkOut();
      producer.send(topic, null, msg);
      logger.debug("Published: {}", SerializationUtils.deserialize(msg));
      messageProducerPool.checkIn(producer);
   }

   public OdeProperties getOdeProperties() {
      return odeProperties;
   }

   public void setOdeProperties(OdeProperties odeProperties) {
      this.odeProperties = odeProperties;
   }

}
