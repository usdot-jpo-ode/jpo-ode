package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735SignalRequestList extends Asn1Object {
    
    private static final long serialVersionUID = 1L;
    private List<J2735SignalRequestPackage> signalRequestPackage = new ArrayList<>();

    @JsonProperty("signalRequestPackage")
    public List<J2735SignalRequestPackage> getRequests() {
        return signalRequestPackage;
    }

    public void setRequests(List<J2735SignalRequestPackage> signalRequestPackage) {
        this.signalRequestPackage = signalRequestPackage;
    }
}
