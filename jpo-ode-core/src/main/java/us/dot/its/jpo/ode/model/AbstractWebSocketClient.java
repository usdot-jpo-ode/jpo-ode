package us.dot.its.jpo.ode.model;

public abstract class AbstractWebSocketClient implements WebSocketClient {
   private BaseRequest request;

   public void setRequest(BaseRequest request) {
      this.request = request;
   }

   @Override
   public BaseRequest getRequest() {
      return request;
   }
   

}
