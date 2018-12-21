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
import javax.websocket.MessageHandler;
import javax.websocket.Session;

/**
 * This interface abstracts the use of {@link MessageHandler.Whole} from the 
 * rest of the application.
 * 
 * @param <T> - the type of message being handled by this message handler.
 */
public interface WebSocketMessageHandler<T> extends MessageHandler.Whole<T> {

   void onOpen(Session session, EndpointConfig config);

   void onClose(Session session, CloseReason reason);

   void onError(Session session, Throwable t);

}
