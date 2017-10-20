package us.dot.its.jpo.ode.model;

public class OdeLogMetadata extends OdeMsgMetadata {

	private static final long serialVersionUID = -8601265839394150140L;

	private String logFileName;
	private String recordType;

	public OdeLogMetadata(OdeMsgPayload payload) {
		super(payload);
	}

	public OdeLogMetadata() {
		super();
	}

	public OdeLogMetadata(String payloadType, SerialId serialId, String receivedAt) {
		super(payloadType, serialId, receivedAt);
	}


	public String getLogFileName() {
		return logFileName;
	}

	public void setLogFileName(String logFileName) {
		this.logFileName = logFileName;
	}

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

}
