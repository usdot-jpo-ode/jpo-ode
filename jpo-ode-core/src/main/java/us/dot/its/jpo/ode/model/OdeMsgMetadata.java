package us.dot.its.jpo.ode.model;

import javax.xml.bind.annotation.XmlRootElement;

import us.dot.its.jpo.ode.util.DateTimeUtils;

@XmlRootElement
public class OdeMsgMetadata extends OdeObject {

   private static final long serialVersionUID = 3979762143291085955L;

   private String payloadType;
   private SerialId serialId;
   private String receivedAt;
   private Integer schemaVersion = 2;
   
   
   public OdeMsgMetadata() {
       this(OdeDataType.Unknown.name(), new SerialId(), DateTimeUtils.now());
   }

   public OdeMsgMetadata(OdeMsgPayload payload) {
      this(payload, new SerialId(), DateTimeUtils.now());
   }

   public OdeMsgMetadata(OdeMsgPayload payload, 
                         SerialId serialId,
                         String receivedAt) {
       this(payload.getClass().getName(),
               serialId,
               receivedAt);
   }

    public OdeMsgMetadata(String payloadType, SerialId serialId, String receivedAt) {
        super();
        this.payloadType = payloadType;
        this.serialId = serialId;
        this.receivedAt = receivedAt;
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

    public String getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(String receivedAt) {
        this.receivedAt = receivedAt;
    }
    
    public Integer getSchemaVersion() {
       return schemaVersion;
    }

    public void setSchemaVersion(Integer schemaVersion) {
       this.schemaVersion = schemaVersion;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((payloadType == null) ? 0 : payloadType.hashCode());
        result = prime * result + ((serialId == null) ? 0 : serialId.hashCode());
        result = prime * result + ((receivedAt == null) ? 0 : receivedAt.hashCode());
        result = prime * result + ((schemaVersion == null) ? 0: schemaVersion.hashCode());
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
        if (schemaVersion == null) {
           if (other.schemaVersion != null) 
              return false;
        } else if (!schemaVersion.equals(other.schemaVersion))
           return false;
        return true;
    }

}
