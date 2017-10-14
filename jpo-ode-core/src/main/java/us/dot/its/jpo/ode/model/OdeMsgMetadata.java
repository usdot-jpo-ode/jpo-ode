package us.dot.its.jpo.ode.model;

import us.dot.its.jpo.ode.model.OdeMsgMetadata.GeneratedBy;
import us.dot.its.jpo.ode.util.DateTimeUtils;

public class OdeMsgMetadata extends OdeObject {

   public enum GeneratedBy {
   	TMC, OBU, RSU
	}

	private static final long serialVersionUID = 3979762143291085955L;

   private String payloadType;
   private SerialId serialId;
   private String odeReceivedAt;
   private Integer schemaVersion = 2;
   private String generatedAt;
   private GeneratedBy generatedBy;
   private boolean validSignature = false;
   private boolean sanitized = false;
   
   
   public OdeMsgMetadata() {
       this(OdeMsgPayload.class.getName(), new SerialId(), DateTimeUtils.now());
   }

   public OdeMsgMetadata(OdeMsgPayload payload) {
      this(payload, new SerialId(), DateTimeUtils.now());
   }

   private OdeMsgMetadata(OdeMsgPayload payload, 
                         SerialId serialId,
                         String receivedAt) {
       this(payload.getClass().getName(),
               serialId,
               receivedAt);
   }

    private OdeMsgMetadata(String payloadType, SerialId serialId, String receivedAt) {
        super();
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

    public String getReceivedAt() {
        return odeReceivedAt;
    }

    public void setReceivedAt(String receivedAt) {
        this.odeReceivedAt = receivedAt;
    }
    
    public Integer getSchemaVersion() {
       return schemaVersion;
    }

    public void setSchemaVersion(Integer schemaVersion) {
       this.schemaVersion = schemaVersion;
    }

    public String getGeneratedAt() {
		return generatedAt;
	}

	public void setGeneratedAt(String generatedAt) {
		this.generatedAt = generatedAt;
	}

	public GeneratedBy getGeneratedBy() {
		return generatedBy;
	}

	public void setGeneratedBy(GeneratedBy generatedBy) {
		this.generatedBy = generatedBy;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((generatedAt == null) ? 0 : generatedAt.hashCode());
		result = prime * result + ((generatedBy == null) ? 0 : generatedBy.hashCode());
		result = prime * result + ((payloadType == null) ? 0 : payloadType.hashCode());
		result = prime * result + ((odeReceivedAt == null) ? 0 : odeReceivedAt.hashCode());
		result = prime * result + (sanitized ? 1231 : 1237);
		result = prime * result + ((schemaVersion == null) ? 0 : schemaVersion.hashCode());
		result = prime * result + ((serialId == null) ? 0 : serialId.hashCode());
		result = prime * result + (validSignature ? 1231 : 1237);
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
		if (generatedAt == null) {
			if (other.generatedAt != null)
				return false;
		} else if (!generatedAt.equals(other.generatedAt))
			return false;
		if (generatedBy == null) {
			if (other.generatedBy != null)
				return false;
		} else if (!generatedBy.equals(other.generatedBy))
			return false;
		if (payloadType == null) {
			if (other.payloadType != null)
				return false;
		} else if (!payloadType.equals(other.payloadType))
			return false;
		if (odeReceivedAt == null) {
			if (other.odeReceivedAt != null)
				return false;
		} else if (!odeReceivedAt.equals(other.odeReceivedAt))
			return false;
		if (sanitized != other.sanitized)
			return false;
		if (schemaVersion == null) {
			if (other.schemaVersion != null)
				return false;
		} else if (!schemaVersion.equals(other.schemaVersion))
			return false;
		if (serialId == null) {
			if (other.serialId != null)
				return false;
		} else if (!serialId.equals(other.serialId))
			return false;
		if (validSignature != other.validSignature)
			return false;
		return true;
	}

}
