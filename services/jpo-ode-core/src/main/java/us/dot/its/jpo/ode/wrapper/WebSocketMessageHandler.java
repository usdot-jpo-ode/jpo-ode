/*******************************************************************************
 * Copyright (c) 2015 US DOT - Joint Program Office
 *
 * The Government has unlimited rights to all documents/material produced under 
 * this task order. All documents and materials, to include the source code of 
 * any software produced under this contract, shall be Government owned and the 
 * property of the Government with all rights and privileges of ownership/copyright 
 * belonging exclusively to the Government. These documents and materials may 
 * not be used or sold by the Contractor without written permission from the CO.
 * All materials supplied to the Government shall be the sole property of the 
 * Government and may not be used for any other purpose. This right does not 
 * abrogate any other Government rights.
 *
 * Contributors:
 *     Booz | Allen | Hamilton - initial API and implementation
 *******************************************************************************/
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
