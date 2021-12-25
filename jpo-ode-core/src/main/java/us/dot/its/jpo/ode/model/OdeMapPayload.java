package us.dot.its.jpo.ode.model;

import com.fasterxml.jackson.annotation.*;

import us.dot.its.jpo.ode.plugin.j2735.J2735MAP;

public class OdeMapPayload extends OdeMsgPayload {

	   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OdeMapPayload() {
	        this(new J2735MAP());
	    }

		@JsonCreator
	    public OdeMapPayload( @JsonProperty("data") J2735MAP map) {
	        super(map);
	        this.setData(map);
	    }

		@JsonProperty("data")
	    public J2735MAP getMap() {
	        return (J2735MAP) getData();
	    }

	    public void setMap(J2735MAP map) {
	    	setData(map);
	    }
}
