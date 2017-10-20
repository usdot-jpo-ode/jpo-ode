package us.dot.its.jpo.ode.model;

public class OdeTimMetadata extends OdeLogMetadata {
   
   private static final long serialVersionUID = 1851475623026081007L;
   
   private ReceivedMessageDetails receivedMessageDetails;
   
   public ReceivedMessageDetails getReceivedMessageDetails() {
      return receivedMessageDetails;
   }

   public void setReceivedMessageDetails(ReceivedMessageDetails receivedMessageDetails) {
      this.receivedMessageDetails = receivedMessageDetails;
   }

  public OdeTimMetadata() {
      super();
   }

   public OdeTimMetadata(OdeTimPayload timPayload) {
      super(timPayload);
   }

   public OdeTimMetadata(OdeBsmPayload bsmPayload) {
      super(bsmPayload);
   }

}
