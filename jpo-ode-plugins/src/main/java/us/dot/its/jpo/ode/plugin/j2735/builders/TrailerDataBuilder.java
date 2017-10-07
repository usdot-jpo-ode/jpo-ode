package us.dot.its.jpo.ode.plugin.j2735.builders;

import java.util.Iterator;

import us.dot.its.jpo.ode.j2735.dsrc.TrailerData;
import us.dot.its.jpo.ode.j2735.dsrc.TrailerUnitDescription;
import us.dot.its.jpo.ode.plugin.j2735.J2735TrailerData;

public class OssTrailerData {

    private OssTrailerData() {
       throw new UnsupportedOperationException();
    }

    public static J2735TrailerData genericTrailerData(TrailerData trailers) {
        J2735TrailerData td = new J2735TrailerData();

        td.setConnection(OssPivotPointDescription.genericPivotPointDescription(trailers.connection));
        td.setSspRights(trailers.sspRights.asInt());

        Iterator<TrailerUnitDescription> iter = trailers.units.elements.iterator();

        while (iter.hasNext()) {
            td.getUnits().add(OssTrailerUnitDescription.genericTrailerUnitDescription(iter.next()));
        }

        return td;
    }

}
