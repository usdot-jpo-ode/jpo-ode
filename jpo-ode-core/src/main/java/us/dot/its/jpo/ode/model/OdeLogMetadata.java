package us.dot.its.jpo.ode.model;

public class OdeLogMetadata extends OdeMsgMetadata {

    private static final long serialVersionUID = -8601265839394150140L;
    
    protected String logFileName;

    public OdeLogMetadata() {
        super();
    }

    public String getLogFileName() {
        return logFileName;
    }

    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

	public OdeLogMetadata(OdeMsgPayload payload) {
		super(payload);
	}

}
