package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import lombok.Data;
import lombok.EqualsAndHashCode;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

/**
 * LaneDataAttribute.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class LaneDataAttribute extends Asn1Object {
  private static final long serialVersionUID = 1L;

  //-- adjusts final point/width slant
  //-- of the lane to align with the stop line
  private int pathEndPointAngle;
  //-- sets the canter of the road bed
  //-- from centerline point
  private int laneCrownPointCenter;
  //  -- sets the canter of the road bed
  //  -- from left edge
  private int laneCrownPointLeft;
  //  -- sets the canter of the road bed
  //  -- from right edge
  private int laneCrownPointRight;
  //  -- the angle or direction of another lane
  //  -- this is required to support Japan style
  //  -- when a merge point angle is required
  private int laneAngle;
  //  -- Reference regulatory speed limits
  //  -- used by all segments
  private SpeedLimitList speedLimits;
}
