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
import java.util.List;
import us.dot.its.jpo.ode.coder.stream.LogFileToAsn1CodecPublisher.LogFileToAsn1CodecPublisherException;
import us.dot.its.jpo.ode.importer.ImporterFileType;
import us.dot.its.jpo.ode.importer.parser.LogFileParser;
import us.dot.its.jpo.ode.importer.parser.LogFileParserFactory;
import us.dot.its.jpo.ode.model.OdeData;
import us.dot.its.jpo.ode.model.OdeLogMetadata;
import us.dot.its.jpo.ode.model.OdeMsgPayload;
import us.dot.its.jpo.ode.model.OdeObject;

/**
 * The Asn1CodecPublisher interface defines a method for processing and publishing
 * data from input streams that represent various file types containing ODE data.
 */
public interface Asn1CodecPublisher {

  List<OdeData<OdeLogMetadata, OdeMsgPayload<OdeObject>>> publish(BufferedInputStream bis, 
      String fileName, ImporterFileType fileType, LogFileParser fileParser)
      throws LogFileToAsn1CodecPublisherException, LogFileParserFactory.LogFileParserFactoryException;
}
