package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class SpeedLimitType extends Asn1Object {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
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
  public String getUnknown() {
    return unknown;
  }
  public void setUnknown(String unknown) {
    this.unknown = unknown;
  }
  public String getMaxSpeedInSchoolZone() {
    return maxSpeedInSchoolZone;
  }
  public void setMaxSpeedInSchoolZone(String maxSpeedInSchoolZone) {
    this.maxSpeedInSchoolZone = maxSpeedInSchoolZone;
  }
  public String getMaxSpeedInSchoolZoneWhenChildrenArePresent() {
    return maxSpeedInSchoolZoneWhenChildrenArePresent;
  }
  public void setMaxSpeedInSchoolZoneWhenChildrenArePresent(String maxSpeedInSchoolZoneWhenChildrenArePresent) {
    this.maxSpeedInSchoolZoneWhenChildrenArePresent = maxSpeedInSchoolZoneWhenChildrenArePresent;
  }
  public String getMaxSpeedInConstructionZone() {
    return maxSpeedInConstructionZone;
  }
  public void setMaxSpeedInConstructionZone(String maxSpeedInConstructionZone) {
    this.maxSpeedInConstructionZone = maxSpeedInConstructionZone;
  }
  public String getVehicleMinSpeed() {
    return vehicleMinSpeed;
  }
  public void setVehicleMinSpeed(String vehicleMinSpeed) {
    this.vehicleMinSpeed = vehicleMinSpeed;
  }
  public String getVehicleMaxSpeed() {
    return vehicleMaxSpeed;
  }
  public void setVehicleMaxSpeed(String vehicleMaxSpeed) {
    this.vehicleMaxSpeed = vehicleMaxSpeed;
  }
  public String getVehicleNightMaxSpeed() {
    return vehicleNightMaxSpeed;
  }
  public void setVehicleNightMaxSpeed(String vehicleNightMaxSpeed) {
    this.vehicleNightMaxSpeed = vehicleNightMaxSpeed;
  }
  public String getTruckMinSpeed() {
    return truckMinSpeed;
  }
  public void setTruckMinSpeed(String truckMinSpeed) {
    this.truckMinSpeed = truckMinSpeed;
  }
  public String getTruckMaxSpeed() {
    return truckMaxSpeed;
  }
  public void setTruckMaxSpeed(String truckMaxSpeed) {
    this.truckMaxSpeed = truckMaxSpeed;
  }
  public String getTruckNightMaxSpeed() {
    return truckNightMaxSpeed;
  }
  public void setTruckNightMaxSpeed(String truckNightMaxSpeed) {
    this.truckNightMaxSpeed = truckNightMaxSpeed;
  }
  public String getVehiclesWithTrailersMinSpeed() {
    return vehiclesWithTrailersMinSpeed;
  }
  public void setVehiclesWithTrailersMinSpeed(String vehiclesWithTrailersMinSpeed) {
    this.vehiclesWithTrailersMinSpeed = vehiclesWithTrailersMinSpeed;
  }
  public String getVehiclesWithTrailersMaxSpeed() {
    return vehiclesWithTrailersMaxSpeed;
  }
  public void setVehiclesWithTrailersMaxSpeed(String vehiclesWithTrailersMaxSpeed) {
    this.vehiclesWithTrailersMaxSpeed = vehiclesWithTrailersMaxSpeed;
  }
  public String getVehiclesWithTrailersNightMaxSpeed() {
    return vehiclesWithTrailersNightMaxSpeed;
  }
  public void setVehiclesWithTrailersNightMaxSpeed(String vehiclesWithTrailersNightMaxSpeed) {
    this.vehiclesWithTrailersNightMaxSpeed = vehiclesWithTrailersNightMaxSpeed;
  }
}
