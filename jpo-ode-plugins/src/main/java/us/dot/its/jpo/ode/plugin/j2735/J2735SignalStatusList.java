package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735SignalStatusList extends Asn1Object {

    private static final long serialVersionUID = 1L;
    private List<J2735SignalStatus> signalStatus = new ArrayList<>();

    @JsonProperty("signalStatus")
    public List<J2735SignalStatus> getStatus() {
        return signalStatus;
    }

    public void setStatus(List<J2735SignalStatus> signalStatus) {
        this.signalStatus = signalStatus;
    }
}
