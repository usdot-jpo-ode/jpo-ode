package us.dot.its.jpo.ode.model;

import com.fasterxml.jackson.annotation.*;

import us.dot.its.jpo.ode.plugin.j2735.J2735SRM;

public class OdeSrmPayload extends OdeMsgPayload {

    private static final long serialVersionUID = 1L;

    public OdeSrmPayload() {
        this(new J2735SRM());
    }

    @JsonCreator
    public OdeSrmPayload( @JsonProperty("data") J2735SRM srm) {
        super(srm);
        this.setData(srm);
    }

    @JsonProperty("data")
    public J2735SRM getSrm() {
        return (J2735SRM) getData();
    }

    public void setSrm(J2735SRM srm) {
        setData(srm);
    }
}
