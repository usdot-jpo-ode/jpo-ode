package us.dot.its.jpo.ode.plugin.ieee1609dot2;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class SignedData extends Asn1Object {
   private static final long serialVersionUID = -7271795367676711500L;

   private HashAlgorithm hashId;
   private ToBeSignedData tbsData;
   private SignerIdentifier signer;
   private Signature signature;
   
   public HashAlgorithm getHashId() {
      return hashId;
   }
   public void setHashId(HashAlgorithm hashId) {
      this.hashId = hashId;
   }
   public ToBeSignedData getTbsData() {
      return tbsData;
   }
   public void setTbsData(ToBeSignedData tbsData) {
      this.tbsData = tbsData;
   }
   public SignerIdentifier getSigner() {
      return signer;
   }
   public void setSigner(SignerIdentifier signer) {
      this.signer = signer;
   }
   public Signature getSignature() {
      return signature;
   }
   public void setSignature(Signature signature) {
      this.signature = signature;
   }
   

}
