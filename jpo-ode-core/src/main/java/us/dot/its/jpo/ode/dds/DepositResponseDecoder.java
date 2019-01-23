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
package us.dot.its.jpo.ode.dds;

import javax.websocket.DecodeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.eventlog.EventLogger;

public class DepositResponseDecoder extends DdsStatusMessageDecoder {

   private static final Logger logger = LoggerFactory
         .getLogger(DepositResponseDecoder.class);

   @Override
   public DdsMessage decode(String message) throws DecodeException {
      DdsMessage statusMsg;
      statusMsg = super.decode(message);
      logger.info("Deposit Response Received: {}", message);
      EventLogger.logger.info("Deposit Response Received: {}", message);

      return statusMsg;
   }

   @Override
   public boolean willDecode(String message) {
      return true;
   }

}
