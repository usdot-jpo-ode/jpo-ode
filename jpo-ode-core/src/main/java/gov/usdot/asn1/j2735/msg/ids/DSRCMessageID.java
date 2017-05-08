package gov.usdot.asn1.j2735.msg.ids;

public class DSRCMessageID extends ConnectedVehicleMessageID {

	private long dsrcMsgID = 0;
	
	public DSRCMessageID(long dsrcMsgID) {
		this.dsrcMsgID = dsrcMsgID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (dsrcMsgID ^ (dsrcMsgID >>> 32));
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
		DSRCMessageID other = (DSRCMessageID) obj;
		if (dsrcMsgID != other.dsrcMsgID)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DSRCMessageID [dsrcMsgID=" + dsrcMsgID + "]";
	}

	@Override
	public long getMessageId() {
		return dsrcMsgID;
	}

}
