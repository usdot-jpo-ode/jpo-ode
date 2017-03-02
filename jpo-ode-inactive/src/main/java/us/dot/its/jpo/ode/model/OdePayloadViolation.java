package us.dot.its.jpo.ode.model;

public class OdePayloadViolation {

	private String fieldName;
	private double validMin;
	private double validMax;

	public OdePayloadViolation() {
		super();
		fieldName = null;
		validMin = Double.NaN;
		validMax = Double.NaN;
	}
	
	public OdePayloadViolation(String fieldName, double validMin, double validMax) {
		super();
		this.fieldName = fieldName;
		this.validMin = validMin;
		this.validMax = validMax;
	}

	public String getFieldName(){
		return fieldName;
	}

	public OdePayloadViolation setFieldName(String fieldName){
		this.fieldName = fieldName;
		return this;
	}

	public double getValidMin(){
		return validMin;
	}

	public OdePayloadViolation setValidMin(double validMin){
		this.validMin = validMin;
		return this;
	}

	public double getValidMax(){
		return validMax;
	}

	public OdePayloadViolation setValidMax(double validMax){
		this.validMax = validMax;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fieldName == null) ? 0 : fieldName.hashCode());
		result = prime * result + ((validMin == Double.NaN) ? 0 : (int) validMin);
		result = prime * result
				+ ((validMax == Double.NaN) ? 0 : (int) validMax);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OdePayloadViolation other = (OdePayloadViolation) obj;
		if (fieldName == null) {
			if (other.fieldName != null)
				return false;
		} else if (!fieldName.equals(other.fieldName))
			return false;
		if (validMin == Double.NaN) {
			if (other.validMin != Double.NaN)
				return false;
		} else if (!(validMin == other.validMin))
			return false;
		if (validMax == Double.NaN) {
			if (other.validMax != Double.NaN)
				return false;
		} else if (!(validMax==other.validMax))
			return false;
		return true;
	}
}
