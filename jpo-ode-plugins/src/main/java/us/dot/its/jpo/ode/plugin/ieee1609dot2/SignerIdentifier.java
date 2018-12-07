package us.dot.its.jpo.ode.plugin.ieee1609dot2;

import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class SignerIdentifier extends Asn1Object {
   private static final long serialVersionUID = 9091063871259962271L;

   private String digest;
   private Certificate[] certificate;
   private OdeObject self = null;
   
   public String getDigest() {
      return digest;
   }
   public void setDigest(String digest) {
      this.digest = digest;
   }
   public Certificate[] getCertificate() {
      return certificate;
   }
   public void setCertificate(Certificate[] certificate) {
      this.certificate = certificate;
   }
   public OdeObject getSelf() {
      return self;
   }
   public void setSelf(OdeObject self) {
      this.self = self;
   }

}
