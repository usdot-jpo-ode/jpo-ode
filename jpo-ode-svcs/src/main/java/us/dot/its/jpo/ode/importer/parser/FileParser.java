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

public interface FileParser {

   public static class FileParserException extends Exception {
      public FileParserException(String msg) {
         super(msg);
      }

      public FileParserException(String msg, Exception e) {
         super(msg, e);
      }

      private static final long serialVersionUID = 1L;

   }

   public enum ParserStatus {
      UNKNOWN, INIT, NA, PARTIAL, COMPLETE, EOF, ERROR
   }

   public ParserStatus parseFile(BufferedInputStream bis, String fileName) throws FileParserException;
   public void writeTo(OutputStream os) throws IOException;
}
