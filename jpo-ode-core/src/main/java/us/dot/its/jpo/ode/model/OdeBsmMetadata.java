package us.dot.its.jpo.ode.model;

public class OdeBsmMetadata extends OdeLogMetadata {

    private static final long serialVersionUID = -8601265839394150140L;
    
    public OdeBsmMetadata() {
        super();
    }

    public OdeBsmMetadata(String payloadType, SerialId serialId, String receivedAt) {
        super(payloadType, serialId, receivedAt);
    }

    public OdeBsmMetadata(OdeMsgPayload payload, SerialId serialId, String receivedAt, String generatedAt) {
        super(payload, serialId, receivedAt);
        this.generatedAt = generatedAt;
    }

    public OdeBsmMetadata(OdeMsgPayload payload) {
        super(payload);
    }

    public OdeBsmMetadata(OdeMsgPayload payload, String generatedAt) {
        super(payload);
        this.generatedAt = generatedAt;
    }

    public OdeBsmMetadata(String payloadType, SerialId serialId, String receivedAt, String generatedAt) {
        super(payloadType, serialId, receivedAt);
        this.generatedAt = generatedAt;
    }

    public OdeBsmMetadata(OdeMsgPayload payload, SerialId serialId, String receivedAt) {
       
    }

}
