package us.dot.its.jpo.ode.model;


public class OdePsmMetadata extends OdeLogMetadata {

	private static final long serialVersionUID = 1L;
	
	public enum PsmSource {
		RSU, V2X, MMITSS, unknown
	}

	private PsmSource psmSource;
	private String originIp;

	public PsmSource getPsmSource() {
		return psmSource;
	}
	public OdePsmMetadata() {
		super();
	}

	public OdePsmMetadata(OdeMsgPayload payload) {
		super(payload);
	}

	public void setPsmSource(PsmSource psmSource) {
		this.psmSource = psmSource;
	}
	
	public String getOriginIp() {
		return originIp;
    }
 
    public void setOriginIp(String originIp) {
		this.originIp = originIp;
    }
}