package us.dot.its.jpo.ode.plugin.j2735;

import java.util.List;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735PathHistoryPointList extends Asn1Object {
    private static final long serialVersionUID = 1L;

    private List<J2735PathHistoryPoint> pathHistoryPointList;

    public List<J2735PathHistoryPoint> getPathHistoryPointList() {
        return pathHistoryPointList;
    }

    public void setPathHistoryPointList(List<J2735PathHistoryPoint> pathHistoryPointList) {
        this.pathHistoryPointList = pathHistoryPointList;
    }

}
