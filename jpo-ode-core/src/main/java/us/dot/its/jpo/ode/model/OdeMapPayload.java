package us.dot.its.jpo.ode.model;

import us.dot.its.jpo.ode.plugin.j2735.J2735MAP;

public class OdeMapPayload extends OdeMsgPayload {

	   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OdeMapPayload() {
	        this(new J2735MAP());
	    }

	    public OdeMapPayload(J2735MAP map) {
	        super(map);
	        this.setData(map);
	    }

	    public J2735MAP getMap() {
	        return (J2735MAP) getData();
	    }

	    public void setMap(J2735MAP map) {
	    	setData(map);
	    }
}
