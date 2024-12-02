package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import lombok.EqualsAndHashCode;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

@EqualsAndHashCode(callSuper = false)
public class RegulatorySpeedLimit extends Asn1Object {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private SpeedLimitType type;
  private int speed;
  
  public SpeedLimitType getType() {
    return type;
  }
  public void setType(SpeedLimitType type) {
    this.type = type;
  }
  public int getSpeed() {
    return speed;
  }
  public void setSpeed(int speed) {
    this.speed = speed;
  }
}
