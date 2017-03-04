package us.dot.its.jpo.ode.plugin.j2735.oss;

/**
 * Created by anthonychen on 3/3/17.
 */

import us.dot.its.jpo.ode.j2735.dsrc.Elevation;
import us.dot.its.jpo.ode.j2735.dsrc.Latitude;
import us.dot.its.jpo.ode.j2735.dsrc.Longitude;
import us.dot.its.jpo.ode.j2735.dsrc.Position3D;
import us.dot.its.jpo.ode.plugin.j2735.J2735Position3D;

public class OssPosition3D {


    private OssPosition3D() {}

    public static Position3D position3D(J2735Position3D genericPosition) {
        Position3D position3D = new Position3D();

        position3D.setElevation( new Elevation( genericPosition.getElevation().intValue()));
        position3D.setLat(new Latitude(genericPosition.getLatitude().intValue()));
        position3D.set_long(new Longitude(genericPosition.getLongitude().intValue()));

        return position3D;
    }


}
