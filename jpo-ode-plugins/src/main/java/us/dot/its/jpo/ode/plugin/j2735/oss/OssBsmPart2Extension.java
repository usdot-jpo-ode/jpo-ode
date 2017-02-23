package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.SupplementalVehicleExtensions;
import us.dot.its.jpo.ode.plugin.j2735.J2735BsmPart2Extension;
import us.dot.its.jpo.ode.plugin.j2735.J2735SupplementalVehicleExtensions;

public class OssBsmPart2Extension {

    public static J2735BsmPart2Extension genericSupplementalVehicleExtensions(SupplementalVehicleExtensions sve) {
        J2735SupplementalVehicleExtensions gsve = OssSupplementalVehicleExtensions
                .genericSupplementalVehicleExtensions(sve);
        return gsve;
    }

}
