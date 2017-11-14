package us.dot.its.jpo.ode.plugin.ieee1609dot2;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class Ieee1609Dot2Content extends Asn1Object {
   private static final long serialVersionUID = 8613093241018229620L;

   public String unsecuredData; 
   public SignedData signedData;
   public EncryptedData encryptedData;
   public String signedCertificateRequest;
   
}
