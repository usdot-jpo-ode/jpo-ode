package us.dot.its.jpo.ode.services.vsd;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.dot.its.jpo.ode.context.AppContext;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.wrapper.AbstractSubPubTransformer;
import us.dot.its.jpo.ode.wrapper.MessageProducer;
import us.dot.its.jpo.ode.wrapper.serdes.VehSitDataMessageSerializer;

/**
 * Kafka consumer/publisher that creates VSDs from BSMs.
 * 
 * Input stream: j2735FilteredBsm (JSON string) Output stream: encodedVsd (byte
 * array)
 */
public class BsmToVsdPackager extends AbstractSubPubTransformer<String, String, byte[]> {

   private static final Logger logger = LoggerFactory.getLogger(BsmToVsdPackager.class);
   
   private VsdBundler bundler;
   private ObjectMapper mapper;
   private VehSitDataMessageSerializer serializer;

   public BsmToVsdPackager(MessageProducer<String, byte[]> producer, String outputTopic) {
      super(producer, (java.lang.String) outputTopic);
      this.bundler = new VsdBundler();
      this.mapper = new ObjectMapper();
      this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      this.serializer = new VehSitDataMessageSerializer();
   }

   @Override
   protected byte[] process(String consumedData) {

      JsonNode bsmNode = JsonUtils.getJsonNode(consumedData, AppContext.PAYLOAD_STRING).get(AppContext.DATA_STRING);

      J2735Bsm bsmData;
      try {
         bsmData = mapper.treeToValue(bsmNode, J2735Bsm.class);
      } catch (IOException e) {
         logger.error("Failed to decode JSON object.", e);
         return null;
      }
      
      byte[] returnValue = null;
      logger.debug("Consuming BSM.");

      VehSitDataMessage vsd = bundler.addToVsdBundle(bsmData);

      // Only full VSDs (10) will be published
      // TODO - toggleable mechanism for periodically publishing not-full
      // VSDs
      if (vsd != null) {
         returnValue = serializer.serialize(null, vsd);
      }
      return returnValue;
   }
}