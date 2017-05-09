package gov.usdot.asn1.j2735.msg.ids;

public abstract class ConnectedVehicleMessageID {

	public abstract boolean equals(Object other);
	public abstract int hashCode();
	public abstract String toString();
	public abstract long getMessageId();
}
