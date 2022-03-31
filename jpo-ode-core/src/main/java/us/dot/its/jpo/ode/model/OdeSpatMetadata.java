package us.dot.its.jpo.ode.model;

import com.fasterxml.jackson.annotation.*;

public class OdeSpatMetadata extends OdeLogMetadata {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5361008186032548625L;

	public enum SpatSource {
		RSU, V2X, MMITSS, unknown
	}

	private SpatSource spatSource;
	private boolean isCertPresent;
	private String originIp;

	@JsonProperty("isCertPresent")
	public boolean getIsCertPresent() {
		return isCertPresent;
	}

	public void setIsCertPresent(boolean isCertPresent) {
		this.isCertPresent = isCertPresent;
	}

	public OdeSpatMetadata() {
		super();
	}

	public OdeSpatMetadata(OdeMsgPayload payload) {
		super(payload);
	}

	public OdeSpatMetadata(OdeMsgPayload payload, SerialId serialId, String receivedAt) {

	}

	public SpatSource getSpatSource() {
		return spatSource;
	}

	public void setSpatSource(SpatSource spatSource) {
		this.spatSource = spatSource;
	}

	public String getOriginIp() {
		return originIp;
    }
 
    public void setOriginIp(String originIp) {
		this.originIp = originIp;
    }

}
