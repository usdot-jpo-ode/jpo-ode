package us.dot.its.jpo.ode.coder.stream;

import java.io.BufferedInputStream;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import us.dot.its.jpo.ode.coder.OdeLogMetadataCreatorHelper;
import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.coder.TimDecoderHelper;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher.ImporterFileType;
import us.dot.its.jpo.ode.importer.parser.BsmLogFileParser;
import us.dot.its.jpo.ode.importer.parser.DriverAlertFileParser;
import us.dot.its.jpo.ode.importer.parser.FileParser.ParserStatus;
import us.dot.its.jpo.ode.importer.parser.LogFileParser;
import us.dot.its.jpo.ode.importer.parser.TimLogFileParser;
import us.dot.its.jpo.ode.model.Asn1Encoding;
import us.dot.its.jpo.ode.model.Asn1Encoding.EncodingRule;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeAsn1Metadata;
import us.dot.its.jpo.ode.model.OdeAsn1Payload;
import us.dot.its.jpo.ode.model.OdeAsn1WithBsmMetadata;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.OdeDriverAlertData;
import us.dot.its.jpo.ode.model.OdeDriverAlertMetadata;
import us.dot.its.jpo.ode.model.OdeDriverAlertPayload;
import us.dot.its.jpo.ode.model.OdeLogMetadata;
import us.dot.its.jpo.ode.model.OdeMsgPayload;
import us.dot.its.jpo.ode.model.ReceivedMessageDetails;
import us.dot.its.jpo.ode.util.JsonUtils;
import us.dot.its.jpo.ode.util.XmlUtils;

public class LogFileToAsn1CodecPublisher implements Asn1CodecPublisher {

   public class LogFileToAsn1CodecPublisherException extends Exception {

      private static final long serialVersionUID = 1L;

      public LogFileToAsn1CodecPublisherException(String string, Exception e) {
         super (string, e);
      }

   }

   protected static final Logger logger = LoggerFactory.getLogger(LogFileToAsn1CodecPublisher.class);

   protected StringPublisher publisher;
   protected LogFileParser fileParser;

   protected static AtomicInteger bundleId = new AtomicInteger(1);

   public LogFileToAsn1CodecPublisher(StringPublisher dataPub) {
      this.publisher = dataPub;
   }

   public void publish(BufferedInputStream bis, String fileName, ImporterFileType fileType) 
         throws LogFileToAsn1CodecPublisherException {
      XmlUtils xmlUtils = new XmlUtils();
      ParserStatus status = ParserStatus.UNKNOWN;

      if (fileType == ImporterFileType.OBU_LOG_FILE) {
         fileParser = LogFileParser.factory(fileName, bundleId.incrementAndGet());
      } else {
         status = ParserStatus.NA;
      }
      
      do {
         try {
            status = fileParser.parseFile(bis, fileName);
            if (status == ParserStatus.COMPLETE) {
               publish(xmlUtils);
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
         } catch (Exception e) {
            throw new LogFileToAsn1CodecPublisherException("Error parsing or publishing data.", e);
         }
      } while (status == ParserStatus.COMPLETE);
   }

   private void publish(XmlUtils xmlUtils) throws JsonProcessingException {

      OdeMsgPayload msgPayload;
      OdeLogMetadata msgMetadata;
      OdeData msgData;
      
      if (fileParser instanceof DriverAlertFileParser){
         logger.debug("Publishing a driverAlert.");
         msgPayload = new OdeDriverAlertPayload(((DriverAlertFileParser) fileParser).getAlert());
         
         ReceivedMessageDetails receivedMsgDetails = TimDecoderHelper.buildReceivedMessageDetails((TimLogFileParser) fileParser);
         msgMetadata = new OdeDriverAlertMetadata(msgPayload, receivedMsgDetails);

         msgMetadata.getSerialId().setBundleId(bundleId.get()).addRecordId(1);
         OdeLogMetadataCreatorHelper.updateLogMetadata(msgMetadata, fileParser);
         
         msgData = new OdeDriverAlertData(msgMetadata, msgPayload);
         publisher.publish(JsonUtils.toJson(msgData, false),
            publisher.getOdeProperties().getKafkaTopicDriverAlertJson());
      } else {
         msgPayload = new OdeAsn1Payload(fileParser.getPayload());
         OdeAsn1Metadata asn1Metadata;
         if (fileParser instanceof BsmLogFileParser) {
            logger.debug("Publishing a BSM");
            asn1Metadata = new OdeAsn1WithBsmMetadata(msgPayload);
         } else {
            logger.debug("Publishing a TIM");
            asn1Metadata = new OdeAsn1Metadata(msgPayload);
         }
         Asn1Encoding msgEncoding = new Asn1Encoding("root", "Ieee1609Dot2Data", EncodingRule.COER);
         Asn1Encoding unsecuredDataEncoding = new Asn1Encoding("unsecuredData", "MessageFrame",
                 EncodingRule.UPER);
         asn1Metadata.addEncoding(msgEncoding).addEncoding(unsecuredDataEncoding);
         msgMetadata = asn1Metadata;

         msgMetadata.getSerialId().setBundleId(bundleId.get()).addRecordId(1);
         OdeLogMetadataCreatorHelper.updateLogMetadata(msgMetadata, fileParser);
         
         msgData = new OdeAsn1Data(msgMetadata, msgPayload);
         publisher.publish(xmlUtils.toXml(msgData),
            publisher.getOdeProperties().getKafkaTopicAsn1DecoderInput());
      }
   }

   @Override
   public void publish(byte[] payloadBytes) throws Exception {
      OdeAsn1Payload payload = new OdeAsn1Payload(payloadBytes);
      OdeAsn1Metadata metadata = new OdeAsn1Metadata(payload);
      metadata.getSerialId().setBundleId(bundleId.get()).addRecordId(1);

      Asn1Encoding msgEncoding = new Asn1Encoding("root", "MessageFrame", EncodingRule.UPER);
      metadata.addEncoding(msgEncoding);
      OdeAsn1Data asn1Data = new OdeAsn1Data(metadata, payload);

      // publisher.publish(asn1Data.toJson(false),
      // publisher.getOdeProperties().getKafkaTopicAsn1EncodedBsm());
      publisher.publish(XmlUtils.toXmlS(asn1Data), publisher.getOdeProperties().getKafkaTopicAsn1DecoderInput());
   }
}
