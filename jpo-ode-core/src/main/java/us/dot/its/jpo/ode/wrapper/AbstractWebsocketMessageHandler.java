package us.dot.its.jpo.ode.wrapper;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

import us.dot.its.jpo.ode.model.OdeMessage;
import us.dot.its.jpo.ode.model.WebSocketClient;

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
