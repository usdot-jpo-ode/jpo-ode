package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import us.dot.its.jpo.ode.j2735.dsrc.PathHistoryPoint;
import us.dot.its.jpo.ode.j2735.dsrc.PathHistoryPointList;
import us.dot.its.jpo.ode.plugin.j2735.J2735PathHistoryPoint;

public class OssPathHistoryPointList {

    public static List<J2735PathHistoryPoint> genericPathHistoryPointList(PathHistoryPointList crumbData) {

        List<J2735PathHistoryPoint> phpl = new ArrayList<J2735PathHistoryPoint>();

        Iterator<PathHistoryPoint> iter = crumbData.elements.iterator();

        while (iter.hasNext() && phpl.size() < 23) {
            phpl.add(OssPathHistoryPoint.genericPathHistoryPoint(iter.next()));
        }

        return phpl;
    }

}
