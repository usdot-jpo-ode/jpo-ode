package us.dot.its.jpo.ode.importer.parser;

import java.io.BufferedInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.model.OdeLogMetadata.RecordType;

public abstract class LogFileParser implements FileParser {
   private static final Logger logger = LoggerFactory.getLogger(LogFileParser.class);

   public static final int BUFFER_SIZE = 4096;

   protected transient byte[] readBuffer = new byte[BUFFER_SIZE];
   protected int step = 0;

   protected String filename;
   protected RecordType recordType;

   protected LocationParser locationParser;
   protected TimeParser timeParser;
   protected SecurityResultCodeParser secResCodeParser;
   protected PayloadParser payloadParser;

   public LogFileParser() {
      super();
   }

   public static LogFileParser factory(String fileName) {
      LogFileParser fileParser;
      if (fileName.startsWith(RecordType.bsmTx.name())) {
         logger.debug("Parsing as \"Transmit BSM \" log file type.");
         fileParser = new BsmLogFileParser().setRecordType(RecordType.bsmTx);
      } else if (fileName.startsWith(RecordType.bsmLogDuringEvent.name())) {
         logger.debug("Parsing as \"BSM For Event\" log file type.");
         fileParser = new BsmLogFileParser().setRecordType(RecordType.bsmLogDuringEvent);
      } else if (fileName.startsWith(RecordType.rxMsg.name())) {
         logger.debug("Parsing as \"Received Messages\" log file type.");
         fileParser = new RxMsgFileParser().setRecordType(RecordType.rxMsg);
      } else if (fileName.startsWith(RecordType.dnMsg.name())) {
         logger.debug("Parsing as \"Distress Notifications\" log file type.");
         fileParser = new DistressMsgFileParser().setRecordType(RecordType.dnMsg);
      } else if (fileName.startsWith(RecordType.driverAlert.name())) {
         logger.debug("Parsing as \"Driver Alert\" log file type.");
         fileParser = new DriverAlertFileParser().setRecordType(RecordType.driverAlert);
      } else {
         throw new IllegalArgumentException("Unknown log file prefix: " + fileName);
      }
      return fileParser;
   }

   public ParserStatus parseFile(BufferedInputStream bis, String fileName) 
         throws FileParserException {

      if (getStep() == 0) {
         setFilename(fileName);
         setStep(getStep() + 1);
      }

      return ParserStatus.COMPLETE;
   }

   public ParserStatus parseStep(BufferedInputStream bis, int length) throws FileParserException {
      if (length > BUFFER_SIZE) {
         throw new FileParserException("Data size of " + length 
               + " is larger than allocated buffer size of " + BUFFER_SIZE);
      }
      
      try {
         int numBytes;
         if (bis.markSupported()) {
            bis.mark(length);
         }
         numBytes = bis.read(readBuffer, 0, length);
         if (numBytes < 0) {
            return ParserStatus.EOF;
         } else if (numBytes < length) {
            if (bis.markSupported()) {
               try {
                  bis.reset();
               } catch (IOException ioe) {
                  throw new FileParserException("Error reseting Input Stream to marked position", ioe);
               }
            }
            return ParserStatus.PARTIAL;
         } else {
            step++;
            return ParserStatus.COMPLETE;
         }
      } catch (Exception e) {
         throw new FileParserException("Error parsing step " + step, e);
      }
   }

   public int getStep() {
      return step;
   }

   public LogFileParser setStep(int step) {
      this.step = step;
      return this;
   }

   protected int resetStep() {
      return setStep(0).getStep();
   }
   
   protected ParserStatus nextStep(
      BufferedInputStream bis, 
      String fileName, 
      LogFileParser parser) throws FileParserException {
      
      ParserStatus status = parser.parseFile(bis, fileName);
      if (status == ParserStatus.COMPLETE) {
         step++;
      }
      return status;
   }

   public String getFilename() {
      return filename;
   }

   public LogFileParser setFilename(String filename) {
      this.filename = filename;
      return this;
   }

   public RecordType getRecordType() {
      return recordType;
   }

   public LogFileParser setRecordType(RecordType recordType) {
      this.recordType = recordType;
      return this;
   }

   public LocationParser getLocationParser() {
      return locationParser;
   }

   public void setLocationParser(LocationParser locationParser) {
      this.locationParser = locationParser;
   }

   public TimeParser getTimeParser() {
      return timeParser;
   }

   public void setTimeParser(TimeParser timeParser) {
      this.timeParser = timeParser;
   }

   public SecurityResultCodeParser getSecResCodeParser() {
      return secResCodeParser;
   }

   public void setSecResCodeParser(SecurityResultCodeParser secResCodeParser) {
      this.secResCodeParser = secResCodeParser;
   }

   public PayloadParser getPayloadParser() {
      return payloadParser;
   }

   public void setPayloadParser(PayloadParser payloadParser) {
      this.payloadParser = payloadParser;
   }

}
