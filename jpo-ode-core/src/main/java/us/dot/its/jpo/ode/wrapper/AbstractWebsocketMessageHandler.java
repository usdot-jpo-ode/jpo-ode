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
package us.dot.its.jpo.ode.wrapper;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

import us.dot.its.jpo.ode.model.OdeMessage;

public abstract class AbstractWebsocketMessageHandler<MessageType> 
implements WebSocketMessageHandler<MessageType> {
   protected WebSocketClient client;

   public AbstractWebsocketMessageHandler() {
      super();
   }

   public AbstractWebsocketMessageHandler(WebSocketClient client) {
      super();
      this.client = client;
   }

   public WebSocketClient getClient() {
      return client;
   }

   public void setClient(WebSocketClient client) {
      this.client = client;
   }

   @Override
   public void onMessage(MessageType message) {
      this.client.onMessage(buildOdeMessage(message));
   }

   public abstract OdeMessage buildOdeMessage(MessageType message);

   @Override
   public void onOpen(Session session, EndpointConfig config) {
      this.client.onOpen(session);
      
   }

   @Override
   public void onClose(Session session, CloseReason reason) {
      this.client.onClose(reason);
   }

   @Override
   public void onError(Session session, Throwable t) {
      this.client.onError(t);
      
   }
   
   
   
   
}
