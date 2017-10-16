package us.dot.its.jpo.ode.model;

public class OdeTimMetadata extends OdeLogMetadata {
   
   private static final long serialVersionUID = 1851475623026081007L;
   
   private OdeTimSpecificMetadata receivedMessageDetails;
   
   public OdeTimMetadata() {
      super();
   }

   public OdeTimMetadata(OdeTimPayload timPayload) {
      super(timPayload);
   }

   public OdeTimSpecificMetadata getReceivedMessageDetails() {
      return receivedMessageDetails;
   }

   public void setReceivedMessageDetails(OdeTimSpecificMetadata receivedMessageDetails) {
      this.receivedMessageDetails = receivedMessageDetails;
   }
}
