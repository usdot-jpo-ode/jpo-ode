package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import lombok.Data;
import lombok.EqualsAndHashCode;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

/**
 * The speed limit for a given section of roadway.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class RegulatorySpeedLimit extends Asn1Object {

  private static final long serialVersionUID = 1L;

  private SpeedLimitType type;
  private int speed;
}
