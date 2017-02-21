package us.dot.its.jpo.ode.model;

import javax.websocket.CloseReason;

public interface WebSocketClient {
   
   OdeRequest getRequest();

   void onMessage(OdeMessage message);

   void onOpen();

   void OnClose(CloseReason reason);

   void onError(Throwable t);
}
