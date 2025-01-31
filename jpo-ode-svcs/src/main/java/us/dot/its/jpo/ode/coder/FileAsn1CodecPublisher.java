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

package us.dot.its.jpo.ode.coder;

import java.io.BufferedInputStream;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.ode.coder.stream.LogFileToAsn1CodecPublisher;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher.ImporterFileType;
import us.dot.its.jpo.ode.importer.parser.LogFileParserFactory;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.kafka.topics.JsonTopics;
import us.dot.its.jpo.ode.kafka.topics.RawEncodedJsonTopics;

/**
 * A class responsible for publishing files to an ASN.1 codec format via a Kafka publisher.
 * It provides functionality for handling different types of importer files and
 * integrates with a codec publisher to process and publish the file content.
 */
@Slf4j
public class FileAsn1CodecPublisher {

  /**
   * Represents an exception specific to the failure of publishing files to an ASN.1 codec format.
   * This exception is thrown to indicate errors encountered during the publishing process
   * within the {@link FileAsn1CodecPublisher} class.
   *
   * <p>This exception primarily catches and wraps underlying exceptions that occur, providing
   * additional context about the error related to the file publishing operation.</p>
   */
  public static class FileAsn1CodecPublisherException extends Exception {

    private static final long serialVersionUID = 1L;

    public FileAsn1CodecPublisherException(String string, Exception e) {
      super(string, e);
    }

  }

  private final LogFileToAsn1CodecPublisher codecPublisher;

  /**
   * Constructs a {@code FileAsn1CodecPublisher}.
   * This constructor initializes the codec publisher using the provided Kafka properties,
   * JSON topics, and raw encoded JSON topics.
   *
   * @param odeKafkaProperties   the Kafka properties configured with brokers, disabled topics, and Kafka type,
   *                             used to initialize the message publisher.
   * @param jsonTopics           the JSON topics containing the mappings for various topic names used
   *                             in publishing data.
   * @param rawEncodedJsonTopics the raw encoded JSON topics containing the mappings for raw
   *                             encoded topic names used in publishing data.
   */
  public FileAsn1CodecPublisher(OdeKafkaProperties odeKafkaProperties, JsonTopics jsonTopics, RawEncodedJsonTopics rawEncodedJsonTopics) {
    StringPublisher messagePub = new StringPublisher(odeKafkaProperties.getBrokers(),
        odeKafkaProperties.getKafkaType(),
        odeKafkaProperties.getDisabledTopics());

    this.codecPublisher = new LogFileToAsn1CodecPublisher(messagePub, jsonTopics, rawEncodedJsonTopics);
  }

  /**
   * Parses a file into ASN.1 format using a file parser determined from the filePath.
   * The method processes the file from the given input stream, parses the data
   * based on the file type, and then publishes it through a codec publisher.
   *
   * @param filePath          the path of the file to be published
   * @param fileInputStream   the input stream containing the file data to be published
   * @param fileType          the type of the file being published, defining the parser and processing logic
   *
   * @throws FileAsn1CodecPublisherException if an error occurs during the file publication process
   */
  public void publishFile(Path filePath, BufferedInputStream fileInputStream, ImporterFileType fileType)
      throws FileAsn1CodecPublisherException {
    String fileName = filePath.toFile().getName();

    log.info("Publishing file {}", fileName);

    try {
      log.info("Publishing data from {} to asn1_codec.", filePath);
      codecPublisher.publish(fileInputStream, fileName, fileType, LogFileParserFactory.getLogFileParser(fileName));
    } catch (Exception e) {
      throw new FileAsn1CodecPublisherException("Failed to publish file.", e);
    }
  }

}
