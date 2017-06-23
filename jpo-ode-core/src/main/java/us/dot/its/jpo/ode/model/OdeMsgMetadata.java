package us.dot.its.jpo.ode.model;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.util.DateTimeUtils;

public class OdeMsgMetadata extends OdeMessage {

   private static final long serialVersionUID = 3979762143291085955L;

   private String payloadType;
   private SerialId serialId;
   private String timeStamp;
   private Long latency; 
   private List<OdePayloadViolation> violations;
   
   public OdeMsgMetadata(OdeMsgPayload payload) {
      this(payload, new SerialId(), DateTimeUtils.now(), null, null);
   }

   public OdeMsgMetadata(OdeMsgPayload payload, 
                         SerialId serialId,
                         String timeStamp,
                         Long latency,
                         JsonNode violations) {
       this(OdeDataType.getByClazz(payload.getClass()).getShortName(),
               serialId,
               timeStamp,
               latency,
               null);

      if (violations != null) {
         this.violations = new ArrayList<OdePayloadViolation>();
         if (violations.isArray()) {
            for (final JsonNode objNode : violations) {
               if (objNode.get("fieldName") != null
                     && objNode.get("value") != null
                     && objNode.get("validMin") != null
                     && objNode.get("validMax") != null)
                  this.violations.add(new OdePayloadViolation(
                        objNode.get("fieldName").asText(),
                        objNode.get("value").asDouble(),
                        objNode.get("validMin").asDouble(),
                        objNode.get("validMax").asDouble()));
            }
         }
      }
   }

    public OdeMsgMetadata(String payloadType, SerialId serialId, String timeStamp, Long latency,
            List<OdePayloadViolation> srcViolations) {
        super();
        this.payloadType = payloadType;
        this.serialId = serialId;
        this.timeStamp = timeStamp;
        this.latency = latency;

        if (srcViolations != null) {
            this.violations = new ArrayList<OdePayloadViolation>();
            srcViolations.forEach(new Consumer<OdePayloadViolation>() {
                @Override
                public void accept(OdePayloadViolation arg0) {
                    if (arg0 != null) {
                        violations.add(new OdePayloadViolation(arg0.getFieldName(), arg0.getValue(), arg0.getValidMin(),
                                arg0.getValidMax()));
                    }
                }

            });
        }
    }

    public String getPayloadType() {
        return payloadType;
    }

    public OdeMsgMetadata setPayloadType(OdeDataType payloadType) {
        this.payloadType = payloadType.getShortName();
        return this;
    }

    public OdeMsgMetadata setPayloadType(String payloadType) {
        this.payloadType = payloadType;
        return this;
    }

    public List<OdePayloadViolation> getViolations() {
        return violations;
    }

    public OdeMsgMetadata setViolations(List<OdePayloadViolation> violations) {
        this.violations = violations;
        return this;
    }

    public Long getLatency() {
        return latency;
    }

    public void setLatency(Long latency) {
        this.latency = latency;
    }

    public void recordLatency(String timestamp) throws ParseException {
        if (timestamp != null) {
            setLatency(DateTimeUtils.elapsedTime(DateTimeUtils.isoDateTime(timestamp)));
        }
    }

    public SerialId getSerialId() {
        return serialId;
    }

    public void setSerialId(SerialId serialId) {
        this.serialId = serialId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((latency == null) ? 0 : latency.hashCode());
        result = prime * result + ((payloadType == null) ? 0 : payloadType.hashCode());
        result = prime * result + ((serialId == null) ? 0 : serialId.hashCode());
        result = prime * result + ((timeStamp == null) ? 0 : timeStamp.hashCode());
        result = prime * result + ((violations == null) ? 0 : violations.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        OdeMsgMetadata other = (OdeMsgMetadata) obj;
        if (latency == null) {
            if (other.latency != null)
                return false;
        } else if (!latency.equals(other.latency))
            return false;
        if (payloadType == null) {
            if (other.payloadType != null)
                return false;
        } else if (!payloadType.equals(other.payloadType))
            return false;
        if (serialId == null) {
            if (other.serialId != null)
                return false;
        } else if (!serialId.equals(other.serialId))
            return false;
        if (timeStamp == null) {
            if (other.timeStamp != null)
                return false;
        } else if (!timeStamp.equals(other.timeStamp))
            return false;
        if (violations == null) {
            if (other.violations != null)
                return false;
        } else if (!violations.equals(other.violations))
            return false;
        return true;
    }

}
