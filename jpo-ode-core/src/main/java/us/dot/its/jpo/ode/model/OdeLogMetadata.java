package us.dot.its.jpo.ode.model;

public class OdeLogMetadata extends OdeMsgMetadata {

	private static final long serialVersionUID = -8601265839394150140L;

	private String logFileName;
	private String recordType;
   private ReceivedMessageDetails receivedMessageDetails;
   
   public ReceivedMessageDetails getReceivedMessageDetails() {
      return receivedMessageDetails;
   }

   public void setReceivedMessageDetails(ReceivedMessageDetails receivedMessageDetails) {
      this.receivedMessageDetails = receivedMessageDetails;
   }

	public OdeLogMetadata(OdeMsgPayload payload) {
		super(payload);
	}

	public OdeLogMetadata() {
		super();
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
