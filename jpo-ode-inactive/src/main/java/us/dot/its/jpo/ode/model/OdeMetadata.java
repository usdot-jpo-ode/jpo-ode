package us.dot.its.jpo.ode.model;

import java.util.List;

import us.dot.its.jpo.ode.wrapper.MQTopic;

public class OdeMetadata extends OdeObject {
   private static final long serialVersionUID = 7892898609083630517L;

   private String  key;
   private String payloadType;
   private List<OdePayloadViolation> violations;
   private MQTopic inputTopic;
   private MQTopic outputTopic;
   private OdeRequest odeRequest;
   
   public OdeMetadata() {
      super();
   }

   public OdeMetadata(String key, MQTopic inputTopic, MQTopic outputTopic,
         OdeRequest odeRequest) {
      super();
      this.key = key;
      this.inputTopic = inputTopic;
      this.outputTopic = outputTopic;
      this.odeRequest = odeRequest;
   }

   public String getKey() {
      return key;
   }
   public OdeMetadata setKey(String key) {
      this.key = key;
      return this;
   }
   public MQTopic getInputTopic() {
      return inputTopic;
   }
   public OdeMetadata setInputTopic(MQTopic inputTopic) {
      this.inputTopic = inputTopic;
      return this;
   }
   public MQTopic getOutputTopic() {
      return outputTopic;
   }
   public OdeMetadata setOutputTopic(MQTopic outputTopic) {
      this.outputTopic = outputTopic;
      return this;
   }
   public OdeRequest getOdeRequest() {
      return odeRequest;
   }
   public OdeMetadata setOdeRequest(OdeRequest odeRequest) {
      this.odeRequest = odeRequest;
      return this;
   }
   public String getPayloadType() {
      return payloadType;
   }

   public OdeMetadata setPayloadType(String payloadType) {
      this.payloadType = payloadType;
      return this;
   }
   
   public List<OdePayloadViolation> getViolations(){
	   return violations;
   }
   
   public OdeMetadata setViolations(List<OdePayloadViolation> violations){
	   this.violations = violations;
	   return this;
   }
   
   

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result
            + ((inputTopic == null) ? 0 : inputTopic.hashCode());
      result = prime * result + ((key == null) ? 0 : key.hashCode());
      result = prime * result
            + ((odeRequest == null) ? 0 : odeRequest.hashCode());
      result = prime * result
            + ((outputTopic == null) ? 0 : outputTopic.hashCode());
      result = prime * result
            + ((payloadType == null) ? 0 : payloadType.hashCode());
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
      OdeMetadata other = (OdeMetadata) obj;
      if (inputTopic == null) {
         if (other.inputTopic != null)
            return false;
      } else if (!inputTopic.equals(other.inputTopic))
         return false;
      if (key == null) {
         if (other.key != null)
            return false;
      } else if (!key.equals(other.key))
         return false;
      if (odeRequest == null) {
         if (other.odeRequest != null)
            return false;
      } else if (!odeRequest.equals(other.odeRequest))
         return false;
      if (outputTopic == null) {
         if (other.outputTopic != null)
            return false;
      } else if (!outputTopic.equals(other.outputTopic))
         return false;
      if (payloadType == null) {
         if (other.payloadType != null)
            return false;
      } else if (!payloadType.equals(other.payloadType))
         return false;
      return true;
   }

}
