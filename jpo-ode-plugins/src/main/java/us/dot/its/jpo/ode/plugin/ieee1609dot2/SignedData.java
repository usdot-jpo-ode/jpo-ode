package us.dot.its.jpo.ode.plugin.ieee1609dot2;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class SignedData extends Asn1Object {
   private static final long serialVersionUID = -7271795367676711500L;

   public HashAlgorithm hashId;
   public ToBeSignedData tbsData;
   public SignerIdentifier signer;
   public Signature signature;

}
