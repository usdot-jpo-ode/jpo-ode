package us.dot.its.jpo.ode.model;

public class OdeLogMetadataReceived extends OdeLogMetadata {
   
   private static final long serialVersionUID = -705610489887212191L;
   
   private ReceivedMessageDetails receivedMessageDetails;
   
   public OdeLogMetadataReceived() {
      super();
   }
   
   public OdeLogMetadataReceived(OdeMsgPayload payload) {
      super(payload);
   }

   public ReceivedMessageDetails getReceivedMessageDetails() {
      return receivedMessageDetails;
   }

   public void setReceivedMessageDetails(ReceivedMessageDetails receivedMessageDetails) {
      this.receivedMessageDetails = receivedMessageDetails;
   }

}
