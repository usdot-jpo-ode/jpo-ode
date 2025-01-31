/*=============================================================================
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

package us.dot.its.jpo.ode.coder.stream;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.ode.coder.StringPublisher;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher.ImporterFileType;
import us.dot.its.jpo.ode.importer.parser.BsmLogFileParser;
import us.dot.its.jpo.ode.importer.parser.DriverAlertFileParser;
import us.dot.its.jpo.ode.importer.parser.FileParser.ParserStatus;
import us.dot.its.jpo.ode.importer.parser.LogFileParser;
import us.dot.its.jpo.ode.importer.parser.LogFileParserFactory;
import us.dot.its.jpo.ode.importer.parser.RxMsgFileParser;
import us.dot.its.jpo.ode.importer.parser.SpatLogFileParser;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;
import us.dot.its.jpo.ode.model.OdeAsn1Data;
import us.dot.its.jpo.ode.model.OdeAsn1Payload;
import us.dot.its.jpo.ode.model.OdeBsmMetadata;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.OdeDriverAlertData;
import us.dot.its.jpo.ode.model.OdeDriverAlertPayload;
import us.dot.its.jpo.ode.model.OdeLogMetadata;
import us.dot.its.jpo.ode.model.OdeMsgPayload;
import us.dot.its.jpo.ode.model.OdeSpatMetadata;
import us.dot.its.jpo.ode.model.RxSource;
import us.dot.its.jpo.ode.model.SerialId;
import us.dot.its.jpo.ode.uper.UperUtil;
import us.dot.its.jpo.ode.util.JsonUtils;

/**
 * The LogFileToAsn1CodecPublisher class is responsible for processing log files
 * and publishing data in ASN.1 encoded format to Kafka topics. It serves as an implementation
 * of the Asn1CodecPublisher interface.
 *
 * <p>This class handles different file types and decoding processes, converting them into ODE metadata
 * and payload models for publishing. It can also differentiate between various record types, such as
 * Driver Alert, BSM (Basic Safety Message), and SPAT (Signal Phase and Timing), and directs the data
 * to the appropriate topics defined in RawEncodedJsonTopics or JsonTopics.</p>
 */
@Slf4j
public class LogFileToAsn1CodecPublisher implements Asn1CodecPublisher {

  /**
   * This exception is thrown to indicate an error that occurred while processing
   * a log file to be published via the ASN.1 codec. It acts as a wrapper for
   * exceptions that arise in this context, providing additional context through
   * a descriptive message and the underlying cause.
   *
   * <p>The exception is typically used in scenarios where log files are parsed,
   * converted, or otherwise processed to be published in ASN.1 format, and a
   * failure in this process needs appropriate propagation and handling.</p>
   */
  public static class LogFileToAsn1CodecPublisherException extends Exception {

    private static final long serialVersionUID = 1L;

    public LogFileToAsn1CodecPublisherException(String string, Exception e) {
      super(string, e);
    }

  }

  private final RawEncodedJsonTopics rawEncodedJsonTopics;
  private final JsonTopics jsonTopics;
  private final StringPublisher publisher;
  private final SerialId serialId;

  /**
   * Constructs a LogFileToAsn1CodecPublisher instance used for converting log files
   * to ASN.1 encoded formats and publishing them. It integrates with given
   * publishers and topic configurations for data processing and dissemination.
   *
   * @param stringPublisher      the publisher responsible for publishing the encoded strings
   * @param jsonTopics           the topic configuration for processing JSON data
   * @param rawEncodedJsonTopics the topic configuration for processing raw encoded JSON data
   */
  public LogFileToAsn1CodecPublisher(StringPublisher stringPublisher, JsonTopics jsonTopics, RawEncodedJsonTopics rawEncodedJsonTopics) {
    this.jsonTopics = jsonTopics;
    this.rawEncodedJsonTopics = rawEncodedJsonTopics;
    this.publisher = stringPublisher;
    this.serialId = new SerialId();
  }

  /**
   * Publishes data parsed from a given input stream based on the specified file type.
   * The method processes the input stream, parses the data using specific parsers
   * depending on the file type, and publishes the parsed data. Handles both complete
   * and incomplete data scenarios and maintains a list of all successfully parsed data.
   *
   * @param inputStream the input stream containing the data to be parsed
   * @param fileName    the name of the file associated with the data in the input stream
   * @param fileType    the type of file being processed, which determines the parser to be used
   *
   * @return a list of {@code OdeData} objects that have been successfully parsed
   *
   * @throws LogFileToAsn1CodecPublisherException               if an error occurs while parsing or publishing the data
   * @throws LogFileParserFactory.LogFileParserFactoryException if an error occurs while creating the file parser
   */
  public List<OdeData> publish(BufferedInputStream inputStream, String fileName, ImporterFileType fileType, LogFileParser fileParser)
      throws LogFileToAsn1CodecPublisherException, LogFileParserFactory.LogFileParserFactoryException {
    ParserStatus status;

    List<OdeData> dataList = new ArrayList<>();
    if (fileType == ImporterFileType.LOG_FILE) {

      do {
        try {
          status = fileParser.parseFile(inputStream);
          switch (status) {
            case ParserStatus.COMPLETE -> addDataToList(dataList, fileParser);
            case ParserStatus.EOF -> publishList(dataList, fileParser);
            case ParserStatus.INIT -> log.error("Failed to parse the header bytes.");
            default -> log.error("Failed to decode ASN.1 data");
          }
          inputStream = removeNextNewLineCharacter(inputStream);
        } catch (Exception e) {
          throw new LogFileToAsn1CodecPublisherException("Error parsing or publishing data.", e);
        }
      } while (status == ParserStatus.COMPLETE);
    }

    return dataList;
  }

  private void addDataToList(List<OdeData> dataList, LogFileParser fileParser) {

    OdeData odeData;

    OdeMsgPayload payload;
    OdeLogMetadata metadata;
    if (isDriverAlertRecord(fileParser)) {
      payload = new OdeDriverAlertPayload(((DriverAlertFileParser) fileParser).getAlert());
      metadata = new OdeLogMetadata(payload);
      odeData = new OdeDriverAlertData(metadata, payload);
    } else if (isBsmRecord(fileParser)) {
      payload = new OdeAsn1Payload(fileParser.getPayloadParser().getPayload());
      metadata = new OdeBsmMetadata(payload);
      odeData = new OdeAsn1Data(metadata, payload);
    } else if (isSpatRecord(fileParser)) {
      payload = new OdeAsn1Payload(fileParser.getPayloadParser().getPayload());
      metadata = new OdeSpatMetadata(payload);
      odeData = new OdeAsn1Data(metadata, payload);
    } else {
      payload = new OdeAsn1Payload(fileParser.getPayloadParser().getPayload());
      metadata = new OdeLogMetadata(payload);
      odeData = new OdeAsn1Data(metadata, payload);
    }
    fileParser.updateMetadata(metadata);

    dataList.add(odeData);
  }

  public boolean isDriverAlertRecord(LogFileParser fileParser) {
    return fileParser instanceof DriverAlertFileParser;
  }

  public boolean isBsmRecord(LogFileParser fileParser) {
    return fileParser instanceof BsmLogFileParser || (fileParser instanceof RxMsgFileParser
        && ((RxMsgFileParser) fileParser).getRxSource() == RxSource.RV);
  }

  public boolean isSpatRecord(LogFileParser fileParser) {
    return fileParser instanceof SpatLogFileParser;
  }

  private void publishList(List<OdeData> dataList, LogFileParser fileParser) {
    serialId.setBundleSize(dataList.size());

    for (OdeData odeData : dataList) {
      OdeLogMetadata msgMetadata = (OdeLogMetadata) odeData.getMetadata();
      OdeMsgPayload msgPayload = odeData.getPayload();
      msgMetadata.setSerialId(serialId);

      if (isDriverAlertRecord(fileParser)) {
        publisher.publish(jsonTopics.getDriverAlert(), JsonUtils.toJson(odeData, false));
      } else if (isBsmRecord(fileParser)) {
        publisher.publish(rawEncodedJsonTopics.getBsm(), JsonUtils.toJson(odeData, false));
      } else if (isSpatRecord(fileParser)) {
        publisher.publish(rawEncodedJsonTopics.getSpat(), JsonUtils.toJson(odeData, false));
      } else {
        String messageType = UperUtil.determineMessageType(msgPayload);
        switch (messageType) {
          case "MAP" -> publisher.publish(rawEncodedJsonTopics.getMap(), JsonUtils.toJson(odeData, false));
          case "SPAT" -> publisher.publish(rawEncodedJsonTopics.getSpat(), JsonUtils.toJson(odeData, false));
          case "TIM" -> publisher.publish(rawEncodedJsonTopics.getTim(), JsonUtils.toJson(odeData, false));
          case "BSM" -> publisher.publish(rawEncodedJsonTopics.getBsm(), JsonUtils.toJson(odeData, false));
          case "SSM" -> publisher.publish(rawEncodedJsonTopics.getSsm(), JsonUtils.toJson(odeData, false));
          case "SRM" -> publisher.publish(rawEncodedJsonTopics.getSrm(), JsonUtils.toJson(odeData, false));
          case "PSM" -> publisher.publish(rawEncodedJsonTopics.getPsm(), JsonUtils.toJson(odeData, false));
          default -> log.warn("Unknown message type: {}", messageType);
        }
      }

      serialId.increment();
    }
  }

  // This method will check if the next character is a newline character (0x0A in hex or 10 in converted decimal)
  // or if the next character does not contain a newline character it will put that character back into the buffered input stream
  private BufferedInputStream removeNextNewLineCharacter(BufferedInputStream bis) {
    try {
      bis.mark(1);
      int nextByte = bis.read();
      if (nextByte != 10) { // If the next byte is not a newline
        bis.reset(); // Reset the stream back to the most recent mark
      }
    } catch (IOException e) {
      log.error("Error removing next newline character: ", e);
    }
    return bis;
  }
}
