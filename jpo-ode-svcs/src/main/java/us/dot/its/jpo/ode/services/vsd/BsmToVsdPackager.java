package us.dot.its.jpo.ode.services.vsd;

import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;
import com.oss.asn1.PERUnalignedCoder;

import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.wrapper.AbstractSubPubTransformer;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

/**
 * Kafka consumer/publisher that creates VSDs from BSMs.
 * 
 * Input stream: j2735FilteredBsm (JSON string) Output stream: encodedVsd (byte
 * array)
 */
public class BsmToVsdPackager extends AbstractSubPubTransformer<String, String, byte[]> {

   private static final Logger logger = LoggerFactory.getLogger(BsmToVsdPackager.class);

   private final PERUnalignedCoder coder;

   private VsdBundler bundler;

   public BsmToVsdPackager(MessageProducer<String, byte[]> producer, String outputTopic) {
      super(producer, (java.lang.String) outputTopic);
      this.coder = J2735.getPERUnalignedCoder();
      this.bundler = new VsdBundler();
   }

   @Override
   protected byte[] transform(String consumedData) {
      
      OdeBsmData odeBsmData = (OdeBsmData) JsonUtils.fromJson(consumedData, OdeBsmData.class);
      J2735Bsm bsmData = (J2735Bsm) odeBsmData.getPayload().getData();

      byte[] encodedVsd = null;
      try {
         logger.debug("Consuming BSM.");

         VehSitDataMessage vsd = bundler.addToVsdBundle(bsmData);

         // Only full VSDs (10) will be published
         // TODO - toggleable mechanism for periodically publishing not-full
         // VSDs
         if (vsd != null) {
            encodedVsd = coder.encode(vsd).array();
            String hexMsg = HexUtils.toHexString(encodedVsd);
            logger.debug("VSD ready to send: {}", hexMsg);
         }
      } catch (EncodeFailedException | EncodeNotSupportedException e) {
         logger.error("Error Sending VSD to SDC", e);
      }
      return encodedVsd;
   }
}