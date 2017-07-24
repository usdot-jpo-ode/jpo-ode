package us.dot.its.jpo.ode.model;

public class OdeMessage extends OdeObject {

    private static final long serialVersionUID = 6381260328835278701L;

    private Integer schemaVersion;

    public OdeMessage() {
        super();
        this.schemaVersion = 1;
    }

    public Integer getSchemaVersion() {
        return schemaVersion;
    }

    public void setSchemaVersion(Integer schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((schemaVersion == null) ? 0 : schemaVersion.hashCode());
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
        OdeMessage other = (OdeMessage) obj;
        if (schemaVersion == null) {
            if (other.schemaVersion != null)
                return false;
        } else if (!schemaVersion.equals(other.schemaVersion))
            return false;
        return true;
    }

}
