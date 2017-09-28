package us.dot.its.jpo.ode.model;

public class OdeTimMetadata extends OdeBsmMetadata {
   
   private static final long serialVersionUID = 1851475623026081007L;
   
   private RxSource rxSource;
   
   public class OdeTimMetadataLocation {
      String latitude;
      String longitude;
      String elevation;
      String speed;
   }
   
   public OdeTimMetadata() {
      super();
   }

   public OdeTimMetadata(OdeTimPayload timPayload) {
      super(timPayload);
   }

   public OdeTimMetadata(OdeTimPayload timPayload, SerialId serialId, String receivedAt, String generatedAt) {
      super(timPayload, serialId, receivedAt, generatedAt);
   }

   public RxSource getRxSource() {
      return rxSource;
   }

   public void setRxSource(RxSource rxSource) {
      this.rxSource = rxSource;
   }

}
