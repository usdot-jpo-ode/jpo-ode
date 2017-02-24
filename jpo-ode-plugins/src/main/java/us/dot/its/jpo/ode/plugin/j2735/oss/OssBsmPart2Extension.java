package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.SupplementalVehicleExtensions;
import us.dot.its.jpo.ode.plugin.j2735.J2735BsmPart2Extension;

public class OssBsmPart2Extension {
    
    private OssBsmPart2Extension() {}

    public static J2735BsmPart2Extension genericSupplementalVehicleExtensions(SupplementalVehicleExtensions sve) {
        return OssSupplementalVehicleExtensions.genericSupplementalVehicleExtensions(sve);
    }

}
