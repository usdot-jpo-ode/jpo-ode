package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735TimeChangeDetails extends Asn1Object {
	private static final long serialVersionUID = 1L;
	private Integer startTime;
	private Integer minEndTime;
	private Integer maxEndTime;
	private Integer likelyTime;
	private Integer confidence;
	private Integer nextTime;

	public Integer getStartTime() {
		return startTime;
	}

	public void setStartTime(Integer startTime) {
		this.startTime = startTime;
	}

	public Integer getMinEndTime() {
		return minEndTime;
	}

	public void setMinEndTime(Integer minEndTime) {
		this.minEndTime = minEndTime;
	}

	public Integer getMaxEndTime() {
		return maxEndTime;
	}

	public void setMaxEndTime(Integer maxEndTime) {
		this.maxEndTime = maxEndTime;
	}

	public Integer getLikelyTime() {
		return likelyTime;
	}

	public void setLikelyTime(Integer likelyTime) {
		this.likelyTime = likelyTime;
	}

	public Integer getConfidence() {
		return confidence;
	}

	public void setConfidence(Integer confidence) {
		this.confidence = confidence;
	}

	public Integer getNextTime() {
		return nextTime;
	}

	public void setNextTime(Integer nextTime) {
		this.nextTime = nextTime;
	}

}
