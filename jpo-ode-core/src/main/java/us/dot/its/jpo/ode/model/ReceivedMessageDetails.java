package us.dot.its.jpo.ode.model;

public class ReceivedMessageDetails extends OdeObject {

   private static final long serialVersionUID = -122988228561853841L;

   private OdeLogMsgMetadataLocation locationData;
   private RxSource rxSource;

   public ReceivedMessageDetails() {
      super();
   }
   
   public ReceivedMessageDetails(OdeLogMsgMetadataLocation locationData, RxSource rxSource) {
      super();
      this.setLocationData(locationData);
      this.setRxSource(rxSource);
   }

   public OdeLogMsgMetadataLocation getLocationData() {
      return locationData;
   }

   public void setLocationData(OdeLogMsgMetadataLocation locationData) {
      this.locationData = locationData;
   }

   public RxSource getRxSource() {
      return rxSource;
   }

   public void setRxSource(RxSource rxSource) {
      this.rxSource = rxSource;
   }
}
