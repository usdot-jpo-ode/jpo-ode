package us.dot.its.jpo.ode.model;

import us.dot.its.jpo.ode.util.DateTimeUtils;

public class OdeMsgMetadata extends OdeObject {

   public enum GeneratedBy {
      TMC, OBU, RSU
   }

   private static final long serialVersionUID = 3979762143291085955L;

   private static int staticSchemaVersion;

   private String payloadType;
   private SerialId serialId;
   private String odeReceivedAt;
   private int schemaVersion;
   private String recordGeneratedAt;
   private GeneratedBy recordGeneratedBy;
   private boolean sanitized = false;

   public OdeMsgMetadata() {
      this(OdeMsgPayload.class.getName(), new SerialId(), DateTimeUtils.now());
   }

   public OdeMsgMetadata(OdeMsgPayload payload) {
      this(payload, new SerialId(), DateTimeUtils.now());
   }

   private OdeMsgMetadata(OdeMsgPayload payload, SerialId serialId, String receivedAt) {
      this(payload.getClass().getName(), serialId, receivedAt);
   }

   public OdeMsgMetadata(String payloadType, SerialId serialId, String receivedAt) {
      super();
      this.schemaVersion = staticSchemaVersion;
      this.payloadType = payloadType;
      this.serialId = serialId;
      this.odeReceivedAt = receivedAt;
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

   public SerialId getSerialId() {
      return serialId;
   }

   public void setSerialId(SerialId serialId) {
      this.serialId = serialId;
   }

   public String getOdeReceivedAt() {
      return odeReceivedAt;
   }

   public void setOdeReceivedAt(String receivedAt) {
      this.odeReceivedAt = receivedAt;
   }

   public int getSchemaVersion() {
      return schemaVersion;
   }

   public void setSchemaVersion(int aSchemaVersion) {
      schemaVersion = aSchemaVersion;
   }

   public String getRecordGeneratedAt() {
      return recordGeneratedAt;
   }

   public void setRecordGeneratedAt(String recordGeneratedAt) {
      this.recordGeneratedAt = recordGeneratedAt;
   }

   public GeneratedBy getRecordGeneratedBy() {
      return recordGeneratedBy;
   }

   public void setRecordGeneratedBy(GeneratedBy recordGeneratedBy) {
      this.recordGeneratedBy = recordGeneratedBy;
   }

   public boolean isSanitized() {
      return sanitized;
   }

   public void setSanitized(boolean sanitized) {
      this.sanitized = sanitized;
   }

   
   public static int getStaticSchemaVersion() {
      return staticSchemaVersion;
   }

   public static void setStaticSchemaVersion(int aSchemaVersion) {
      staticSchemaVersion = aSchemaVersion;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((odeReceivedAt == null) ? 0 : odeReceivedAt.hashCode());
      result = prime * result + ((payloadType == null) ? 0 : payloadType.hashCode());
      result = prime * result + ((recordGeneratedAt == null) ? 0 : recordGeneratedAt.hashCode());
      result = prime * result + ((recordGeneratedBy == null) ? 0 : recordGeneratedBy.hashCode());
      result = prime * result + (sanitized ? 1231 : 1237);
      result = prime * result + schemaVersion;
      result = prime * result + ((serialId == null) ? 0 : serialId.hashCode());
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
      if (odeReceivedAt == null) {
         if (other.odeReceivedAt != null)
            return false;
      } else if (!odeReceivedAt.equals(other.odeReceivedAt))
         return false;
      if (payloadType == null) {
         if (other.payloadType != null)
            return false;
      } else if (!payloadType.equals(other.payloadType))
         return false;
      if (recordGeneratedAt == null) {
         if (other.recordGeneratedAt != null)
            return false;
      } else if (!recordGeneratedAt.equals(other.recordGeneratedAt))
         return false;
      if (recordGeneratedBy != other.recordGeneratedBy)
         return false;
      if (sanitized != other.sanitized)
         return false;
      if (schemaVersion != other.schemaVersion)
         return false;
      if (serialId == null) {
         if (other.serialId != null)
            return false;
      } else if (!serialId.equals(other.serialId))
         return false;
      return true;
   }

}
