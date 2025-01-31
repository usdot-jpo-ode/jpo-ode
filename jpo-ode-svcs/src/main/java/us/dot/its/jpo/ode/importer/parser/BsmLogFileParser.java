/*===========================================================================
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
import us.dot.its.jpo.ode.model.OdeBsmMetadata.BsmSource;
import us.dot.its.jpo.ode.model.OdeLogMetadata;

/**
 * This class is responsible for parsing Basic Safety Message (BSM) log files. It extends
 * {@code LogFileParser} and provides specific parsing logic for BSM data, including metadata
 * handling and extraction of BSM source and relevant information.
 */
public class BsmLogFileParser extends LogFileParser {
  private static final Logger logger = LoggerFactory.getLogger(BsmLogFileParser.class);

  private static final int DIRECTION_LENGTH = 1;

  private BsmSource bsmSource; // 0 for EV(Tx), 1 for RV(Rx)

  /**
   * Constructs a new instance of BsmLogFileParser with the specified record type.
   * Initializes specific parsers for location, time, security result code, and payload,
   * and sets the record type for the parser.
   *
   * @param recordType the type of record to be parsed, as specified by the {@link OdeLogMetadata.RecordType} enumeration.
   * @param filename   the name of the file to be parsed
   */
  public BsmLogFileParser(OdeLogMetadata.RecordType recordType, String filename) {
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

      if (getStep() == 1) {
        status = parseStep(bis, DIRECTION_LENGTH);
        if (status != ParserStatus.COMPLETE) {
          return status;
        }
        setBsmSource(readBuffer);
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
      throw new FileParserException("Error parsing " + getFilename(), e);
    }

    return status;
  }

  public BsmSource getBsmSource() {
    return bsmSource;
  }

  public void setBsmSource(BsmSource bsmSource) {
    this.bsmSource = bsmSource;
  }

  protected void setBsmSource(byte[] code) {
    try {
      setBsmSource(BsmSource.values()[code[0]]);

    } catch (Exception e) {
      logger.error("Invalid BsmSource: {}. Valid values are {}-{} inclusive",
          code, 0, BsmSource.values());
      setBsmSource(BsmSource.unknown);
    }
  }

  @Override
  public void writeTo(OutputStream os) throws IOException {
    os.write((byte) bsmSource.ordinal());
    super.writeTo(os);
  }
}
