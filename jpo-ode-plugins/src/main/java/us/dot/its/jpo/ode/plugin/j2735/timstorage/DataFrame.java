package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import com.fasterxml.jackson.annotation.JsonProperty;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class DataFrame extends Asn1Object {
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
