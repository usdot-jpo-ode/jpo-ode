package us.dot.its.jpo.ode.coder;

import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.Ieee1609Dot2Content;

public class Ieee1609ContentValidator {

    private Ieee1609ContentValidator() {
    }

    public static byte[] getUnsecuredData(Ieee1609Dot2Content ieee1609dot2ContentIn) {
        byte[] unsecuredData;
        try {
            if (ieee1609dot2ContentIn.getUnsecuredData() != null) {
                unsecuredData = ieee1609dot2ContentIn.getUnsecuredData().byteArrayValue();
            } else {
                unsecuredData = ieee1609dot2ContentIn.getSignedData().getTbsData().getPayload().getData().getContent()
                        .getUnsecuredData().byteArrayValue();
            }
        } catch (NullPointerException e) {
            unsecuredData = null;
        }
        
        return unsecuredData;
    }

}
