package us.dot.its.jpo.ode.model;

public class OdeSsmMetadata extends OdeLogMetadata {

    public enum SsmSource {
		RSU, V2X, MMITSS, unknown
	}
    
    private String originIp;
    private SsmSource ssmSource;
    
    public OdeSsmMetadata(OdeMsgPayload payload) {
        super(payload);
    }

    public OdeSsmMetadata() {
        super();
    }

    public String getOriginIp() {
        return originIp;
    }
 
    public void setOriginIp(String originIp) {
        this.originIp = originIp;
    }

    public SsmSource getSsmSource() {
	    return ssmSource;
	}

    public void setSsmSource(SsmSource ssmSource) {
	    this.ssmSource = ssmSource;
	}
}
