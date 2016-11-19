package us.dot.its.jpo.ode.model;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.util.DateTimeUtils;

public class OdeMsgMetadata extends OdeMessage {

   private static final long serialVersionUID = 3979762143291085955L;

   private String payloadType;
   private List<OdePayloadViolation> violations;
   private Long latency; 
   
   public OdeMsgMetadata(OdeMsgPayload payload) {
      this(payload, null);
   }

   public OdeMsgMetadata(OdeMsgPayload payload, JsonNode violations) {
      this.payloadType = OdeDataType.getByClazz(payload.getClass())
            .getShortName();

      if (violations != null) {
         this.violations = new ArrayList<OdePayloadViolation>();
         if (violations.isArray()) {
            for (final JsonNode objNode : violations) {
               if (objNode.get("fieldName") != null
                     && objNode.get("validMin") != null
                     && objNode.get("validMax") != null)
                  this.violations.add(new OdePayloadViolation(
                        objNode.get("fieldName").asText(),
                        objNode.get("validMin").asDouble(),
                        objNode.get("validMax").asDouble()));
            }
         }
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

   public OdeMsgMetadata setViolations(
         List<OdePayloadViolation> violations) {
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

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result
            + ((payloadType == null) ? 0 : payloadType.hashCode());
      result = prime * result
            + ((violations == null) ? 0 : violations.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      OdeMsgMetadata other = (OdeMsgMetadata) obj;
      if (payloadType == null) {
         if (other.payloadType != null)
            return false;
      } else if (!payloadType.equals(other.payloadType))
         return false;
      if (violations == null) {
         if (other.violations != null)
            return false;
      } else if (!violations.equals(other.violations))
         return false;
      return true;
   }

}
