package us.dot.its.jpo.ode.wrapper;

import javax.websocket.CloseReason;
import javax.websocket.Session;

import us.dot.its.jpo.ode.model.OdeMessage;
import us.dot.its.jpo.ode.model.OdeRequest;

public interface WebSocketClient {
   
   OdeRequest getRequest();

   void onMessage(OdeMessage message);

   void onOpen(Session session);

   void onClose(CloseReason reason);

   void onError(Throwable t);
}
