package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import lombok.Data;
import lombok.EqualsAndHashCode;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

/**
 * Extent.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class Extent extends Asn1Object {
  private static final long serialVersionUID = 1L;

  /**
   * Enumerated values for Extent.
   */
  public enum ExtentEnum {
    useInstantlyOnly, // (0),
    useFor3meters, // (1),
    useFor10meters, // (2),
    useFor50meters, // (3),
    useFor100meters, // (4),
    useFor500meters, // (5),
    useFor1000meters, // (6),
    useFor5000meters, // (7),
    useFor10000meters, // (8),
    useFor50000meters, // (9),
    useFor100000meters, // (10),
    useFor500000meters, // (11),
    useFor1000000meters, // (12),
    useFor5000000meters, // (13),
    useFor10000000meters, // (14),
    forever // (15) -- very wide area
  }

  private String useInstantlyOnly; // (0),
  private String useFor3meters; // (1),
  private String useFor10meters; // (2),
  private String useFor50meters; // (3),
  private String useFor100meters; // (4),
  private String useFor500meters; // (5),
  private String useFor1000meters; // (6),
  private String useFor5000meters; // (7),
  private String useFor10000meters; // (8),
  private String useFor50000meters; // (9),
  private String useFor100000meters; // (10),
  private String useFor500000meters; // (11),
  private String useFor1000000meters; // (12),
  private String useFor5000000meters; // (13),
  private String useFor10000000meters; // (14),
  private String forever; // (15) -- very wide area
}
