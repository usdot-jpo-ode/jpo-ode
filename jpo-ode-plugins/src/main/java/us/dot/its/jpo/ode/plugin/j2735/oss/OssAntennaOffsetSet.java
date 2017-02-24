package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.AntennaOffsetSet;
import us.dot.its.jpo.ode.plugin.j2735.J2735AntennaOffsetSet;

public class OssAntennaOffsetSet {
    
    private OssAntennaOffsetSet() {}

    public static J2735AntennaOffsetSet genericAntennaOffsetSet(AntennaOffsetSet offsetSet) {

        J2735AntennaOffsetSet genericAntennaOffsetSet = new J2735AntennaOffsetSet();

        genericAntennaOffsetSet.setAntOffsetX(OssOffset.genericOffset(offsetSet.antOffsetX));
        genericAntennaOffsetSet.setAntOffsetY(OssOffset.genericOffset(offsetSet.antOffsetY));
        genericAntennaOffsetSet.setAntOffsetZ(OssOffset.genericOffset(offsetSet.antOffsetZ));

        return genericAntennaOffsetSet;
    }

}
