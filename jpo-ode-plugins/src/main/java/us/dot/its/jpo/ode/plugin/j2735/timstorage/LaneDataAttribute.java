package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import lombok.EqualsAndHashCode;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

@EqualsAndHashCode(callSuper = false)
public class LaneDataAttribute extends Asn1Object {

  /**
   *
   */
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

  public int getPathEndPointAngle() {
    return pathEndPointAngle;
  }

  public void setPathEndPointAngle(int pathEndPointAngle) {
    this.pathEndPointAngle = pathEndPointAngle;
  }

  public int getLaneCrownPointCenter() {
    return laneCrownPointCenter;
  }

  public void setLaneCrownPointCenter(int laneCrownPointCenter) {
    this.laneCrownPointCenter = laneCrownPointCenter;
  }

  public int getLaneCrownPointLeft() {
    return laneCrownPointLeft;
  }

  public void setLaneCrownPointLeft(int laneCrownPointLeft) {
    this.laneCrownPointLeft = laneCrownPointLeft;
  }

  public int getLaneCrownPointRight() {
    return laneCrownPointRight;
  }

  public void setLaneCrownPointRight(int laneCrownPointRight) {
    this.laneCrownPointRight = laneCrownPointRight;
  }

  public int getLaneAngle() {
    return laneAngle;
  }

  public void setLaneAngle(int laneAngle) {
    this.laneAngle = laneAngle;
  }

  public SpeedLimitList getSpeedLimits() {
    return speedLimits;
  }

  public void setSpeedLimits(SpeedLimitList speedLimits) {
    this.speedLimits = speedLimits;
  }
}
