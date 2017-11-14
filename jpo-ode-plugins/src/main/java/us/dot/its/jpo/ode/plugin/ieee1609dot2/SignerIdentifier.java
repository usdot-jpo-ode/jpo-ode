package us.dot.its.jpo.ode.plugin.ieee1609dot2;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class SignerIdentifier extends Asn1Object {
   private static final long serialVersionUID = 9091063871259962271L;

   public String digest;
   public Certificate[] certificate;
   public Object self = null;

}
