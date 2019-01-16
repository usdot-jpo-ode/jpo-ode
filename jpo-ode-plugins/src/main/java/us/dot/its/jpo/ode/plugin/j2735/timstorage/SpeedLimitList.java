package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import com.fasterxml.jackson.annotation.JsonProperty;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class SpeedLimitList extends Asn1Object {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  @JsonProperty("RegulatorySpeedLimit")
  private RegulatorySpeedLimit[] RegulatorySpeedLimit;

  public RegulatorySpeedLimit[] getRegulatorySpeedLimit() {
    return RegulatorySpeedLimit;
  }

  public void setRegulatorySpeedLimit(RegulatorySpeedLimit[] regulatorySpeedLimit) {
    RegulatorySpeedLimit = regulatorySpeedLimit;
  }

}
