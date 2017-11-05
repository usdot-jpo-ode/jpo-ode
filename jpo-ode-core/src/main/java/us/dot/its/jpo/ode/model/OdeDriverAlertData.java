package us.dot.its.jpo.ode.model;

/**
 * Created by anthonychen on 11/4/17.
 */
public class OdeDriverAlertData extends OdeObject {

    private static final long serialVersionUID = 2057040404896561615L;
    private String alert;

    public OdeDriverAlertData() {
            super();
        }

    public OdeDriverAlertData(OdeMsgMetadata metadata, String alert) {
        this.alert = alert;
    }

}
