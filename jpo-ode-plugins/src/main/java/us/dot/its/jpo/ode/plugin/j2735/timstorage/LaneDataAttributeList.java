package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

/**
 * A list of lane data attributes.
 */
@EqualsAndHashCode(callSuper = false)
public class LaneDataAttributeList extends Asn1Object {

  private static final long serialVersionUID = 1L;

  @JsonProperty("LaneDataAttribute")
  private LaneDataAttribute laneDataAttribute;

  public LaneDataAttribute getLaneDataAttribute() {
    return laneDataAttribute;
  }

  public void setLaneDataAttribute(LaneDataAttribute laneDataAttribute) {
    this.laneDataAttribute = laneDataAttribute;
  }
}
