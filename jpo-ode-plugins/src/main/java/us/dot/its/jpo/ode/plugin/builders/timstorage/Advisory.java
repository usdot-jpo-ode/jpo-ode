package us.dot.its.jpo.ode.plugin.builders.timstorage;

import com.fasterxml.jackson.annotation.JsonProperty;

import us.dot.its.jpo.ode.model.OdeObject;

public class Advisory extends OdeObject {
   private static final long serialVersionUID = 1L;
   @JsonProperty("SEQUENCE")
   private SEQUENCE[] sequence;

   public SEQUENCE[] getSEQUENCE() {
      return sequence;
   }

   public void setSEQUENCE(SEQUENCE[] sequence) {
      this.sequence = sequence;
   }
}
