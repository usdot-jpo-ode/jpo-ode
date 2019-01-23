/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
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
