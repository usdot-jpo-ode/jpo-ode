package us.dot.its.jpo.ode.model;


/**
 * Created by anthonychen on 11/4/17.
 */
public class OdeDriverAlertPayload extends OdeMsgPayload {


        private static final long serialVersionUID = 7061315628111448390L;
        private String alert;


        public OdeDriverAlertPayload(String alert) {
            this.alert = alert;
        }

        public String getAlert() {
            return this.alert;
        }

        public void setAlert(String alert) {
            this.alert = alert;
        }


}
