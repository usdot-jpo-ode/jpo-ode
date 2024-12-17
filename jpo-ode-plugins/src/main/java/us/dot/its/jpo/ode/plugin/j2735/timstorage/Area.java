package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import lombok.Data;
import lombok.EqualsAndHashCode;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

/**
 * A geographic area.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class Area extends Asn1Object {
  private static final long serialVersionUID = 1L;

  private ShapePointSet shapepoint;
  private Circle circle;
  private RegionPointSet regionPoint;
}
