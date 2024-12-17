package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import lombok.Data;
import lombok.EqualsAndHashCode;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

/**
 * ValidRegion.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class ValidRegion extends Asn1Object {
  private static final long serialVersionUID = 1L;

  private String direction;
  private Extent extent;
  private Area area;
}
