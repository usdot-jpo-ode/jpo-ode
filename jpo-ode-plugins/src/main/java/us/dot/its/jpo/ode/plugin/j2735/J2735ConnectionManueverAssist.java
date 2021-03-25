package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735ConnectionManueverAssist extends Asn1Object {
	private static final long serialVersionUID = 1L;
	private Integer connectionID;
	private Integer queueLength;
	private Integer availableStorageLength;
	private boolean waitOnStop;
	private boolean pedBicycleDetect;

	public Integer getConnectionID() {
		return connectionID;
	}

	public void setConnectionID(Integer connectionID) {
		this.connectionID = connectionID;
	}

	public Integer getQueueLength() {
		return queueLength;
	}

	public void setQueueLength(Integer queueLength) {
		this.queueLength = queueLength;
	}

	public Integer getAvailableStorageLength() {
		return availableStorageLength;
	}

	public void setAvailableStorageLength(Integer availableStorageLength) {
		this.availableStorageLength = availableStorageLength;
	}

	public boolean getWaitOnStop() {
		return waitOnStop;
	}

	public void setWaitOnStop(boolean waitOnStop) {
		this.waitOnStop = waitOnStop;
	}

	public boolean getPedBicycleDetect() {
		return pedBicycleDetect;
	}

	public void setPedBicycleDetect(boolean pedBicycleDetect) {
		this.pedBicycleDetect = pedBicycleDetect;
	}

}
