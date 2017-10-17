package us.dot.its.jpo.ode.model;

public class OdeTimSpecificMetadata extends OdeObject {

   private static final long serialVersionUID = -122988228561853841L;

   private OdeTimSpecificMetadataLocation locationData;
   private RxSource rxSource;

   public OdeTimSpecificMetadata() {
      super();
   }
   
   public OdeTimSpecificMetadata(OdeTimSpecificMetadataLocation locationData, RxSource rxSource) {
      super();
      this.setLocationData(locationData);
      this.setRxSource(rxSource);
   }

   public OdeTimSpecificMetadataLocation getLocationData() {
      return locationData;
   }

   public void setLocationData(OdeTimSpecificMetadataLocation locationData) {
      this.locationData = locationData;
   }

   public RxSource getRxSource() {
      return rxSource;
   }

   public void setRxSource(RxSource rxSource) {
      this.rxSource = rxSource;
   }
}
