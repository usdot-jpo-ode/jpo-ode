package us.dot.its.jpo.ode.model;

import javax.websocket.CloseReason;
import javax.websocket.Session;

public interface WebSocketClient {
   
   OdeRequest getRequest();

   void onMessage(OdeMessage message);

   void onOpen(Session session);

   void onClose(CloseReason reason);

   void onError(Throwable t);
}
