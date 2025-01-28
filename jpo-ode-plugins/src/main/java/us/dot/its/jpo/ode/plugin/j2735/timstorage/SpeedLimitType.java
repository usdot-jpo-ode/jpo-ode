package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import lombok.Data;
import lombok.EqualsAndHashCode;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

/**
 * The type of speed limit.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class SpeedLimitType extends Asn1Object {
  private static final long serialVersionUID = 1L;

  /**
   * Enumerated type for speed limit types.
   */
  public enum SpeedLimitTypeEnum {
    unknown,                                    //-- Speed limit type not available
    maxSpeedInSchoolZone,                       //-- Only sent when the limit is active
    maxSpeedInSchoolZoneWhenChildrenArePresent, //-- Sent at any time
    maxSpeedInConstructionZone,                 //-- Used for work zones, incident zones, etc.
    //-- where a reduced speed is present
    vehicleMinSpeed,
    vehicleMaxSpeed,                            //-- Regulatory speed limit for general traffic
    vehicleNightMaxSpeed,
    truckMinSpeed,
    truckMaxSpeed,
    truckNightMaxSpeed,
    vehiclesWithTrailersMinSpeed,
    vehiclesWithTrailersMaxSpeed,
    vehiclesWithTrailersNightMaxSpeed
  }

  private String unknown;
  private String maxSpeedInSchoolZone;
  private String maxSpeedInSchoolZoneWhenChildrenArePresent;
  private String maxSpeedInConstructionZone;
  private String vehicleMinSpeed;
  private String vehicleMaxSpeed;
  private String vehicleNightMaxSpeed;
  private String truckMinSpeed;
  private String truckMaxSpeed;
  private String truckNightMaxSpeed;
  private String vehiclesWithTrailersMinSpeed;
  private String vehiclesWithTrailersMaxSpeed;
  private String vehiclesWithTrailersNightMaxSpeed;
}
