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
import java.io.Serial;

/**
 * Interface for processing files uploaded by OBUs for offline batch processing.
 */
public interface FileParser {

  /**
   * Custom exception thrown when a file parsing operation fails.
   * This exception is designed to handle errors specific to the
   * file parsing process implemented in the file parsers.
   */
  class FileParserException extends Exception {
    public FileParserException(String msg) {
      super(msg);
    }

    public FileParserException(String msg, Exception e) {
      super(msg, e);
    }

    @Serial
    private static final long serialVersionUID = 1L;
  }

  /**
   * Represents the status of a parsing operation in the file parsing process.
   * This enumeration is used to track and manage the progression of parsing
   * performed by implementations of the FileParser interface.
   *
   * <p>The possible states are:
   * <ul>
   *   <li>UNKNOWN: The parser status is not determined or has not been initialized.</li>
   *   <li>INIT: The parser is initialized and ready to begin processing.</li>
   *   <li>NA: The parser is unable to determine the status or the input is deemed not applicable.</li>
   *   <li>PARTIAL: The parsing is in progress but not yet complete.</li>
   *   <li>COMPLETE: The parsing has successfully completed.</li>
   *   <li>EOF: End of file is reached during parsing.</li>
   *   <li>ERROR: An error has occurred during the parsing process.</li>
   * </ul>
   */
  enum ParserStatus {
    UNKNOWN, INIT, NA, PARTIAL, COMPLETE, EOF, ERROR
  }

  ParserStatus parseFile(BufferedInputStream bis) throws FileParserException;

  public void writeTo(OutputStream os) throws IOException;
}
