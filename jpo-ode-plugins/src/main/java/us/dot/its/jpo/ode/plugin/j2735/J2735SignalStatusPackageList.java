package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;
import java.util.List;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735SignalStatusPackageList extends Asn1Object {

    private static final long serialVersionUID = 1L;
    private List<J2735SignalStatusPackage> signalStatusPackage = new ArrayList<>();

    public List<J2735SignalStatusPackage> getSigStatus() {
        return signalStatusPackage;
    }

    public void setSigStatus(List<J2735SignalStatusPackage> signalStatusPackage) {
        this.signalStatusPackage = signalStatusPackage;
    }
}
