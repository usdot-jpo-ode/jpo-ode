package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import com.fasterxml.jackson.annotation.JsonProperty;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class Advisory extends Asn1Object {
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
