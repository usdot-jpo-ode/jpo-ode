package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import lombok.Data;
import lombok.EqualsAndHashCode;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

/**
 * A list of nodes in the XY plane.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class NodeListXY extends Asn1Object {
  private static final long serialVersionUID = 1L;

  private NodeSetXY nodes;
  private ComputedLane computed;
}
