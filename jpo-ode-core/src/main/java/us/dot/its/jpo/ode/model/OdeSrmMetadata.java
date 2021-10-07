package us.dot.its.jpo.ode.model;

public class OdeSrmMetadata extends OdeLogMetadata {
    
    public enum SrmSource {
		RSU, V2X, MMITSS, unknown
	}
    
    private String originIp;
    private SrmSource srmSource;
    
    public OdeSrmMetadata(OdeMsgPayload payload) {
        super(payload);
    }

    public OdeSrmMetadata() {
        super();
    }

    public String getOriginIp() {
        return originIp;
    }
 
    public void setOriginIp(String originIp) {
        this.originIp = originIp;
    }

    public SrmSource getSrmSource() {
	    return srmSource;
	}

    public void setSrmSource(SrmSource srmSource) {
	    this.srmSource = srmSource;
	}
}
