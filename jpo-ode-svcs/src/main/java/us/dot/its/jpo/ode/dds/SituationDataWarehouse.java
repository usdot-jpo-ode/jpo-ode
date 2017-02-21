package us.dot.its.jpo.ode.dds;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.DdsClient.DdsClientException;
import us.dot.its.jpo.ode.dds.DdsRequestManager.DdsRequestManagerException;
import us.dot.its.jpo.ode.model.OdeRequest;
import us.dot.its.jpo.ode.model.WebSocketClient;
import us.dot.its.jpo.ode.wrapper.WebSocketEndpoint.WebSocketException;
import us.dot.its.jpo.ode.wrapper.WebSocketMessageHandler;

public class SituationDataWarehouse<MessageType> {
   
   private OdeProperties odeProperties;
   private WebSocketClient client;
   private DdsRequestManager<MessageType> requestManager;
   
   public SituationDataWarehouse (OdeProperties odeProperties, WebSocketClient client) {
      
      this.odeProperties = odeProperties;
      this.client = client;
   }
   
   @SuppressWarnings("unchecked")
   public void send(OdeRequest odeRequest) 
         throws DdsRequestManagerException, DdsClientException, WebSocketException {
      switch (odeRequest.getRequestType()) {
      case Deposit:
         if(this.requestManager == null) {
            this.requestManager = (DdsRequestManager<MessageType>) new DdsDepositRequestManager(
               odeProperties);
         }
         
         if (!this.requestManager.isConnected()) {
            this.requestManager.connect(
                  (WebSocketMessageHandler<MessageType>) new StatusMessageHandler(client), 
                  DepositResponseDecoder.class);
         }
         
         this.requestManager.sendRequest(odeRequest);
         break;
         
      default:
      }
   }

   public WebSocketClient getClient() {
      return client;
   }

   public void setClient(WebSocketClient client) {
      this.client = client;
   }

}
