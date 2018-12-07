package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import com.fasterxml.jackson.annotation.JsonProperty;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class Advisory extends Asn1Object {
   private static final long serialVersionUID = 1L;
   @JsonProperty("SEQUENCE")
   private Sequence[] SEQUENCE;
   
   public Sequence[] getSEQUENCE() {
     return SEQUENCE;
   }
   public void setSEQUENCE(Sequence[] sEQUENCE) {
     SEQUENCE = sEQUENCE;
   }
}
