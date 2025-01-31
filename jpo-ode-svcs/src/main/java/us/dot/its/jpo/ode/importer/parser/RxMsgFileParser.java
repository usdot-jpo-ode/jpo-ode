/*============================================================================
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.dot.its.jpo.ode.model.OdeLogMetadata;
import us.dot.its.jpo.ode.model.RxSource;


/**
 * RxMsgFileParser is a specialized implementation of the LogFileParser class
 * designed to parse "Received Message" log files. It provides step-by-step
 * parsing of various components of the log file, including the RX source,
 * location, time, security result code, and payload sections. Each step
 * corresponds to a specific component being parsed, and the parser transitions
 * between steps based on the success of parsing operations.
 */
public class RxMsgFileParser extends LogFileParser {

  private static final Logger logger = LoggerFactory.getLogger(RxMsgFileParser.class);

  private static final int RX_SOURCE_LENGTH = 1;

  private RxSource rxSource;

  /**
   * Constructs a new instance of RxMsgFileParser for parsing received message logs.
   * Initializes the location, time, security result code, and payload parsers
   * and sets the record type for the parser.
   *
   * @param recordType the type of record being parsed, which specifies the applicable parsing logic.
   * @param filename   the name of the file to be parsed
   */
  public RxMsgFileParser(OdeLogMetadata.RecordType recordType, String filename) {
    super(recordType, filename);
    setLocationParser(new LocationParser(recordType, filename));
    setTimeParser(new TimeParser(recordType, filename));
    setSecResCodeParser(new SecurityResultCodeParser(recordType, filename));
    setPayloadParser(new PayloadParser(recordType, filename));
  }

  @Override
  public ParserStatus parseFile(BufferedInputStream bis) throws FileParserException {

    ParserStatus status;
    try {
      status = super.parseFile(bis);
      if (status != ParserStatus.COMPLETE) {
        return status;
      }

      // parse rxSource
      if (getStep() == 1) {
        status = parseStep(bis, RX_SOURCE_LENGTH);
        if (status != ParserStatus.COMPLETE) {
          return status;
        }
        try {
          setRxSource(readBuffer[0]);
        } catch (Exception e) {
          setRxSource(RxSource.UNKNOWN);
        }
      }

      if (getStep() == 2) {
        status = nextStep(bis, locationParser);
        if (status != ParserStatus.COMPLETE) {
          return status;
        }
      }

      if (getStep() == 3) {
        status = nextStep(bis, timeParser);
        if (status != ParserStatus.COMPLETE) {
          return status;
        }
      }

      if (getStep() == 4) {
        status = nextStep(bis, secResCodeParser);
        if (status != ParserStatus.COMPLETE) {
          return status;
        }
      }

      if (getStep() == 5) {
        status = nextStep(bis, payloadParser);
        if (status != ParserStatus.COMPLETE) {
          return status;
        }
      }

      resetStep();
      status = ParserStatus.COMPLETE;

    } catch (Exception e) {
      throw new FileParserException(String.format("Error parsing %s on step %d", getFilename(), getStep()), e);
    }

    return status;

  }

  public RxSource getRxSource() {
    return rxSource;
  }

  public void setRxSource(RxSource rxSource) {
    logger.debug("rxSourceOrdinal value: {}", rxSource);
    this.rxSource = rxSource;
  }

  protected void setRxSource(int rxSourceOrdinal) {
    try {
      setRxSource(RxSource.values()[rxSourceOrdinal]);
    } catch (Exception e) {
      logger.error("Invalid RxSource: {}. Valid values are {}: ",
          rxSourceOrdinal, RxSource.values());
      setRxSource(RxSource.UNKNOWN);
    }
  }

  @Override
  public void writeTo(OutputStream os) throws IOException {
    os.write((byte) rxSource.ordinal());
    super.writeTo(os);
  }
}
