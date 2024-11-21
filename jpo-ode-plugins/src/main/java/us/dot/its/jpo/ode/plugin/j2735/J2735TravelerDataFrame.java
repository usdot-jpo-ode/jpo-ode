package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;
import us.dot.its.jpo.ode.plugin.j2735.timstorage.Content;

public class J2735TravelerDataFrame extends Asn1Object {
    private static final long serialVersionUID = 1L;

    private String notUsed;
    private J2735TravelerInfoType frameType;
    private J2735MsgId msgId;
    private String startYear;
    private String startTime;
    private String durationTime;
    private String priority;
    private String notUsed1;
    private J2735GeographicalPath[] regions;
    private String notUsed2;
    private String notUsed3;
    private Content content;
    private String url;

    public String getNotUsed() {
		return notUsed;
	}

	public void setNotUsed(String notUsed) {
		this.notUsed = notUsed;
	}

    public J2735TravelerInfoType getFrameType() {
		return frameType;
	}

	public void setFrameType(J2735TravelerInfoType frameType) {
		this.frameType = frameType;
	}

    public J2735MsgId getMsgId() {
		return msgId;
	}

	public void setMsgId(J2735MsgId msgId) {
		this.msgId = msgId;
	}

    public String getStartYear() {
		return startYear;
	}

	public void setStartYear(String startYear) {
		this.startYear = startYear;
	}

    public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

    public String getDurationTime() {
		return durationTime;
	}

	public void setDurationTime(String durationTime) {
		this.durationTime = durationTime;
	}

    public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

    public String getNotUsed1() {
		return notUsed1;
	}

	public void setNotUsed1(String notUsed1) {
		this.notUsed1 = notUsed1;
	}

    public J2735GeographicalPath[] getRegions() {
		return regions;
	}

	public void setRegions(J2735GeographicalPath[] regions) {
		this.regions = regions;
	}

    public String getNotUsed2() {
		return notUsed2;
	}

	public void setNotUsed2(String notUsed2) {
		this.notUsed2 = notUsed2;
	}

    public String getNotUsed3() {
		return notUsed3;
	}

	public void setNotUsed3(String notUsed3) {
		this.notUsed3 = notUsed3;
	}

    public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}

    public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
