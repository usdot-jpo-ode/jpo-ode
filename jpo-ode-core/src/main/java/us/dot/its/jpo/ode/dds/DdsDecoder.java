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
import javax.websocket.EndpointConfig;

import us.dot.its.jpo.ode.wrapper.WebSocketMessageDecoder;

public abstract class DdsDecoder implements WebSocketMessageDecoder<DdsMessage> {

   public static class DdsDecoderException extends Exception {

      private static final long serialVersionUID = -4474047515873708804L;

      public DdsDecoderException(String string) {
         super(string);
      }

   }

   @Override
   public void init(EndpointConfig endpointConfig) {
   }

   @Override
   public void destroy() {
   }

   @Override
   public abstract DdsMessage decode(String message) throws DecodeException;

}
