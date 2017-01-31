package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;
import java.util.List;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735SpeedProfile extends Asn1Object {
    private static final long serialVersionUID = 1L;

    private List<Integer> speedReports = new ArrayList<>();

    public List<Integer> getSpeedReports() {
        return speedReports;
    }

    public void setSpeedReports(List<Integer> speedReports) {
        this.speedReports = speedReports;
    }

}
