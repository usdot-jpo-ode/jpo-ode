package us.dot.its.jpo.ode.model;

public final class OdeWeatherData extends OdeData {

   private static final long serialVersionUID = -5436772750681089157L;

   public OdeWeatherData() {
      super();
   }

   public OdeWeatherData(String streamId, long bundleId, long recordId) {
      super(streamId, bundleId, recordId);
   }

   public OdeWeatherData(String serialId) {
      super(serialId);
   }

}
