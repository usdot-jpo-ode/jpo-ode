package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735DataParameters extends Asn1Object {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String processMethod;
	private String processAgency;
	private String lastCheckedDate;
	private String geoidUsed;

	public String getProcessMethod() {
		return processMethod;
	}

	public void setProcessMethod(String processMethod) {
		this.processMethod = processMethod;
	}

	public String getProcessAgency() {
		return processAgency;
	}

	public void setProcessAgency(String processAgency) {
		this.processAgency = processAgency;
	}

	public String getLastCheckedDate() {
		return lastCheckedDate;
	}

	public void setLastCheckedDate(String lastCheckedDate) {
		this.lastCheckedDate = lastCheckedDate;
	}

	public String getGeoidUsed() {
		return geoidUsed;
	}

	public void setGeoidUsed(String geoidUsed) {
		this.geoidUsed = geoidUsed;
	}

}
