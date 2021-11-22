package us.dot.its.jpo.ode.model;

import us.dot.its.jpo.ode.plugin.j2735.J2735SSM;

public class OdeSsmPayload extends OdeMsgPayload {

    public OdeSsmPayload() {
        this(new J2735SSM());
    }

    public OdeSsmPayload(J2735SSM ssm) {
        super(ssm);
        this.setData(ssm);
    }

    public J2735SSM getSsm() {
        return (J2735SSM) getData();
    }

    public void setSsm(J2735SSM ssm) {
        setData(ssm);
    }
}
