package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735IntersectionAccessPoint extends Asn1Object {

    private static final long serialVersionUID = 1L;
    private Integer lane;
    private Integer approach;
    private Integer connection;

    public Integer getLane() {
        return lane;
    }

    public void setLane(Integer lane) {
        this.lane = lane;
    }

    public Integer getApproach() {
        return approach;
    }

    public void setApproach(Integer approach) {
        this.approach = approach;
    }

    public Integer getConnection() {
        return connection;
    }

    public void setConnection(Integer connection) {
        this.connection = connection;
    }
}
