package us.dot.its.jpo.ode.services.asn1;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.coder.OdeBsmDataCreatorHelper;
import us.dot.its.jpo.ode.coder.OdeTimDataCreatorHelper;
import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.importer.parser.LogFileParser.RecordType;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.plugin.j2735.J2735DSRCmsgID;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.wrapper.AbstractSubscriberProcessor;
import us.dot.its.jpo.ode.wrapper.MessageProducer;
import us.dot.its.jpo.ode.wrapper.serdes.OdeBsmSerializer;

public class Asn1DecodedDataRouter extends AbstractSubscriberProcessor<String, String> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private OdeProperties odeProperties;
    private MessageProducer<String, OdeBsmData> bsmProducer;
    private MessageProducer<String, String> timProducer;
    
    public Asn1DecodedDataRouter(OdeProperties odeProps) {
      super();
      this.odeProperties = odeProps;
      this.bsmProducer = new MessageProducer<String, OdeBsmData>(
            odeProps.getKafkaBrokers(), odeProps.getKafkaProducerType(), 
            null, OdeBsmSerializer.class.getName());
      this.timProducer = MessageProducer.defaultStringMessageProducer(
            odeProps.getKafkaBrokers(), odeProps.getKafkaProducerType());
    }

    @Override
    public Object process(String consumedData) {
        try {
           JSONObject consumed = XmlUtils.toJSONObject(consumedData).getJSONObject(
              OdeAsn1Data.class.getSimpleName());
           int messageId = consumed.getJSONObject(AppContext.PAYLOAD_STRING)
                 .getJSONObject(AppContext.DATA_STRING)
                 .getJSONObject("MessageFrame")
                 .getInt("messageId");
           
           RecordType recordType = RecordType.valueOf(consumed.getJSONObject(AppContext.METADATA_STRING)
                 .getString("recordType"));
           
           if (messageId == J2735DSRCmsgID.BasicSafetyMessage.getMsgID()) {
         	  //ODE-518/ODE-604 Demultiplex the messages to appropriate topics based on the "recordType"
              OdeBsmData odeBsmData = OdeBsmDataCreatorHelper.createOdeBsmData(consumedData);
              if (recordType == RecordType.bsmLogDuringEvent) {
                 bsmProducer.send(odeProperties.getKafkaTopicOdeBsmDuringEventPojo(), getRecord().key(),
                    odeBsmData);
              } else if (recordType == RecordType.rxMsg) {
                 bsmProducer.send(odeProperties.getKafkaTopicOdeBsmRxPojo(), getRecord().key(),
                    odeBsmData);
              } else if (recordType == RecordType.bsmTx) {
                 bsmProducer.send(odeProperties.getKafkaTopicOdeBsmTxPojo(), getRecord().key(),
                    odeBsmData);
              }
              // Send all BSMs also to OdeBsmPojo
              bsmProducer.send(odeProperties.getKafkaTopicOdeBsmPojo(), getRecord().key(),
                 odeBsmData);
           } else if (messageId == J2735DSRCmsgID.TravelerInformation.getMsgID()) {
              String odeTimData = OdeTimDataCreatorHelper.createOdeTimData(consumed).toString();
              if (recordType == RecordType.dnMsg) {
                 timProducer.send(odeProperties.getKafkaTopicOdeDNMsgJson(), getRecord().key(), 
                    odeTimData);
              } else if (recordType == RecordType.rxMsg){
                 timProducer.send(odeProperties.getKafkaTopicOdeTimRxJson(), getRecord().key(), 
                    odeTimData);
              }
              // Send all TIMs also to OdeTimJson
              timProducer.send(odeProperties.getKafkaTopicOdeTimJson(), getRecord().key(), 
                 odeTimData);
           }
        } catch (Exception e) {
           logger.error("Failed to route received data: " + consumedData, e);
        }
        return null;
    }
}
