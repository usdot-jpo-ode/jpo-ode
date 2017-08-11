package us.dot.its.jpo.ode.model;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public class OdeBsmMetadata extends OdeMsgMetadata {

    private static final long serialVersionUID = -8601265839394150140L;
    
    private String generatedAt;
    private String logFileName;
    private boolean validSignature = false;
    private boolean sanitized = false;
    
    

    public OdeBsmMetadata() {
        super();
    }

    public OdeBsmMetadata(OdeMsgPayload payload, SerialId serialId, String receivedAt, Long latency,
            JsonNode violations) {
        super(payload, serialId, receivedAt, latency, violations);
    }

    public OdeBsmMetadata(String payloadType, SerialId serialId, String receivedAt, Long latency,
            List<OdePayloadViolation> srcViolations) {
        super(payloadType, serialId, receivedAt, latency, srcViolations);
    }

    public OdeBsmMetadata(
        OdeMsgPayload payload, 
        SerialId serialId, 
        String receivedAt, 
        Long latency,
        JsonNode violations, 
        String generatedAt) {
        super(payload, serialId, receivedAt, latency, violations);
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
        Long latency,
        List<OdePayloadViolation> srcViolations, 
        String generatedAt) {
        super(payloadType, serialId, receivedAt, latency, srcViolations);
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
