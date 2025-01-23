package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

/**
 * The units of distance used in the message.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class DistanceUnits extends Asn1Object {
  private static final long serialVersionUID = 1L;

  /**
   * Enumeration of distance units.
   */
  public enum DistanceUnitsEnum {
    centimeter, // (0),
    cm2_5,      // (1), -- Steps of 2.5 centimeters
    decimeter,  // (2),
    meter,      // (3),
    kilometer,  // (4),
    foot,       // (5), -- US foot, 0.3048 meters exactly
    yard,       // (6), -- three US feet
    mile        // (7) -- US mile (5280 US feet)
  }

  private String centimeter;  // (0),
  @JsonProperty("cm2-5")
  private String cm2dot5;       // (1), -- Steps of 2.5 centimeters
  private String decimeter;   // (2),
  private String meter;       // (3),
  private String kilometer;   // (4),
  private String foot;        // (5), -- US foot, 0.3048 meters exactly
  private String yard;        // (6), -- three US feet
  private String mile;        // (7) -- US mile (5280 US feet)
}
