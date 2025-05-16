/*==============================================================================
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
import us.dot.its.jpo.ode.model.OdeLogMetadata;

/**
 * DistressMsgFileParser extends the {@link LogFileParser} abstract class and provides functionality
 * to parse log files containing Distress TIMs. It defines * steps to parse specific components like
 * location, time, security result codes, and payload data within the file.
 *
 * <p>Note: There doesn't seem to be any reference to Distress Notifications in the 2024 spec,
 * so it's likely this message type is no longer supported. We may consider deprecating this parser in the future.
 */
public class DistressMsgFileParser extends LogFileParser {

  /**
   * Initializes a new instance of the DistressMsgFileParser class. This parser is used
   * to parse distress messages from log files while breaking down components such as
   * location, time, security result codes, and payload data.
   *
   * @param recordType the {@link OdeLogMetadata.RecordType} representing the type of
   *                   record to be parsed. This helps the parser determine message context
   *                   and apply proper parsing logic.
   * @param filename   the name of the file to be parsed
   */
  public DistressMsgFileParser(OdeLogMetadata.RecordType recordType, String filename) {
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
      if (status != ParserStatus.ENTRY_PARSING_COMPLETE) {
        return status;
      }

      if (getStep() == 1) {
        status = nextStep(bis, locationParser);
        if (status != ParserStatus.ENTRY_PARSING_COMPLETE) {
          return status;
        }
      }

      if (getStep() == 2) {
        status = nextStep(bis, timeParser);
        if (status != ParserStatus.ENTRY_PARSING_COMPLETE) {
          return status;
        }
      }

      if (getStep() == 3) {
        status = nextStep(bis, secResCodeParser);
        if (status != ParserStatus.ENTRY_PARSING_COMPLETE) {
          return status;
        }
      }

      if (getStep() == 4) {
        status = nextStep(bis, payloadParser);
        if (status != ParserStatus.ENTRY_PARSING_COMPLETE) {
          return status;
        }
      }

      resetStep();
      status = ParserStatus.ENTRY_PARSING_COMPLETE;

    } catch (Exception e) {
      throw new FileParserException(String.format("Error parsing %s on step %d", getFilename(), getStep()), e);
    }

    return status;

  }
}
