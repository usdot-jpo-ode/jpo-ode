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
   private String receivedAt;
   private Long latency; 
   private List<OdePayloadViolation> violations;
   
   
   public OdeMsgMetadata() {
       this(OdeDataType.Unknown.name(), new SerialId(), DateTimeUtils.now(), null, null);
   }

   public OdeMsgMetadata(OdeMsgPayload payload) {
      this(payload, new SerialId(), DateTimeUtils.now(), null, null);
   }

   public OdeMsgMetadata(OdeMsgPayload payload, 
                         SerialId serialId,
                         String receivedAt,
                         Long latency,
                         JsonNode violations) {
       this(payload.getClass().getName(),
               serialId,
               receivedAt,
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
                        objNode.get("fieldValue").asDouble(),
                        objNode.get("validMin").asDouble(),
                        objNode.get("validMax").asDouble()));
            }
         }
      }
   }

    public OdeMsgMetadata(String payloadType, SerialId serialId, String receivedAt, Long latency,
            List<OdePayloadViolation> srcViolations) {
        super();
        this.payloadType = payloadType;
        this.serialId = serialId;
        this.receivedAt = receivedAt;
        this.latency = latency;

        if (srcViolations != null) {
            this.violations = new ArrayList<OdePayloadViolation>();
            srcViolations.forEach(new Consumer<OdePayloadViolation>() {
                @Override
                public void accept(OdePayloadViolation arg0) {
                    if (arg0 != null) {
                        violations.add(new OdePayloadViolation(arg0.getFieldName(), arg0.getFieldValue(), arg0.getValidMin(),
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

    public void recordLatency(String receivedAt) throws ParseException {
        if (receivedAt != null) {
            setLatency(DateTimeUtils.elapsedTime(DateTimeUtils.isoDateTime(receivedAt)));
        }
    }

    public SerialId getSerialId() {
        return serialId;
    }

    public void setSerialId(SerialId serialId) {
        this.serialId = serialId;
    }

    public String getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(String receivedAt) {
        this.receivedAt = receivedAt;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((latency == null) ? 0 : latency.hashCode());
        result = prime * result + ((payloadType == null) ? 0 : payloadType.hashCode());
        result = prime * result + ((serialId == null) ? 0 : serialId.hashCode());
        result = prime * result + ((receivedAt == null) ? 0 : receivedAt.hashCode());
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
        if (receivedAt == null) {
            if (other.receivedAt != null)
                return false;
        } else if (!receivedAt.equals(other.receivedAt))
            return false;
        if (violations == null) {
            if (other.violations != null)
                return false;
        } else if (!violations.equals(other.violations))
            return false;
        return true;
    }

}
