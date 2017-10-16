package us.dot.its.jpo.ode.model;

public class OdeLogMetadata extends OdeMsgMetadata {

    private static final long serialVersionUID = -8601265839394150140L;
    
    protected String recordGeneratedAt;
    protected String recordType;
    protected String recordGeneratedBy;
    protected String logFileName;
    protected boolean validSignature = false;
    protected boolean sanitized = false;

    public OdeLogMetadata() {
        super();
    }

    public OdeLogMetadata(String payloadType, SerialId serialId, String receivedAt) {
        super(payloadType, serialId, receivedAt);
    }

    public OdeLogMetadata(OdeMsgPayload payload, SerialId serialId, String receivedAt, String generatedAt) {
        super(payload, serialId, receivedAt);
        this.recordGeneratedAt = generatedAt;
    }

    public OdeLogMetadata(OdeMsgPayload payload) {
        super(payload);
    }

    public OdeLogMetadata(OdeMsgPayload payload, String generatedAt) {
        super(payload);
        this.recordGeneratedAt = generatedAt;
    }

    public OdeLogMetadata(String payloadType, SerialId serialId, String receivedAt, String generatedAt) {
        super(payloadType, serialId, receivedAt);
        this.recordGeneratedAt = generatedAt;
    }

    public OdeLogMetadata(OdeMsgPayload payload, SerialId serialId, String receivedAt) {
       
    }

    public String getRecordGeneratedAt() {
        return recordGeneratedAt;
    }

    public void setRecordGeneratedAt(String recordGeneratedAt) {
        this.recordGeneratedAt = recordGeneratedAt;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public String getRecordGeneratedBy() {
        return recordGeneratedBy;
    }

    public void setRecordGeneratedBy(String recordGeneratedBy) {
        this.recordGeneratedBy= recordGeneratedBy;
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
