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

   public OdeTimMetadata(OdeBsmPayload timPayload) {
      super(timPayload);
   }

   public OdeTimMetadata(OdeTimPayload timPayload, SerialId serialId, String receivedAt) {
      super(timPayload.getClass().getName(), serialId, receivedAt);
   }

   public OdeTimMetadata(OdeBsmPayload timPayload, SerialId serialId, String receivedAt) {
      super(timPayload.getClass().getName(), serialId, receivedAt);
   }

   public OdeTimSpecificMetadata getReceivedMessageDetails() {
      return receivedMessageDetails;
   }

   public void setReceivedMessageDetails(OdeTimSpecificMetadata receivedMessageDetails) {
      this.receivedMessageDetails = receivedMessageDetails;
   }
}
