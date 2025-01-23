package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import lombok.Data;
import lombok.EqualsAndHashCode;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

/**
 * A set of one or more regions.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class RegionPointSet extends Asn1Object {
  private static final long serialVersionUID = 1L;

  private Position anchor;
  private int scale;
  private RegionList regionList;
}
