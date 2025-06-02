package us.dot.its.jpo.ode.model;

import com.fasterxml.jackson.annotation.*;

import us.dot.its.jpo.ode.plugin.j2735.J2735PSM;

public class OdePsmPayload extends OdeMsgPayload {

	private static final long serialVersionUID = 1L;

	public OdePsmPayload() {
	        this(new J2735PSM());
	    }

		@JsonCreator
	    public OdePsmPayload( @JsonProperty("data") J2735PSM psm) {
	        super(psm);
	        this.setData(psm);
	    }

		@JsonProperty("data")
	    public J2735PSM getPsm() {
	        return (J2735PSM) getData();
	    }

	    public void setPsm(J2735PSM psm) {
	    	setData(psm);
	    }
}