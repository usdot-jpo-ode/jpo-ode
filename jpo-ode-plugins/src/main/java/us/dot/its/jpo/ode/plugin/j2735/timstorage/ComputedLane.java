package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import lombok.Data;
import lombok.EqualsAndHashCode;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

/**
 * A computed lane is a lane that is derived from a reference lane by applying
 * transformations to the reference lane.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class ComputedLane extends Asn1Object {
  private static final long serialVersionUID = 1L;

  private int referenceLaneId;
  private OffsetAxis offsetXaxis;
  private OffsetAxis offsetYaxis;
  private int rotateXY;
  private int scaleXaxis;
  private int scaleYaxis;
}
