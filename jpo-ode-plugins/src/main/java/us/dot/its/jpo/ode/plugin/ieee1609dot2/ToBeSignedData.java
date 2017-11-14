package us.dot.its.jpo.ode.plugin.ieee1609dot2;

import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.HeaderInfo;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.SignedDataPayload;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class ToBeSignedData extends Asn1Object {
   public SignedDataPayload payload;
   public HeaderInfo headerInfo;

}
