package us.dot.its.jpo.ode.coder.stream;

import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import us.dot.its.jpo.ode.coder.OdeLogMetadataCreatorHelper;
import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher.ImporterFileType;
import us.dot.its.jpo.ode.importer.parser.BsmLogFileParser;
import us.dot.its.jpo.ode.importer.parser.DriverAlertFileParser;
import us.dot.its.jpo.ode.importer.parser.FileParser.ParserStatus;
import us.dot.its.jpo.ode.importer.parser.LogFileParser;
import us.dot.its.jpo.ode.importer.parser.RxMsgFileParser;
import us.dot.its.jpo.ode.model.Asn1Encoding;
import us.dot.its.jpo.ode.model.Asn1Encoding.EncodingRule;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeAsn1Payload;
import us.dot.its.jpo.ode.model.OdeBsmMetadata;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.OdeDriverAlertData;
import us.dot.its.jpo.ode.model.OdeDriverAlertPayload;
import us.dot.its.jpo.ode.model.OdeLogMetadata;
import us.dot.its.jpo.ode.model.OdeMsgPayload;
import us.dot.its.jpo.ode.model.RxSource;
import us.dot.its.jpo.ode.model.SerialId;
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
   protected SerialId serialId;

   public LogFileToAsn1CodecPublisher(StringPublisher dataPub) {
      this.publisher = dataPub;
      this.serialId = new SerialId();
   }

   public void publish(BufferedInputStream bis, String fileName, ImporterFileType fileType) 
         throws LogFileToAsn1CodecPublisherException {
      XmlUtils xmlUtils = new XmlUtils();
      ParserStatus status = ParserStatus.UNKNOWN;

      if (fileType == ImporterFileType.OBU_LOG_FILE) {
         fileParser = LogFileParser.factory(fileName);
      } else {
         status = ParserStatus.NA;
      }

      List<OdeMsgPayload> payloadList = new ArrayList<OdeMsgPayload>();
      do {
         try {
            status = fileParser.parseFile(bis, fileName);
            if (status == ParserStatus.COMPLETE) {
               parsePayload(payloadList);
            } else if (status == ParserStatus.EOF) {
               publish(xmlUtils, payloadList);
               
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

   public void parsePayload(List<OdeMsgPayload> payloadList) {

      OdeMsgPayload msgPayload;
      
      if (fileParser instanceof DriverAlertFileParser){
         msgPayload = new OdeDriverAlertPayload(((DriverAlertFileParser) fileParser).getAlert());
      } else {
         msgPayload = new OdeAsn1Payload(fileParser.getPayloadParser().getPayload());
      }

      payloadList.add(msgPayload);
   }

   public void publish(XmlUtils xmlUtils, List<OdeMsgPayload> payloadList) throws JsonProcessingException {
     serialId.setBundleSize(payloadList.size());
     for (OdeMsgPayload msgPayload : payloadList) {
       OdeLogMetadata msgMetadata;
       OdeData msgData;
       
       if (fileParser instanceof DriverAlertFileParser){
          logger.debug("Publishing a driverAlert.");

          msgMetadata = new OdeLogMetadata(msgPayload);
          msgMetadata.setSerialId(serialId);

          OdeLogMetadataCreatorHelper.updateLogMetadata(msgMetadata, fileParser);
          
          msgData = new OdeDriverAlertData(msgMetadata, msgPayload);
          publisher.publish(JsonUtils.toJson(msgData, false),
             publisher.getOdeProperties().getKafkaTopicDriverAlertJson());
          serialId.increment();
       } else {
          if (fileParser instanceof BsmLogFileParser || 
                (fileParser instanceof RxMsgFileParser && ((RxMsgFileParser)fileParser).getRxSource() == RxSource.RV)) {
             logger.debug("Publishing a BSM");
             msgMetadata = new OdeBsmMetadata(msgPayload);
          } else {
             logger.debug("Publishing a TIM");
             msgMetadata = new OdeLogMetadata(msgPayload);
          }
          msgMetadata.setSerialId(serialId);

          Asn1Encoding msgEncoding = new Asn1Encoding("root", "Ieee1609Dot2Data", EncodingRule.COER);
          Asn1Encoding unsecuredDataEncoding = new Asn1Encoding("unsecuredData", "MessageFrame",
                  EncodingRule.UPER);
          msgMetadata.addEncoding(msgEncoding).addEncoding(unsecuredDataEncoding);

          OdeLogMetadataCreatorHelper.updateLogMetadata(msgMetadata, fileParser);
          
          msgData = new OdeAsn1Data(msgMetadata, msgPayload);
          publisher.publish(xmlUtils.toXml(msgData),
             publisher.getOdeProperties().getKafkaTopicAsn1DecoderInput());
          serialId.increment();
       }
     }
   }


}
