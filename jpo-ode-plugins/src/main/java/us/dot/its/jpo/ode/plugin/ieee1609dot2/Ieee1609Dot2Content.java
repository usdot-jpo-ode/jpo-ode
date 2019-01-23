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
import us.dot.its.jpo.ode.plugin.j2735.timstorage.MessageFrame;

public class Ieee1609Dot2Content extends Asn1Object {
   private static final long serialVersionUID = 8613093241018229620L;

   
   private MessageFrame unsecuredData; 
   private String unsecuredDataBytes; 
   private SignedData signedData;
   private EncryptedData encryptedData;
   private String signedCertificateRequest;

   public MessageFrame getUnsecuredData() {
      return unsecuredData;
   }
   public void setUnsecuredData(MessageFrame unsecuredData) {
      this.unsecuredData = unsecuredData;
   }
   public String getUnsecuredDataBytes() {
      return unsecuredDataBytes;
   }
   public void setUnsecuredDataBytes(String unsecuredDataBytes) {
      this.unsecuredDataBytes = unsecuredDataBytes;
   }
   public SignedData getSignedData() {
      return signedData;
   }
   public void setSignedData(SignedData signedData) {
      this.signedData = signedData;
   }
   public EncryptedData getEncryptedData() {
      return encryptedData;
   }
   public void setEncryptedData(EncryptedData encryptedData) {
      this.encryptedData = encryptedData;
   }
   public String getSignedCertificateRequest() {
      return signedCertificateRequest;
   }
   public void setSignedCertificateRequest(String signedCertificateRequest) {
      this.signedCertificateRequest = signedCertificateRequest;
   }
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((encryptedData == null) ? 0 : encryptedData.hashCode());
      result = prime * result + ((signedCertificateRequest == null) ? 0 : signedCertificateRequest.hashCode());
      result = prime * result + ((signedData == null) ? 0 : signedData.hashCode());
      result = prime * result + ((unsecuredData == null) ? 0 : unsecuredData.hashCode());
      return result;
   }
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Ieee1609Dot2Content other = (Ieee1609Dot2Content) obj;
      if (encryptedData == null) {
         if (other.encryptedData != null)
            return false;
      } else if (!encryptedData.equals(other.encryptedData))
         return false;
      if (signedCertificateRequest == null) {
         if (other.signedCertificateRequest != null)
            return false;
      } else if (!signedCertificateRequest.equals(other.signedCertificateRequest))
         return false;
      if (signedData == null) {
         if (other.signedData != null)
            return false;
      } else if (!signedData.equals(other.signedData))
         return false;
      if (unsecuredData == null) {
         if (other.unsecuredData != null)
            return false;
      } else if (!unsecuredData.equals(other.unsecuredData))
         return false;
      return true;
   }
   
}
