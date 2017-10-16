package us.dot.its.jpo.ode.model;

public class OdeBsmMetadata extends OdeLogMetadata {

    private static final long serialVersionUID = -8601265839394150140L;
    
    public OdeBsmMetadata() {
        super();
    }

   public OdeBsmMetadata(OdeMsgPayload payload) {
        super(payload);
    }

    public OdeBsmMetadata(OdeMsgPayload payload, SerialId serialId, String receivedAt) {
       
    }

}
