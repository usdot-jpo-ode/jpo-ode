package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import lombok.Data;
import lombok.EqualsAndHashCode;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

/**
 * A set of points in the XY plane that define a shape.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class ShapePointSet extends Asn1Object {
  private static final long serialVersionUID = 1L;

  private Position anchor;
  private int laneWidth;
  private DirectionOfUse directionality;
  private NodeListXY nodeList;
}
