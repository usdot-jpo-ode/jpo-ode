package us.dot.its.jpo.ode.plugin.j2735;

import java.util.List;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735PathHistory extends Asn1Object {
    private static final long serialVersionUID = 1L;

    private J2735FullPositionVector initialPosition;
    private J2735BitString currGNSSstatus;
    private List<J2735PathHistoryPoint> crumbData;

    public J2735FullPositionVector getInitialPosition() {
        return initialPosition;
    }

    public void setInitialPosition(J2735FullPositionVector initialPosition) {
        this.initialPosition = initialPosition;
    }

    public J2735BitString getCurrGNSSstatus() {
        return currGNSSstatus;
    }

    public void setCurrGNSSstatus(J2735BitString currGNSSstatus) {
        this.currGNSSstatus = currGNSSstatus;
    }

    public List<J2735PathHistoryPoint> getCrumbData() {
        return crumbData;
    }

    public void setCrumbData(List<J2735PathHistoryPoint> crumbData) {
        this.crumbData = crumbData;
    }

}
