package us.dot.its.jpo.ode.model;

public class OdeBsmMetadata extends OdeMsgMetadata {

    private static final long serialVersionUID = -8601265839394150140L;
    
    private String generatedAt;
    private String logFileName;
    private boolean validSignature = false;
    private boolean sanitized = false;

    public OdeBsmMetadata() {
        super();
    }

    public OdeBsmMetadata(String payloadType, SerialId serialId, String receivedAt) {
        super(payloadType, serialId, receivedAt);
    }

    public OdeBsmMetadata(
        OdeMsgPayload payload, 
        SerialId serialId, 
        String receivedAt, 
        String generatedAt) {
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

    public OdeBsmMetadata(
        String payloadType, 
        SerialId serialId, 
        String receivedAt,
        String generatedAt) {
        super(payloadType, serialId, receivedAt);
        this.generatedAt = generatedAt;
    }

    public String getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(String generatedAt) {
        this.generatedAt = generatedAt;
    }

    public String getLogFileName() {
        return logFileName;
    }

    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

    public boolean isValidSignature() {
        return validSignature;
    }

    public void setValidSignature(boolean validSignature) {
        this.validSignature = validSignature;
    }

    public boolean isSanitized() {
        return sanitized;
    }

    public void setSanitized(boolean sanitized) {
        this.sanitized = sanitized;
    }

    
}
