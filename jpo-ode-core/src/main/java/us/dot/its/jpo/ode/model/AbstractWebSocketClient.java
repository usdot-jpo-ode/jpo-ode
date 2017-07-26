package us.dot.its.jpo.ode.model;

import us.dot.its.jpo.ode.wrapper.WebSocketClient;

public abstract class AbstractWebSocketClient implements WebSocketClient {
   private OdeRequest odeRequest;

   public OdeRequest getOdeRequest() {
      return odeRequest;
   }

   public void setOdeRequest(OdeRequest odeRequest) {
      this.odeRequest = odeRequest;
   }

   @Override
   public OdeRequest getRequest() {
      return odeRequest;
   }
   

}
