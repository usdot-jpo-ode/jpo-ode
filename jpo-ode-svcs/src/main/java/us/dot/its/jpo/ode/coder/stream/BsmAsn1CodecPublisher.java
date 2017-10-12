package us.dot.its.jpo.ode.coder.stream;

import java.io.BufferedInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.coder.OdeLogMetadataCreatorHelper;
import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.importer.parser.BsmFileParser;
import us.dot.its.jpo.ode.importer.parser.FileParser.ParserStatus;
import us.dot.its.jpo.ode.model.Asn1Encoding;
import us.dot.its.jpo.ode.model.Asn1Encoding.EncodingRule;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeAsn1Metadata;
import us.dot.its.jpo.ode.model.OdeAsn1Payload;
import us.dot.its.jpo.ode.util.XmlUtils;

public class BsmAsn1CodecPublisher extends AbstractAsn1CodecPublisher {

    private static final Logger logger = LoggerFactory.getLogger(BsmAsn1CodecPublisher.class);

    protected BsmFileParser bsmFileParser;

    public BsmAsn1CodecPublisher(StringPublisher dataPub) {
        super(dataPub);
    }

   @Override
   public void publish(BufferedInputStream bis, String fileName) throws Exception {
      ParserStatus status = ParserStatus.UNKNOWN;
      this.bsmFileParser = new BsmFileParser(bundleId.incrementAndGet());
      XmlUtils xmlUtils = new XmlUtils();
      do {
         try {
            status = bsmFileParser.parseFile(bis, fileName);

            if (status == ParserStatus.COMPLETE) {
               OdeAsn1Payload payload = new OdeAsn1Payload(bsmFileParser.getPayload());
               OdeAsn1Metadata metadata = new OdeAsn1Metadata(payload);
               metadata.getSerialId().setBundleId(bundleId.get()).addRecordId(1);
               OdeLogMetadataCreatorHelper.updateLogMetadata(metadata, bsmFileParser);
               
               Asn1Encoding msgEncoding = new Asn1Encoding("root", "Ieee1609Dot2Data", EncodingRule.COER);
               Asn1Encoding unsecuredDataEncoding = new Asn1Encoding("unsecuredData", "MessageFrame", EncodingRule.UPER);
               metadata.addEncoding(msgEncoding).addEncoding(unsecuredDataEncoding);
               OdeAsn1Data asn1Data = new OdeAsn1Data(metadata , payload);

//               publisher.publish(asn1Data.toJson(false), publisher.getOdeProperties().getKafkaTopicAsn1EncodedBsm());
               publisher.publish(xmlUtils.toXml(asn1Data), publisher.getOdeProperties().getKafkaTopicAsn1EncodedBsm());
            } else {
               // if parser returns PARTIAL record, we will go back and continue parsing
               // but if it's UNKNOWN, it means that we could not parse the header bytes
               if (status == ParserStatus.INIT) {
                  logger.error("Failed to parse the header bytes.");
               } else {
                  logger.error("Failed to decode ASN.1 data");
               }
            }
         } catch (Exception e) {
            logger.error("Error decoding and publishing data.", e);
         }
      } while (status == ParserStatus.COMPLETE);
   }

}
