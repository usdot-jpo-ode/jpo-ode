package us.dot.its.jpo.ode.plugin.builders.timstorage;

import com.fasterxml.jackson.annotation.JsonProperty;

import us.dot.its.jpo.ode.model.OdeObject;

public class DataFrames extends OdeObject {
   private static final long serialVersionUID = 1L;
   
   @JsonProperty("TravelerDataFrame")
   private TravelerDataFrame TravelerDataFrame;

   public TravelerDataFrame getTravelerDataFrame() {
      return TravelerDataFrame;
   }

   public void setTravelerDataFrame(TravelerDataFrame TravelerDataFrame) {
      this.TravelerDataFrame = TravelerDataFrame;
   }
}
