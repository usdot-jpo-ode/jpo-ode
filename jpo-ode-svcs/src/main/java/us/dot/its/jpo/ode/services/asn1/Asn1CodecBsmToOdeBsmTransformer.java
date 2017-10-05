package us.dot.its.jpo.ode.services.asn1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.coder.OdeBsmDataCreatorHelper;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.wrapper.AbstractSubPubTransformer;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

/**
 * Kafka consumer/publisher that creates VSDs from BSMs.
 * 
 * Input stream: j2735FilteredBsm (JSON string) Output stream: encodedVsd (byte
 * array)
 */
public class Asn1CodecBsmToOdeBsmTransformer extends AbstractSubPubTransformer<String, String, OdeBsmData> {

   private static final Logger logger = LoggerFactory.getLogger(Asn1CodecBsmToOdeBsmTransformer.class);
   
   public Asn1CodecBsmToOdeBsmTransformer(MessageProducer<String, OdeBsmData> producer, String outputTopic) {
      super(producer, outputTopic);
   }

   @Override
   protected OdeBsmData transform(String consumedData) {

      try {
         OdeBsmData odeBsmData = OdeBsmDataCreatorHelper.createOdeBsmData(consumedData);
         return odeBsmData;
      } catch (Exception e) {
         logger.error("Failed to decode received data", e);
         return null;
      }
   }
}