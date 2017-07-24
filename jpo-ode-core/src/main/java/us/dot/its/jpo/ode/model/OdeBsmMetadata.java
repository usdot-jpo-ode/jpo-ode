package us.dot.its.jpo.ode.model;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public class OdeBsmMetadata extends OdeMsgMetadata {

    private static final long serialVersionUID = -8601265839394150140L;
    
    private String generatedAt;
    private String logFileName;

    public OdeBsmMetadata(OdeMsgPayload payload, SerialId serialId, String timeStamp, Long latency,
            JsonNode violations, String generatedAt) {
        super(payload, serialId, timeStamp, latency, violations);
        this.generatedAt = generatedAt;
    }

    public OdeBsmMetadata(OdeMsgPayload payload, String generatedAt) {
        super(payload);
        this.generatedAt = generatedAt;
    }

    public OdeBsmMetadata(String payloadType, SerialId serialId, String timeStamp, Long latency,
            List<OdePayloadViolation> srcViolations, String generatedAt) {
        super(payloadType, serialId, timeStamp, latency, srcViolations);
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

    
}
