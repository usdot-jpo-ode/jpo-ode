package us.dot.its.jpo.ode.model;

/**
 * Created by anthonychen on 11/4/17.
 */
public class OdeDriverAlertData extends OdeData {

    private static final long serialVersionUID = 2057040404896561615L;

    public OdeDriverAlertData() {
            super();
        }

    public OdeDriverAlertData(OdeMsgMetadata metadata, OdeMsgPayload payload) {
        super(metadata, payload);
    }


}
