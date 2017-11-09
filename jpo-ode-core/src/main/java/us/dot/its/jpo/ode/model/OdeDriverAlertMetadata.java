package us.dot.its.jpo.ode.model;

/**
 * Created by anthonychen on 11/4/17.
 */
public class OdeDriverAlertMetadata extends OdeLogMetadata{


    private static final long serialVersionUID = -8601265839394150140L;

    private ReceivedMessageDetails receivedMessageDetails;

    public ReceivedMessageDetails getReceivedMessageDetails() {
        return receivedMessageDetails;
    }

    public void setReceivedMessageDetails(ReceivedMessageDetails receivedMessageDetails) {
        this.receivedMessageDetails = receivedMessageDetails;
    }

    public OdeDriverAlertMetadata() {
        super();
    }

    public OdeDriverAlertMetadata(OdeMsgPayload payload) {
        super(payload);
    }

    public OdeDriverAlertMetadata(OdeMsgPayload payload, SerialId serialId, String receivedAt) {

    }


}
