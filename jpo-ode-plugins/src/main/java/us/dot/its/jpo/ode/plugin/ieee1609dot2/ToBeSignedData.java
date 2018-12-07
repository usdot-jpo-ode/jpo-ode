package us.dot.its.jpo.ode.plugin.ieee1609dot2;

import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.HeaderInfo;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.SignedDataPayload;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class ToBeSignedData extends Asn1Object {
   
   private static final long serialVersionUID = 4662545225498639272L;
   
   private SignedDataPayload payload;
   private HeaderInfo headerInfo;
   
   public SignedDataPayload getPayload() {
      return payload;
   }
   public void setPayload(SignedDataPayload payload) {
      this.payload = payload;
   }
   public HeaderInfo getHeaderInfo() {
      return headerInfo;
   }
   public void setHeaderInfo(HeaderInfo headerInfo) {
      this.headerInfo = headerInfo;
   }

}
