package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import com.fasterxml.jackson.annotation.JsonProperty;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class LaneDataAttributeList extends Asn1Object {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  @JsonProperty ("LaneDataAttribute")
  private LaneDataAttribute LaneDataAttribute;

  public LaneDataAttribute getLaneDataAttribute() {
    return LaneDataAttribute;
  }

  public void setLaneDataAttribute(LaneDataAttribute laneDataAttribute) {
    LaneDataAttribute = laneDataAttribute;
  }
}
