/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.ode.importer.parser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.model.OdeBsmMetadata;
import us.dot.its.jpo.ode.model.OdeBsmMetadata.BsmSource;
import us.dot.its.jpo.ode.model.OdeLogMetadata;
import us.dot.its.jpo.ode.model.OdeLogMetadata.RecordType;
import us.dot.its.jpo.ode.model.OdeLogMsgMetadataLocation;
import us.dot.its.jpo.ode.model.OdeMsgMetadata.GeneratedBy;
import us.dot.its.jpo.ode.model.ReceivedMessageDetails;
import us.dot.its.jpo.ode.model.RxSource;
import us.dot.its.jpo.ode.plugin.j2735.builders.ElevationBuilder;
import us.dot.its.jpo.ode.plugin.j2735.builders.HeadingBuilder;
import us.dot.its.jpo.ode.plugin.j2735.builders.LatitudeBuilder;
import us.dot.its.jpo.ode.plugin.j2735.builders.LongitudeBuilder;
import us.dot.its.jpo.ode.plugin.j2735.builders.SpeedOrVelocityBuilder;
import us.dot.its.jpo.ode.util.DateTimeUtils;

public abstract class LogFileParser implements FileParser {
   private static final Logger logger = LoggerFactory.getLogger(LogFileParser.class);

   public static final int BUFFER_SIZE = 4096;

   protected byte[] readBuffer = new byte[BUFFER_SIZE];
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

      ParserStatus status = ParserStatus.INIT;
      if (getStep() == 0) {
         setFilename(fileName);
         setStep(getStep() + 1);
         status = ParserStatus.COMPLETE;
      }

      return status;
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

  public void updateMetadata(OdeLogMetadata metadata) {
      metadata.setLogFileName(getFilename());
      metadata.setRecordType(getRecordType());
      metadata.setRecordGeneratedAt(DateTimeUtils.isoDateTime(getTimeParser().getGeneratedAt()));

      if (getSecResCodeParser() != null) {
        metadata.setSecurityResultCode(getSecResCodeParser().getSecurityResultCode());
      }

      metadata.setReceivedMessageDetails(buildReceivedMessageDetails(this));

      if (metadata instanceof OdeBsmMetadata) {
        OdeBsmMetadata odeBsmMetadata = (OdeBsmMetadata) metadata;
        BsmSource bsmSource = BsmSource.unknown;
        if (this instanceof BsmLogFileParser) {
          BsmLogFileParser bsmLogFileParser = (BsmLogFileParser) this;
          bsmSource = bsmLogFileParser.getBsmSource();
        } else if (this instanceof RxMsgFileParser) {
          RxMsgFileParser rxMsgFileParser = (RxMsgFileParser) this;
          if (rxMsgFileParser.getRxSource() == RxSource.RV) {
            bsmSource = BsmSource.RV;
          }
        }
        odeBsmMetadata.setBsmSource(bsmSource);
      }

      ReceivedMessageDetails receivedMessageDetails = metadata.getReceivedMessageDetails();
      if (receivedMessageDetails != null) {
        if (receivedMessageDetails.getRxSource() != null) {
          switch (receivedMessageDetails.getRxSource()) {
          case RSU:
            metadata.setRecordGeneratedBy(GeneratedBy.RSU);
            break;
          case RV:
          case NA:
            metadata.setRecordGeneratedBy(GeneratedBy.OBU);
            break;
          case SAT:
            metadata.setRecordGeneratedBy(GeneratedBy.TMC_VIA_SAT);
            break;
          case SNMP:
            metadata.setRecordGeneratedBy(GeneratedBy.TMC_VIA_SNMP);
            break;
          default:
            metadata.setRecordGeneratedBy(GeneratedBy.UNKNOWN);
            break;
          }
        } else {
          receivedMessageDetails.setRxSource(RxSource.UNKNOWN);
        }
      } else {
        metadata.setRecordGeneratedBy(GeneratedBy.OBU);
      }
  }
  
  private static ReceivedMessageDetails buildReceivedMessageDetails(LogFileParser parser) {
    LocationParser locationParser = parser.getLocationParser();
    ReceivedMessageDetails rxMsgDetails = null;
    if (locationParser != null) {
       LogLocation locationDetails = locationParser.getLocation();
       BigDecimal genericLatitude = LatitudeBuilder.genericLatitude(locationDetails.getLatitude());
       BigDecimal genericLongitude = LongitudeBuilder.genericLongitude(locationDetails.getLongitude());
       BigDecimal genericElevation = ElevationBuilder.genericElevation(locationDetails.getElevation());
       BigDecimal genericSpeedOrVelocity = SpeedOrVelocityBuilder.genericSpeedOrVelocity(locationDetails.getSpeed());
       BigDecimal genericHeading = HeadingBuilder.genericHeading(locationDetails.getHeading());
       rxMsgDetails = new ReceivedMessageDetails(
             new OdeLogMsgMetadataLocation(
                genericLatitude == null ? null : genericLatitude.stripTrailingZeros().toPlainString(),
                genericLongitude == null ? null : genericLongitude.stripTrailingZeros().toPlainString(),
                genericElevation == null ? null : genericElevation.stripTrailingZeros().toPlainString(),
                genericSpeedOrVelocity == null ? null : genericSpeedOrVelocity.stripTrailingZeros().toPlainString(),
                genericHeading == null ? null : genericHeading.stripTrailingZeros().toPlainString()
                   ), null);
    }
    
    if (rxMsgDetails != null) {
      if (parser instanceof RxMsgFileParser) {
        RxMsgFileParser rxMsgFileParser = (RxMsgFileParser) parser;
        rxMsgDetails.setRxSource(rxMsgFileParser.getRxSource());
      } else {
        rxMsgDetails.setRxSource(RxSource.NA);
      }
    }
    
    return rxMsgDetails; 
  }

  public void writeTo(OutputStream os) throws IOException {
    locationParser.writeTo(os);
    timeParser.writeTo(os);
    secResCodeParser.writeTo(os);
    payloadParser.writeTo(os);
  }
}
