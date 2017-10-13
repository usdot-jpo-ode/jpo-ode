package us.dot.its.jpo.ode.coder.stream;

import java.io.BufferedInputStream;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.coder.OdeLogMetadataCreatorHelper;
import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher.ImporterFileType;
import us.dot.its.jpo.ode.importer.parser.BsmFileParser;
import us.dot.its.jpo.ode.importer.parser.LogFileParser;
import us.dot.its.jpo.ode.importer.parser.RxMsgFileParser;
import us.dot.its.jpo.ode.importer.parser.FileParser.ParserStatus;
import us.dot.its.jpo.ode.model.Asn1Encoding;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeAsn1Metadata;
import us.dot.its.jpo.ode.model.OdeAsn1Payload;
import us.dot.its.jpo.ode.model.Asn1Encoding.EncodingRule;
import us.dot.its.jpo.ode.util.XmlUtils;

public class LogFileToAsn1CodecPublisher implements Asn1CodecPublisher {

   protected static final Logger logger = LoggerFactory.getLogger(LogFileToAsn1CodecPublisher.class);

   private static final String BSMS_FOR_EVENT_PREFIX = "bsmLogDuringEvent";
   private static final String RECEIVED_MESSAGES_PREFIX = "rxMsg";
   private static final String DN_MESSAGE_PREFIX = "dmTx";
   private static final String BSM_TX_MESSAGE_PREFIX = "bsmTx";

   protected StringPublisher publisher;
   protected LogFileParser fileParser;

   protected static AtomicInteger bundleId = new AtomicInteger(1);

   public LogFileToAsn1CodecPublisher(StringPublisher dataPub) {
      this.publisher = dataPub;
   }

   public void publish(BufferedInputStream bis, String fileName, ImporterFileType fileType) throws Exception {
      XmlUtils xmlUtils = new XmlUtils();
      ParserStatus status = ParserStatus.UNKNOWN;

      do {
         try {

            if (fileType == ImporterFileType.BSM_LOG_FILE) {
               if (fileName.startsWith(BSMS_FOR_EVENT_PREFIX)) {
                  logger.debug("Parsing as \"BSM For Event\" log file type.");
                  fileParser = new BsmFileParser(bundleId.incrementAndGet());
               } else if (fileName.startsWith(RECEIVED_MESSAGES_PREFIX)) {
                  logger.debug("Parsing as \"Received Messages\" log file type.");
                  fileParser = new RxMsgFileParser(bundleId.incrementAndGet());
               } else if (fileName.startsWith(DN_MESSAGE_PREFIX)) {
                  logger.debug("Parsing as \"Distress Notification Messages\" log file type.");
                  fileParser = new RxMsgFileParser(bundleId.incrementAndGet());
               } else if (fileName.startsWith(BSM_TX_MESSAGE_PREFIX)) {
                  logger.debug("Parsing as \"BSM Transmit Messages\" log file type.");
                  fileParser = new BsmFileParser(bundleId.incrementAndGet());
               } else {
                  throw new IllegalArgumentException("Unknown log file prefix: " + fileName);
               }

               status = fileParser.parseFile(bis, fileName);
               if (status == ParserStatus.COMPLETE) {
                  OdeAsn1Payload payload = new OdeAsn1Payload(fileParser.getPayload());
                  OdeAsn1Metadata metadata = new OdeAsn1Metadata(payload);
                  metadata.getSerialId().setBundleId(bundleId.get()).addRecordId(1);
                  OdeLogMetadataCreatorHelper.updateLogMetadata(metadata, fileParser);

                  Asn1Encoding msgEncoding = new Asn1Encoding("root", "Ieee1609Dot2Data", EncodingRule.COER);
                  Asn1Encoding unsecuredDataEncoding = new Asn1Encoding("unsecuredData", "MessageFrame",
                        EncodingRule.UPER);
                  metadata.addEncoding(msgEncoding).addEncoding(unsecuredDataEncoding);
                  OdeAsn1Data asn1Data = new OdeAsn1Data(metadata, payload);

                  // publisher.publish(asn1Data.toJson(false),
                  // publisher.getOdeProperties().getKafkaTopicAsn1EncodedBsm());
                  publisher.publish(xmlUtils.toXml(asn1Data),
                     publisher.getOdeProperties().getKafkaTopicAsn1DecoderInput());
               } else if (status == ParserStatus.EOF) {
                  // if parser returns PARTIAL record, we will go back and continue
                  // parsing
                  // but if it's UNKNOWN, it means that we could not parse the
                  // header bytes
                  if (status == ParserStatus.INIT) {
                     logger.error("Failed to parse the header bytes.");
                  } else {
                     logger.error("Failed to decode ASN.1 data");
                  }
               }
            } else {
                status = ParserStatus.NA;
            }
         } catch (Exception e) {
            logger.error("Error decoding and publishing data.", e);
         }
      } while (status == ParserStatus.COMPLETE);
   }

   @Override
   public void publish(byte[] asn1Encoding) throws Exception {
      OdeAsn1Payload payload = new OdeAsn1Payload(asn1Encoding);
      OdeAsn1Metadata metadata = new OdeAsn1Metadata(payload);
      metadata.getSerialId().setBundleId(bundleId.get()).addRecordId(1);
      // OdeLogMetadataCreatorHelper.updateLogMetadata(metadata, bsmFileParser);

      Asn1Encoding msgEncoding = new Asn1Encoding("root", "MessageFrame", EncodingRule.UPER);
      metadata.addEncoding(msgEncoding);
      OdeAsn1Data asn1Data = new OdeAsn1Data(metadata, payload);

      // publisher.publish(asn1Data.toJson(false),
      // publisher.getOdeProperties().getKafkaTopicAsn1EncodedBsm());
      publisher.publish(XmlUtils.toXmlS(asn1Data), publisher.getOdeProperties().getKafkaTopicAsn1DecoderInput());
   }
}
