package us.dot.its.jpo.ode.services.asn1;

import java.util.Arrays;
import java.util.concurrent.Executors;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.coder.OdeBsmDataCreatorHelper;
import us.dot.its.jpo.ode.coder.OdeTimDataCreatorHelper;
import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.plugin.j2735.J2735DSRCmsgID;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;
import us.dot.its.jpo.ode.wrapper.MessageProcessor;
import us.dot.its.jpo.ode.wrapper.MessageProducer;
import us.dot.its.jpo.ode.wrapper.serdes.OdeBsmSerializer;

public class Asn1CodecRouter extends MessageProcessor<String, String> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    protected int messagesConsumed = 0;

    private OdeProperties odeProperties;
    private MessageProducer<String, OdeBsmData> bsmProducer;
    private MessageProducer<String, String> timProducer;
    
    public Asn1CodecRouter(OdeProperties odeProps) {
      super();
      this.odeProperties = odeProps;
      this.bsmProducer = new MessageProducer<String, OdeBsmData>(
            odeProps.getKafkaBrokers(), odeProps.getKafkaProducerType(), 
            null, OdeBsmSerializer.class.getName());
      this.timProducer = MessageProducer.defaultStringMessageProducer(
            odeProps.getKafkaBrokers(), odeProps.getKafkaProducerType());
    }

   /**
     * Starts a Kafka listener that runs call() every time a new msg arrives
     * 
     * @param consumer
     * @param inputTopics
     */
    public void start(MessageConsumer<String, String> consumer, String... inputTopics) {
        logger.info("Subscribing to {}", Arrays.asList(inputTopics).toString());
        
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                consumer.subscribe(inputTopics);
            }
        });
    }

    @Override
    public Object call() {
        messagesConsumed++;
        
        String consumedData = record.value();
        
        try {
           JSONObject consumed = XmlUtils.toJSONObject(consumedData).getJSONObject(
              OdeAsn1Data.class.getSimpleName());
           int messageId = consumed.getJSONObject(AppContext.PAYLOAD_STRING)
                 .getJSONObject(AppContext.DATA_STRING)
                 .getJSONObject("MessageFrame")
                 .getInt("messageId");
           
           if (messageId == J2735DSRCmsgID.BasicSafetyMessage.getMsgID()) {
              bsmProducer.send(odeProperties.getKafkaTopicOdeBsmPojo(), record.key(),
                 OdeBsmDataCreatorHelper.createOdeBsmData(consumedData));
           } else if (messageId == J2735DSRCmsgID.TravelerInformation.getMsgID()) {
              timProducer.send(odeProperties.getKafkaTopicOdeTimJson(), record.key(), 
                 OdeTimDataCreatorHelper.createOdeTimData(consumedData).toString());
           }
        } catch (Exception e) {
           logger.error("Failed to route received data: " + consumedData, e);
        }
        return null;
    }
}
