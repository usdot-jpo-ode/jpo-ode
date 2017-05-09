package gov.usdot.asn1.j2735.msg.ids;

public class SEMIMessageID extends ConnectedVehicleMessageID {

	final private long dialogID;
	final private long seqID;
	final private int preambleSize;
	
	public SEMIMessageID(long dialogID, long seqID) {
		this(dialogID, seqID, 0);
	}
	
	public SEMIMessageID(long dialogID, long seqID, int preambleSize) {
		this.dialogID = dialogID;
		this.seqID = seqID;
		this.preambleSize = preambleSize;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (dialogID ^ (dialogID >>> 32));
		result = prime * result + (int) (seqID ^ (seqID >>> 32));
		result = prime * result + (int) (preambleSize ^ (preambleSize >>> 32));		
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
		SEMIMessageID other = (SEMIMessageID) obj;
		if (dialogID != other.dialogID)
			return false;
		if (seqID != other.seqID)
			return false;
		if (preambleSize != other.preambleSize)
			return false;		
		return true;
	}

	@Override
	public String toString() {
		return "SEMIMessageID [dialogID=" + dialogID + ", seqID=" + seqID + ", preambleSize=" + preambleSize + "]";
	}

	@Override
	public long getMessageId() {
		return dialogID;
	}
	
}
