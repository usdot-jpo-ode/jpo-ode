package us.dot.its.jpo.ode.model;

import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;

public class OdeBsmPayload extends OdeMsgPayload {

    private static final long serialVersionUID = 7061315628111448390L;

    public OdeBsmPayload() {
        this(new J2735Bsm());
    }

    public OdeBsmPayload(J2735Bsm bsm) {
        super();
        this.setData(bsm);
    }

    public J2735Bsm getBsm() {
        return (J2735Bsm) getData();
    }

    public void setBsm(J2735Bsm bsm) {
        setData(bsm);
    }

}
