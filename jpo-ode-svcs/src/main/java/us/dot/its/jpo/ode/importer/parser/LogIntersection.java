package us.dot.its.jpo.ode.importer.parser;
/**
 * POJO class for TIM log file intersection data
 */
public class LogIntersection {
	private byte intersectionStatus;
	private int intersectionId;
	
	public LogIntersection() {
		super();
	}
	public byte getIntersectionStatus() {
		return intersectionStatus;
	}
	public void setIntersectionStatus(byte intersectionStatus) {
		this.intersectionStatus = intersectionStatus;
	}
	public int getIntersectionId() {
		return intersectionId;
	}
	public void setIntersectionId(int intersectionId) {
		this.intersectionId = intersectionId;
	}
	
}
