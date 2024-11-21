package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735Tim  extends Asn1Object {
    private static final long serialVersionUID = 1L;

    private String msgCnt;
    private String timeStamp;
    private String packetID;
    private String urlB;
    private J2735TravelerDataFrame[] dataFrames;

    public String getMsgCnt() {
		return msgCnt;
	}

	public void setMsgCnt(String msgCnt) {
		this.msgCnt = msgCnt;
	}

    public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

    public String getPacketID() {
		return packetID;
	}

	public void setPacketID(String packetID) {
		this.packetID = packetID;
	}

    public String getUrlB() {
		return urlB;
	}

	public void setUrlB(String urlB) {
		this.urlB = urlB;
	}

    public J2735TravelerDataFrame[] getDataFrames() {
		return dataFrames;
	}

	public void setDataFrames(J2735TravelerDataFrame[] dataFrames) {
		this.dataFrames = dataFrames;
	}
}
