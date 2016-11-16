/*******************************************************************************
 * Copyright (c) 2015 US DOT - Joint Program Office
 *
 * The Government has unlimited rights to all documents/material produced under 
 * this task order. All documents and materials, to include the source code of 
 * any software produced under this contract, shall be Government owned and the 
 * property of the Government with all rights and privileges of ownership/copyright 
 * belonging exclusively to the Government. These documents and materials may 
 * not be used or sold by the Contractor without written permission from the CO.
 * All materials supplied to the Government shall be the sole property of the 
 * Government and may not be used for any other purpose. This right does not 
 * abrogate any other Government rights.
 *
 * Contributors:
 *     Booz | Allen | Hamilton - initial API and implementation
 *******************************************************************************/
package us.dot.its.jpo.ode.model;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.List;

import com.bah.ode.asn.oss.dsrc.AccelerationSet4Way;
import com.bah.ode.asn.oss.dsrc.BrakeSystemStatus;
import com.bah.ode.asn.oss.dsrc.Heading;
import com.bah.ode.asn.oss.dsrc.SteeringWheelAngle;
import com.bah.ode.asn.oss.dsrc.TransmissionAndSpeed;
import com.bah.ode.asn.oss.dsrc.VehicleSize;
import com.bah.ode.asn.oss.semi.Capacity;
import com.bah.ode.asn.oss.semi.ElectricVeh;
import com.bah.ode.asn.oss.semi.Emissions;
import com.bah.ode.asn.oss.semi.Environmental;
import com.bah.ode.asn.oss.semi.FuelConsumption;
import com.bah.ode.asn.oss.semi.FuelEconomy;
import com.bah.ode.asn.oss.semi.FundamentalSituationalStatus;
import com.bah.ode.asn.oss.semi.GroupID;
import com.bah.ode.asn.oss.semi.Range;
import com.bah.ode.asn.oss.semi.StateOfCharge;
import com.bah.ode.asn.oss.semi.VehSitRecord;
import com.bah.ode.asn.oss.semi.VehicleSituationStatus;
import com.bah.ode.asn.oss.semi.VehicleSituationStatus.TirePressure;
import com.bah.ode.asn.oss.semi.VsmEventFlag;
import com.bah.ode.asn.oss.semi.Weather;
import com.bah.ode.asn.oss.semi.Weather.WeatherReport;
import com.bah.ode.asn.oss.semi.Weather.Wipers;

import us.dot.its.jpo.ode.asn.OdeDateTime;
import us.dot.its.jpo.ode.asn.OdeGeoRegion;
import us.dot.its.jpo.ode.asn.OdePosition3D;
import us.dot.its.jpo.ode.util.CodecUtils;
import us.dot.its.jpo.ode.util.DateTimeUtils;
import us.dot.its.jpo.ode.util.GeoUtils;

public final class OdeVehicleDataFlat extends OdeData implements HasPosition, HasTimestamp{
   private static final long serialVersionUID = -7170326566884675515L;

   public enum TransmissionState {
      neutral,       //Neutral, speed relative to the vehicle alignment
      park,          //Park, speed relative the to vehicle alignment
      forwardGears,  //Forward gears, speed relative the to vehicle alignment
      reverseGears,  //Reverse gears, speed relative the to vehicle alignment 
      reserved1,
      reserved2,
      reserved3,      
      unavailable;   //not-equipped or unavailable value,
   }
   
   private String groupId;

   private Integer evCap;
   private Integer evRange;
   private Integer evSOC;

   private Integer EnvEmiss;
   private Integer EnvFuelCond;
   private Integer EnvFuelEcon;

   private BigDecimal accelLat;
   private BigDecimal accelLong;
   private BigDecimal accelVert;
   private BigDecimal accelYaw;
   
   private Byte brakesApplied;
   private Byte brakesTraction;
   private Byte brakesABS;
   private Byte brakesSCS;
   private Byte brakesBBA;
   private Byte BrakesAux;
   
   private BigDecimal heading;
   
   private TransmissionState transmission;
   private BigDecimal speed;

   private BigDecimal steeringAngle;

   private Integer sizeLength; //(0..16383) -- LSB units are 1 cm
   private Integer sizeWidth;  //(0..1023) -- LSB units are 1 cm
   
   private Byte eventFlag;

   private BigDecimal latitude;  // in degrees
   private BigDecimal longitude; // in 1/10th micro degrees
   private BigDecimal elevation; // in 0.1 meters (decimeters)

   private String tempId; 

   private Integer year;
   private Integer month;
   private Integer day;
   private Integer hour;
   private Integer minute;
   private BigDecimal second;
   private String  dateTime;

   private Integer lights;  
   private BigDecimal  throttlePos; //(0..200) -- LSB units are 0.5 percent
   private Integer tirePressureLF;  //(0..1000)
   private Integer tirePressureLR;  //(0..1000)
   private Integer tirePressureRF;  //(0..1000)
   private Integer tirePressureRR;  //(0..1000)

   private Integer weatherAirPres;
   private Integer weatherAirTemp;
   private Long    weatherIsRaining;
   private Integer weatherRainRate;
   private Long    weatherPrecipSituation;
   private Integer weatherSolarRadiation;
   private Integer weatherFriction;
   
   private Long    wipersStatusFrnt;
   private Integer wipersRateFrnt;
   private Long    wipersStatusRear;
   private Integer wipersRateRear;   
   
   private String roadSeg = ""; // Required for Aggregator, hence initialized so it appears in JSON

   public OdeVehicleDataFlat() {
      super();
   }

   public OdeVehicleDataFlat(String streamId, long bundleId, long recordId) {
      super(streamId, bundleId, recordId);
   }

   public OdeVehicleDataFlat(String serialId) {
      super(serialId);
   }

   public OdeVehicleDataFlat(String serialId, GroupID groupId, VehSitRecord vsr) {
      super(serialId);
      
      setGroupId(CodecUtils.toHex(groupId.byteArrayValue()));
      
      if (vsr.elveh != null)
         setElectricVehicle(vsr.elveh);
      
      if (vsr.env != null)
         setEnv(vsr.env);
      
      if (vsr.fundamental != null)
         setFundamental(vsr.fundamental);
      
      if (vsr.pos != null) {
         OdePosition3D pos3d = new OdePosition3D(vsr.pos);
         setLatitude(pos3d.getLatitude());
         setLongitude(pos3d.getLongitude());
         setElevation(pos3d.getElevation());
      }

      setTempId(CodecUtils.toHex(vsr.tempID.byteArrayValue()));
      
      if (vsr.time != null) {
         OdeDateTime odt = new OdeDateTime(vsr.time);
         setYear(odt.getYear());
         setMonth(odt.getMonth());
         setDay(odt.getDay());
         setHour(odt.getHour());
         setMinute(odt.getMinute());
         setSecond(odt.getSecond());

         setDateTime(odt.getISODateTime());
      }
      
      if (vsr.vehstat != null)
         setVehicleStatus(vsr.vehstat);
      
      if (vsr.weather != null)
         setWeather(vsr.weather);
   }

   private void setWeather(Weather weather) {
      if (weather != null) {
         //    Weather ::= SEQUENCE {
         //    wipers SEQUENCE {
         //       statFrnt DSRC.WiperStatusFront,
         //       rateFrnt DSRC.WiperRate    OPTIONAL,
         //       statRear DSRC.WiperStatusRear OPTIONAL,
         //       rateRear DSRC.WiperRate       OPTIONAL
         //    } OPTIONAL,
         //    airTemp     DSRC.AmbientAirTemperature OPTIONAL,
         //    airPres     DSRC.AmbientAirPressure    OPTIONAL,
         //    weatherReport SEQUENCE {    
         //       isRaining        NTCIP.EssPrecipYesNo,
         //       rainRate         NTCIP.EssPrecipRate       OPTIONAL,
         //       precipSituation  NTCIP.EssPrecipSituation  OPTIONAL,
         //       solarRadiation   NTCIP.EssSolarRadiation   OPTIONAL,
         //       friction         NTCIP.EssMobileFriction   OPTIONAL
         //    } OPTIONAL,
         //    ...   -- # Additional Data Elements
         // }
         
         // AmbientAirPressure ::= INTEGER (0..255) 
         //       -- 8 Bits in hPa starting at 580 with a resolution of 
         //       -- 2 hPa resulting in a range of 580 to 1090
         setWeatherAirPres(weather.airPres != null ? 580 + (weather.airPres.intValue() * 2) : null);
         
         // AmbientAirTemperature ::= INTEGER (0..191) -- in deg C with a -40 offset
         setWeatherAirTemp(weather.airTemp != null ? weather.airTemp.intValue() - 40 : null);
         
         setWeatherReport(weather.weatherReport);
         setWipers(weather.wipers);
      }
   }

   private void setWeatherReport(WeatherReport weatherReport) {
      if (weatherReport != null) {
         //    weatherReport SEQUENCE {    
         //       isRaining        NTCIP.EssPrecipYesNo,
         //       rainRate         NTCIP.EssPrecipRate       OPTIONAL,
         //       precipSituation  NTCIP.EssPrecipSituation  OPTIONAL,
         //       solarRadiation   NTCIP.EssSolarRadiation   OPTIONAL,
         //       friction         NTCIP.EssMobileFriction   OPTIONAL
         //    } OPTIONAL,
         
         // EssMobileFriction ::= INTEGER (0..101)
         setWeatherFriction(weatherReport.friction != null ? weatherReport.friction.intValue() : null);
   
         // EssPrecipYesNo ::= ENUMERATED {precip (1), noPrecip (2), error (3)} 
         setWeatherIsRaining(weatherReport.isRaining != null ? weatherReport.isRaining.longValue() : null);
   
         // EssPrecipSituation ::= ENUMERATED {
         //    other (1), 
         //    unknown (2), 
         //    noPrecipitation (3), 
         //    unidentifiedSlight (4), 
         //    unidentifiedModerate (5), 
         //    unidentifiedHeavy (6), 
         //    snowSlight (7), 
         //    snowModerate (8), 
         //    snowHeavy (9), 
         //    rainSlight (10), 
         //    rainModerate (11), 
         //    rainHeavy (12), 
         //    frozenPrecipitationSlight (13), 
         //    frozenPrecipitationModerate (14), 
         //    frozenPrecipitationHeavy (15)
         //    }
         setWeatherPrecipSituation(weatherReport.precipSituation != null ? weatherReport.precipSituation.longValue() : null);
   
         // EssPrecipRate ::= INTEGER (0..65535) 
         setWeatherRainRate(weatherReport.rainRate != null ? weatherReport.rainRate.intValue() : null);
   
         // EssSolarRadiation ::= INTEGER (0..65535) 
         setWeatherSolarRadiation(weatherReport.solarRadiation != null ? weatherReport.solarRadiation.intValue() : null);
      }
   }

   private void setWipers(Wipers wipers) {
      if (wipers != null) {
         //    wipers SEQUENCE {
         //       statFrnt DSRC.WiperStatusFront,
         //       rateFrnt DSRC.WiperRate    OPTIONAL,
         //       statRear DSRC.WiperStatusRear OPTIONAL,
         //       rateRear DSRC.WiperRate       OPTIONAL
         //    } OPTIONAL,
         
         // WiperStatusFront ::= ENUMERATED {
         //    unavailable         (0), -- Not Equipped with wiper status
         //                             -- or wiper status is unavailable
         //    off                 (1),  
         //    intermittent        (2), 
         //    low                 (3),
         //    high                (4),
         //    washerInUse       (126), -- washing solution being used
         //    automaticPresent  (127), -- Auto wiper equipped
         //    ... -- # LOCAL_CONTENT
         //    }
         // WiperStatusRear ::= ENUMERATED {
         //    unavailable         (0), -- Not Equipped with wiper status
         //                             -- or wiper status is unavailable
         //    off                 (1),  
         //    intermittent        (2), 
         //    low                 (3),
         //    high                (4),
         //    washerInUse       (126), -- washing solution being used
         //    automaticPresent  (127), -- Auto wipper equipped
         //    ... -- # LOCAL_CONTENT
         //    }
         // WiperRate ::= INTEGER (0..127) -- units of sweeps per minute
         setWipersRateFrnt(wipers.rateFrnt != null ? wipers.rateFrnt.intValue() : null);
         setWipersRateRear(wipers.rateRear != null ? wipers.rateRear.intValue() : null);
         setWipersStatusFrnt(wipers.statFrnt != null ? wipers.statFrnt.longValue() : null);
         setWipersStatusRear(wipers.statRear != null ? wipers.statRear.longValue() : null);
      }
   }

   private void setVehicleStatus(VehicleSituationStatus vehstat) {
      if (vehstat != null) {
         //    VehicleSituationStatus ::=  SEQUENCE {
         //    lights         DSRC.ExteriorLights,
         //    throttlePos    DSRC.ThrottlePosition      OPTIONAL,
         //    tirePressure   SEQUENCE {
         //       leftFront   DSRC.TirePressure,
         //       leftRear DSRC.TirePressure,
         //       rightFront  DSRC.TirePressure,
         //       rightRear   DSRC.TirePressure
         //    }  OPTIONAL,
         //    ... -- # Additional Data Elements
         // }
          
         // ExteriorLights ::= INTEGER (0..256) 
         //       -- With bits as defined:
         //          allLightsOff              ExteriorLights ::= 0  
         //                                    -- B'0000-0000  
         //          lowBeamHeadlightsOn       ExteriorLights ::= 1  
         //                                    -- B'0000-0001
         //          highBeamHeadlightsOn      ExteriorLights ::= 2 
         //                                    -- B'0000-0010
         //          leftTurnSignalOn          ExteriorLights ::= 4  
         //                                    -- B'0000-0100
         //          rightTurnSignalOn         ExteriorLights ::= 8  
         //                                    -- B'0000-1000
         //          hazardSignalOn            ExteriorLights ::= 12  
         //                                    -- B'0000-1100
         //          automaticLightControlOn   ExteriorLights ::= 16  
         //                                    -- B'0001-0000
         //          daytimeRunningLightsOn    ExteriorLights ::= 32  
         //                                    -- B'0010-0000
         //          fogLightOn                ExteriorLights ::= 64 
         //                                    -- B'0100-0000
         //          parkingLightsOn           ExteriorLights ::= 128  
         //                                    -- B'1000-0000
         setLights(vehstat.lights != null ? vehstat.lights.intValue() : null);
         // ThrottlePosition ::= INTEGER (0..200) -- LSB units are 0.5 percent
         if (vehstat.throttlePos != null)
            setThrottlePos(BigDecimal.valueOf(
                  vehstat.throttlePos.intValue() * 5, 1));
         setTirePressure(vehstat.tirePressure);
      }
   }

   private void setTirePressure(TirePressure tirePressure) {
      if (tirePressure != null) {
         //    tirePressure   SEQUENCE {
         //       leftFront   DSRC.TirePressure,
         //       leftRear DSRC.TirePressure,
         //       rightFront  DSRC.TirePressure,
         //       rightRear   DSRC.TirePressure
         //    }  OPTIONAL,
         //    ... -- # Additional Data Elements
         // }
         // TirePressure ::= INTEGER (0..1000)
         setTirePressureLF(tirePressure.leftFront != null ? tirePressure.leftFront
               .intValue() : null);
         setTirePressureLR(tirePressure.leftRear != null ? tirePressure.leftRear
               .intValue() : null);
         setTirePressureRF(tirePressure.rightFront != null ? tirePressure.rightFront
               .intValue() : null);
         setTirePressureRR(tirePressure.rightRear != null ? tirePressure.rightRear
               .intValue() : null);
      }
   }

   private void setFundamental(FundamentalSituationalStatus fundamental) {
      if (fundamental != null) {
         setAccelSet(fundamental.accelSet);
         setBreakes(fundamental.brakes);
         setHeading(fundamental.heading);
         setTransmissionAndSpeed(fundamental.speed);
         setSteeringAngle(fundamental.steeringAngle);
         setVehSize(fundamental.vehSize);
         setVsmEventFlag(fundamental.vsmEventFlag);
      }
   }

   private void setVsmEventFlag(VsmEventFlag vsmEventFlag) {
      setEventFlag(vsmEventFlag != null ? vsmEventFlag.byteArrayValue()[0] : null);
   }

   private void setVehSize(VehicleSize vehSize) {
      if (vehSize != null) {
         setSizeLength(vehSize.length != null ? vehSize.length.intValue()
               : null);
         setSizeWidth(vehSize.width != null ? vehSize.width.intValue() : null);
      }
   }

   private void setSteeringAngle(SteeringWheelAngle steeringWhealAngle) {
   //    SteeringWheelAngle ::= OCTET STRING (SIZE(1)) 
   //    -- LSB units of 1.5 degrees.  
   //    -- a range of -189 to +189 degrees
   //    -- 0x01 = 00 = +1.5 deg
   //    -- 0x81 = -126 = -189 deg and beyond
   //    -- 0x7E = +126 = +189 deg and beyond
   //    -- 0x7F = +127 to be used for unavailable
      if (steeringWhealAngle != null && steeringWhealAngle.byteArrayValue().length >= 1) {
         byte angle = steeringWhealAngle.byteArrayValue()[0];
         if (angle == 0x7F)
            this.steeringAngle = null;
         else
            setSteeringAngle(BigDecimal.valueOf(angle * 15, 1));
      }
   }

   private void setHeading(Heading heading2) {
      if (heading2 != null) {
         //    Heading ::= INTEGER (0..28800) 
         //    -- LSB of 0.0125 degrees
         //    -- A range of 0 to 359.9875 degrees
         setHeading(BigDecimal.valueOf(heading2.intValue() * 125, 4));
      }
   }

   private void setBreakes(BrakeSystemStatus brakes) {
      if (brakes != null && brakes.getSize() >= 2) {
         //      BrakeSystemStatus ::= OCTET STRING (SIZE(2))
         //            -- Encoded with the packed content of: 
         //            -- SEQUENCE {
         //            --   wheelBrakes        BrakeAppliedStatus,
         //            --                      -x- 4 bits
         //            --   wheelBrakesUnavailable  BOOL
         //            --                      -x- 1 bit (1=true)
         //            --   spareBit
         //            --                      -x- 1 bit, set to zero
         //            --   traction           TractionControlState,
         //            --                      -x- 2 bits
         //            --   abs                AntiLockBrakeStatus, 
         //            --                      -x- 2 bits
         //            --   scs                StabilityControlStatus,
         //            --                      -x- 2 bits
         //            --   brakeBoost         BrakeBoostApplied, 
         //            --                      -x- 2 bits
         //            --   auxBrakes          AuxiliaryBrakeStatus,
         //            --                      -x- 2 bits
         //            --   }
         
         int i = brakes.byteArrayValue()[0];
         byte b = (byte) ((i >> 4) & 0x0F);
         //       BrakeAppliedStatus ::= BIT STRING {
         //          allOff      (0), -- B'0000  The condition All Off 
         //          leftFront   (1), -- B'0001  Left Front Active
         //          leftRear    (2), -- B'0010  Left Rear Active
         //          rightFront  (4), -- B'0100  Right Front Active
         //          rightRear   (8)  -- B'1000  Right Rear Active
         //      } -- to fit in 4 bits
         setBrakesApplied(b);

         //      TractionControlState ::= ENUMERATED {
         //         unavailable (0), -- B'00  Not Equipped with tracton control 
         //                          -- or tracton control status is unavailable
         //         off         (1), -- B'01  tracton control is Off
         //         on          (2), -- B'10  tracton control is On (but not Engaged)
         //         engaged     (3)  -- B'11  tracton control is Engaged
         b = (byte) (i & 0x03);
         if ( b != 0) {
            setBrakesTraction(b);
         }
         
         //      AntiLockBrakeStatus ::= ENUMERATED {
         //         unavailable (0), -- B'00  Vehicle Not Equipped with ABS 
         //                          -- or ABS status is unavailable
         //         off         (1), -- B'01  Vehicle's ABS is Off
         //         on          (2), -- B'10  Vehicle's ABS is On (but not engaged)
         //         engaged     (3)  -- B'11  Vehicle's ABS is Engaged
         //         } 
         //         -- Encoded as a 2 bit value
         i = brakes.byteArrayValue()[1];
         b = (byte) ((i >> 6) & 0x03);
         if ( b != 0) {
            setBrakesABS(b);
         }
         
         //      StabilityControlStatus ::= ENUMERATED {
         //         unavailable (0), -- B'00  Not Equipped with SC
         //                          -- or SC status is unavailable
         //         off         (1), -- B'01  Off
         //         on          (2)  -- B'10  On or active (engaged)
         //         } 
         //         -- Encoded as a 2 bit value
         b = (byte) ((i >> 4) & 0x03);
         if ( b != 0) {
            setBrakesSCS(b);
         }
         //      BrakeBoostApplied ::= ENUMERATED {
         //         unavailable   (0), -- Vehicle not equipped with brake boost
         //                            -- or brake boost data is unavailable
         //         off           (1), -- Vehicle's brake boost is off
         //         on            (2)  -- Vehicle's brake boost is on (applied)
         //         }
         //         -- Encoded as a 2 bit value
         b = (byte) ((i >> 2) & 0x03);
         if ( b != 0) {
            setBrakesBBA(b);
         }
         
         //      AuxiliaryBrakeStatus ::= ENUMERATED {
         //         unavailable (0), -- B'00  Vehicle Not Equipped with Aux Brakes 
         //                          -- or Aux Brakes status is unavailable
         //         off         (1), -- B'01  Vehicle's Aux Brakes are Off
         //         on          (2), -- B'10  Vehicle's Aux Brakes are On ( Engaged )
         //         reserved    (3)  -- B'11 
         //         } 
         //         -- Encoded as a 2 bit value
         b = (byte) (i & 0x03);
         if ( b != 0) {
            setBrakesAux(b);
         }
      }
   }

   private void setAccelSet(AccelerationSet4Way accelSet) {
      if (accelSet != null && accelSet.getSize() >= 7) {
         //    AccelerationSet4Way ::= OCTET STRING (SIZE(7)) 
         //    -- composed of the following:
         //    -- SEQUENCE {
         //    --    long Acceleration,          -x- Along the Vehicle Longitudinal axis
         //    --    lat  Acceleration,          -x- Along the Vehicle Lateral axis
         //    --    vert VerticalAcceleration,  -x- Along the Vehicle Vertical axis
         //    --    yaw  YawRate
         //    --    }
         
         //Acceleration ::= INTEGER (-2000..2001) 
         //    -- LSB units are 0.01 m/s^2
         //   -- the value 2000 shall be used for values greater than 2000     
         //   -- the value -2000 shall be used for values less than -2000  
         //   -- a value of 2001 shall be used for Unavailable
         ByteBuffer bb = ByteBuffer.wrap(accelSet.byteArrayValue()).order(ByteOrder.BIG_ENDIAN);
         short accel = bb.getShort();
         if (accel != 2001)
            setAccelLong(BigDecimal.valueOf(accel, 2));

         accel = bb.getShort();
         if (accel != 2001)
            setAccelLat(BigDecimal.valueOf(accel, 2));
         
         //VerticalAcceleration ::= INTEGER (-127..127) 
         //    -- LSB units of 0.02 G steps over 
         //    -- a range +1.54 to -3.4G 
         //    -- and offset by 50  Value 50 = 0g, Value 0 = -1G
         //    -- value +127 = 1.54G, 
         //    -- value -120 = -3.4G
         //    -- value -121 for ranges -3.4 to -4.4G
         //    -- value -122 for ranges -4.4 to -5.4G
         //    -- value -123 for ranges -5.4 to -6.4G
         //    -- value -124 for ranges -6.4 to -7.4G
         //    -- value -125 for ranges -7.4 to -8.4G
         //    -- value -126 for ranges larger than -8.4G
         //    -- value -127 for unavailable data
         byte accelV = bb.get();
         if (accelV != -127)
            setAccelVert(BigDecimal.valueOf((accelV - 50) * 2, 2));
         
         //YawRate ::= INTEGER (-32767..32767) 
         //    -- LSB units of 0.01 degrees per second (signed)
         accel = bb.getShort();
         if (accel < 32767)
            setAccelYaw(BigDecimal.valueOf(accel, 2));

      }
   }

   private void setEnv(Environmental env) {
      if (env != null) {
         setEnvEmiss(env.emiss);
         setEnvFuelCond(env.fuelCon);
         setEnvFuelEcon(env.fuelEcon);
      }
   }

   private void setEnvEmiss(Emissions emiss) {
      if (emiss != null) {
         setEnvEmiss(emiss.intValue());
      }
   }

   private void setEnvFuelCond(FuelConsumption fuelCon) {
      if (fuelCon != null) {
         setEnvFuelCond(fuelCon.intValue());
      }
   }

   private void setEnvFuelEcon(FuelEconomy fuelEcon) {
      if (fuelEcon != null) {
         setEnvFuelEcon(fuelEcon.intValue());
      } 
   }

   private void setElectricVehicle(ElectricVeh elveh) {
      if (elveh != null) {
         setEvCap(elveh.cap);
         setEvRange(elveh.range);
         setEvSOC(elveh.soc);
      }
   }

   private void setEvSOC(StateOfCharge soc) {
      if (soc != null) {
         setEvSOC(soc.intValue());
      }
   }

   private void setEvRange(Range range) {
      if (range != null) {
         setEvRange(range.intValue());
      }
   }

   private void setEvCap(Capacity cap) {
      if (cap != null) {
         setEvCap(cap.intValue());
      }
   }

//   TransmissionAndSpeed ::= OCTET STRING (SIZE(2)) 
//         -- Bits 14~16 to be made up of the data element
//         -- DE_TransmissionState 
//         -- Bits 1~13 to be made up of the data element
//         -- DE_Speed
//   Speed ::= INTEGER (0..8191) -- Units of 0.02 m/s
//         -- The value 8191 indicates that 
//         -- speed is unavailable
//   TransmissionState ::= ENUMERATED {
//      neutral         (0), -- Neutral, speed relative to the vehicle alignment
//      park            (1), -- Park, speed relative the to vehicle alignment
//      forwardGears    (2), -- Forward gears, speed relative the to vehicle alignment
//      reverseGears    (3), -- Reverse gears, speed relative the to vehicle alignment 
//      reserved1       (4),      
//      reserved2       (5),      
//      reserved3       (6),      
//      unavailable     (7), -- not-equipped or unavailable value,
//                           -- speed relative to the vehicle alignment
//
//      ... -- # LOCAL_CONTENT
//      }
   private void setTransmissionAndSpeed(TransmissionAndSpeed tm) {
      if (tm != null && tm.getSize() >= 2) {
         char i = ByteBuffer.wrap(tm.byteArrayValue()).order(ByteOrder.BIG_ENDIAN).getChar();
         int t = i >> 13;
         if (t != TransmissionState.unavailable.ordinal())
            setTransmission(TransmissionState.values()[t]);
         int s = i & 0x1FFF; 
         if (s != 8191)
            // speed is received in units of 0.02 m/s
            setSpeed(BigDecimal.valueOf(s * 2, 2));
      }
   }

   public String getDateTime() {
      return dateTime;
   }

   public void setDateTime(String dateTime) {
      this.dateTime = dateTime;

      if (year == null || month == null || day == null || 
          hour == null || minute == null || second == null) {
         try {
            OdeDateTime odt = new OdeDateTime(dateTime);
            setYear(odt.getYear());
            setMonth(odt.getMonth());
            setDay(odt.getDay());
            setHour(odt.getHour());
            setMinute(odt.getMinute());
            setSecond(odt.getSecond());
         } catch (ParseException e) {
            e.printStackTrace();
         }
      }
   }

   public String getGroupId() {
      return groupId;
   }

   public void setGroupId(String groupId) {
      this.groupId = groupId;
   }

   public Integer getEvCap() {
      return evCap;
   }

   public void setEvCap(Integer evCap) {
      this.evCap = evCap;
   }

   public Integer getEvRange() {
      return evRange;
   }

   public void setEvRange(Integer evRange) {
      this.evRange = evRange;
   }

   public Integer getEvSOC() {
      return evSOC;
   }

   public void setEvSOC(Integer evSOC) {
      this.evSOC = evSOC;
   }

   public Integer getEnvEmiss() {
      return EnvEmiss;
   }

   public void setEnvEmiss(Integer envEmiss) {
      EnvEmiss = envEmiss;
   }

   public Integer getEnvFuelCond() {
      return EnvFuelCond;
   }

   public void setEnvFuelCond(Integer envFuelCond) {
      EnvFuelCond = envFuelCond;
   }

   public Integer getEnvFuelEcon() {
      return EnvFuelEcon;
   }

   public void setEnvFuelEcon(Integer envFuelEcon) {
      EnvFuelEcon = envFuelEcon;
   }

   public BigDecimal getAccelLat() {
      return accelLat;
   }

   public void setAccelLat(BigDecimal accelLat) {
      this.accelLat = accelLat;
   }

   public BigDecimal getAccelLong() {
      return accelLong;
   }

   public void setAccelLong(BigDecimal accelLong) {
      this.accelLong = accelLong;
   }

   public BigDecimal getAccelVert() {
      return accelVert;
   }

   public void setAccelVert(BigDecimal accelVert) {
      this.accelVert = accelVert;
   }

   public BigDecimal getAccelYaw() {
      return accelYaw;
   }

   public void setAccelYaw(BigDecimal accellYaw) {
      this.accelYaw = accellYaw;
   }

   public Byte getBrakesApplied() {
      return brakesApplied;
   }

   public void setBrakesApplied(Byte brakesApplied) {
      this.brakesApplied = brakesApplied;
   }

   public Byte getBrakesTraction() {
      return brakesTraction;
   }

   public void setBrakesTraction(Byte brakesTraction) {
      this.brakesTraction = brakesTraction;
   }

   public Byte getBrakesABS() {
      return brakesABS;
   }

   public void setBrakesABS(Byte brakesABS) {
      this.brakesABS = brakesABS;
   }

   public Byte getBrakesSCS() {
      return brakesSCS;
   }

   public void setBrakesSCS(Byte brakesSCS) {
      this.brakesSCS = brakesSCS;
   }

   public Byte getBrakesBBA() {
      return brakesBBA;
   }

   public void setBrakesBBA(Byte brakesBBA) {
      this.brakesBBA = brakesBBA;
   }

   public Byte getBrakesAux() {
      return BrakesAux;
   }

   public void setBrakesAux(Byte brakesAux) {
      BrakesAux = brakesAux;
   }

   public BigDecimal getHeading() { 
      return heading;
   }

   public void setHeading(BigDecimal heading) {
      this.heading = heading;
   }

   public TransmissionState getTransmission() {
      return transmission;
   }

   public void setTransmission(TransmissionState transmission) {
      this.transmission = transmission;
   }

   public BigDecimal getSpeed() {
      return speed;
   }

   public void setSpeed(BigDecimal speed) {
      this.speed = speed;
   }

   public BigDecimal getSteeringAngle() {
      return steeringAngle;
   }

   public void setSteeringAngle(BigDecimal steeringAngle) {
      this.steeringAngle = steeringAngle;
   }

   public Integer getSizeLength() {
      return sizeLength;
   }

   public void setSizeLength(Integer sizeLength) {
      this.sizeLength = sizeLength;
   }

   public Integer getSizeWidth() {
      return sizeWidth;
   }

   public void setSizeWidth(Integer sizeWidth) {
      this.sizeWidth = sizeWidth;
   }

   public Byte getEventFlag() {
      return eventFlag;
   }

   public void setEventFlag(Byte eventFlag) {
      this.eventFlag = eventFlag;
   }

   public BigDecimal getLatitude() {
      return latitude;
   }

   public void setLatitude(BigDecimal latitude) {
      this.latitude = latitude;
   }

   public BigDecimal getLongitude() {
      return longitude;
   }

   public void setLongitude(BigDecimal longitude) {
      this.longitude = longitude;
   }

   public BigDecimal getElevation() {
      return elevation;
   }

   public void setElevation(BigDecimal elevation) {
      this.elevation = elevation;
   }

   public String getTempId() {
      return tempId;
   }

   public void setTempId(String tempId) {
      this.tempId = tempId;
   }

   public Integer getYear() {
      return year;
   }

   public void setYear(Integer year) {
      this.year = year;
   }

   public Integer getMonth() {
      return month;
   }

   public void setMonth(Integer month) {
      this.month = month;
   }

   public Integer getDay() {
      return day;
   }

   public void setDay(Integer day) {
      this.day = day;
   }

   public Integer getHour() {
      return hour;
   }

   public void setHour(Integer hour) {
      this.hour = hour;
   }

   public Integer getMinute() {
      return minute;
   }

   public void setMinute(Integer minute) {
      this.minute = minute;
   }

   public BigDecimal getSecond() {
      return second;
   }

   public void setSecond(BigDecimal second) {
      this.second = second;
   }

   public Integer getLights() {
      return lights;
   }

   public void setLights(Integer integer) {
      this.lights = integer;
   }

   public BigDecimal getThrottlePos() {
      return throttlePos;
   }

   public void setThrottlePos(BigDecimal throttlePos) {
      this.throttlePos = throttlePos;
   }

   public Integer getTirePressureLF() {
      return tirePressureLF;
   }

   public void setTirePressureLF(Integer tirePressureLF) {
      this.tirePressureLF = tirePressureLF;
   }

   public Integer getTirePressureLR() {
      return tirePressureLR;
   }

   public void setTirePressureLR(Integer tirePressureLR) {
      this.tirePressureLR = tirePressureLR;
   }

   public Integer getTirePressureRF() {
      return tirePressureRF;
   }

   public void setTirePressureRF(Integer tirePressureRF) {
      this.tirePressureRF = tirePressureRF;
   }

   public Integer getTirePressureRR() {
      return tirePressureRR;
   }

   public void setTirePressureRR(Integer tirePressureRR) {
      this.tirePressureRR = tirePressureRR;
   }

   public Integer getWeatherAirPres() {
      return weatherAirPres;
   }

   public void setWeatherAirPres(Integer weatherAirPres) {
      this.weatherAirPres = weatherAirPres;
   }

   public Integer getWeatherAirTemp() {
      return weatherAirTemp;
   }

   public void setWeatherAirTemp(Integer weatherAirTemp) {
      this.weatherAirTemp = weatherAirTemp;
   }

   public Long getWeatherIsRaining() {
      return weatherIsRaining;
   }

   public void setWeatherIsRaining(Long long1) {
      this.weatherIsRaining = long1;
   }

   public Integer getWeatherRainRate() {
      return weatherRainRate;
   }

   public void setWeatherRainRate(Integer weatherRainRate) {
      this.weatherRainRate = weatherRainRate;
   }

   public Long getWeatherPrecipSituation() {
      return weatherPrecipSituation;
   }

   public void setWeatherPrecipSituation(Long weatherPrecipSituation) {
      this.weatherPrecipSituation = weatherPrecipSituation;
   }

   public Integer getWeatherSolarRadiation() {
      return weatherSolarRadiation;
   }

   public void setWeatherSolarRadiation(Integer weatherSolarRadiation) {
      this.weatherSolarRadiation = weatherSolarRadiation;
   }

   public Integer getWeatherFriction() {
      return weatherFriction;
   }

   public void setWeatherFriction(Integer weatherFriction) {
      this.weatherFriction = weatherFriction;
   }

   public Long getWipersStatusFrnt() {
      return wipersStatusFrnt;
   }

   public void setWipersStatusFrnt(Long long1) {
      this.wipersStatusFrnt = long1;
   }

   public Integer getWipersRateFrnt() {
      return wipersRateFrnt;
   }

   public void setWipersRateFrnt(Integer wipersRateFrnt) {
      this.wipersRateFrnt = wipersRateFrnt;
   }

   public Long getWipersStatusRear() {
      return wipersStatusRear;
   }

   public void setWipersStatusRear(Long wipersStatusRear) {
      this.wipersStatusRear = wipersStatusRear;
   }

   public Integer getWipersRateRear() {
      return wipersRateRear;
   }

   public void setWipersRateRear(Integer wipersRateRear) {
      this.wipersRateRear = wipersRateRear;
   }

   public String getRoadSeg() {
      return roadSeg;
   }

   public void setRoadSeg(String roadSeg) {
      this.roadSeg = roadSeg;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result
            + ((BrakesAux == null) ? 0 : BrakesAux.hashCode());
      result = prime * result + ((EnvEmiss == null) ? 0 : EnvEmiss.hashCode());
      result = prime * result
            + ((EnvFuelCond == null) ? 0 : EnvFuelCond.hashCode());
      result = prime * result
            + ((EnvFuelEcon == null) ? 0 : EnvFuelEcon.hashCode());
      result = prime * result + ((accelLat == null) ? 0 : accelLat.hashCode());
      result = prime * result
            + ((accelLong == null) ? 0 : accelLong.hashCode());
      result = prime * result
            + ((accelVert == null) ? 0 : accelVert.hashCode());
      result = prime * result
            + ((accelYaw == null) ? 0 : accelYaw.hashCode());
      result = prime * result
            + ((brakesABS == null) ? 0 : brakesABS.hashCode());
      result = prime * result
            + ((brakesApplied == null) ? 0 : brakesApplied.hashCode());
      result = prime * result
            + ((brakesBBA == null) ? 0 : brakesBBA.hashCode());
      result = prime * result
            + ((brakesSCS == null) ? 0 : brakesSCS.hashCode());
      result = prime * result
            + ((brakesTraction == null) ? 0 : brakesTraction.hashCode());
      result = prime * result + ((dateTime == null) ? 0 : dateTime.hashCode());
      result = prime * result + ((day == null) ? 0 : day.hashCode());
      result = prime * result
            + ((elevation == null) ? 0 : elevation.hashCode());
      result = prime * result + ((evCap == null) ? 0 : evCap.hashCode());
      result = prime * result + ((evRange == null) ? 0 : evRange.hashCode());
      result = prime * result + ((evSOC == null) ? 0 : evSOC.hashCode());
      result = prime * result
            + ((eventFlag == null) ? 0 : eventFlag.hashCode());
      result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
      result = prime * result + ((heading == null) ? 0 : heading.hashCode());
      result = prime * result + ((hour == null) ? 0 : hour.hashCode());
      result = prime * result + ((latitude == null) ? 0 : latitude.hashCode());
      result = prime * result + ((lights == null) ? 0 : lights.hashCode());
      result = prime * result
            + ((longitude == null) ? 0 : longitude.hashCode());
      result = prime * result + ((minute == null) ? 0 : minute.hashCode());
      result = prime * result + ((month == null) ? 0 : month.hashCode());
      result = prime * result + ((roadSeg == null) ? 0 : roadSeg.hashCode());
      result = prime * result + ((second == null) ? 0 : second.hashCode());
      result = prime * result
            + ((sizeLength == null) ? 0 : sizeLength.hashCode());
      result = prime * result
            + ((sizeWidth == null) ? 0 : sizeWidth.hashCode());
      result = prime * result + ((speed == null) ? 0 : speed.hashCode());
      result = prime * result
            + ((steeringAngle == null) ? 0 : steeringAngle.hashCode());
      result = prime * result + ((tempId == null) ? 0 : tempId.hashCode());
      result = prime * result
            + ((throttlePos == null) ? 0 : throttlePos.hashCode());
      result = prime * result
            + ((tirePressureLF == null) ? 0 : tirePressureLF.hashCode());
      result = prime * result
            + ((tirePressureLR == null) ? 0 : tirePressureLR.hashCode());
      result = prime * result
            + ((tirePressureRF == null) ? 0 : tirePressureRF.hashCode());
      result = prime * result
            + ((tirePressureRR == null) ? 0 : tirePressureRR.hashCode());
      result = prime * result
            + ((transmission == null) ? 0 : transmission.hashCode());
      result = prime * result
            + ((weatherAirPres == null) ? 0 : weatherAirPres.hashCode());
      result = prime * result
            + ((weatherAirTemp == null) ? 0 : weatherAirTemp.hashCode());
      result = prime * result
            + ((weatherFriction == null) ? 0 : weatherFriction.hashCode());
      result = prime * result
            + ((weatherIsRaining == null) ? 0 : weatherIsRaining.hashCode());
      result = prime
            * result
            + ((weatherPrecipSituation == null) ? 0 : weatherPrecipSituation
                  .hashCode());
      result = prime * result
            + ((weatherRainRate == null) ? 0 : weatherRainRate.hashCode());
      result = prime
            * result
            + ((weatherSolarRadiation == null) ? 0 : weatherSolarRadiation
                  .hashCode());
      result = prime * result
            + ((wipersRateFrnt == null) ? 0 : wipersRateFrnt.hashCode());
      result = prime * result
            + ((wipersRateRear == null) ? 0 : wipersRateRear.hashCode());
      result = prime * result
            + ((wipersStatusFrnt == null) ? 0 : wipersStatusFrnt.hashCode());
      result = prime * result
            + ((wipersStatusRear == null) ? 0 : wipersStatusRear.hashCode());
      result = prime * result + ((year == null) ? 0 : year.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      OdeVehicleDataFlat other = (OdeVehicleDataFlat) obj;
      if (BrakesAux == null) {
         if (other.BrakesAux != null)
            return false;
      } else if (!BrakesAux.equals(other.BrakesAux))
         return false;
      if (EnvEmiss == null) {
         if (other.EnvEmiss != null)
            return false;
      } else if (!EnvEmiss.equals(other.EnvEmiss))
         return false;
      if (EnvFuelCond == null) {
         if (other.EnvFuelCond != null)
            return false;
      } else if (!EnvFuelCond.equals(other.EnvFuelCond))
         return false;
      if (EnvFuelEcon == null) {
         if (other.EnvFuelEcon != null)
            return false;
      } else if (!EnvFuelEcon.equals(other.EnvFuelEcon))
         return false;
      if (accelLat == null) {
         if (other.accelLat != null)
            return false;
      } else if (!accelLat.equals(other.accelLat))
         return false;
      if (accelLong == null) {
         if (other.accelLong != null)
            return false;
      } else if (!accelLong.equals(other.accelLong))
         return false;
      if (accelVert == null) {
         if (other.accelVert != null)
            return false;
      } else if (!accelVert.equals(other.accelVert))
         return false;
      if (accelYaw == null) {
         if (other.accelYaw != null)
            return false;
      } else if (!accelYaw.equals(other.accelYaw))
         return false;
      if (brakesABS == null) {
         if (other.brakesABS != null)
            return false;
      } else if (!brakesABS.equals(other.brakesABS))
         return false;
      if (brakesApplied == null) {
         if (other.brakesApplied != null)
            return false;
      } else if (!brakesApplied.equals(other.brakesApplied))
         return false;
      if (brakesBBA == null) {
         if (other.brakesBBA != null)
            return false;
      } else if (!brakesBBA.equals(other.brakesBBA))
         return false;
      if (brakesSCS == null) {
         if (other.brakesSCS != null)
            return false;
      } else if (!brakesSCS.equals(other.brakesSCS))
         return false;
      if (brakesTraction == null) {
         if (other.brakesTraction != null)
            return false;
      } else if (!brakesTraction.equals(other.brakesTraction))
         return false;
      if (dateTime == null) {
         if (other.dateTime != null)
            return false;
      } else if (!dateTime.equals(other.dateTime))
         return false;
      if (day == null) {
         if (other.day != null)
            return false;
      } else if (!day.equals(other.day))
         return false;
      if (elevation == null) {
         if (other.elevation != null)
            return false;
      } else if (!elevation.equals(other.elevation))
         return false;
      if (evCap == null) {
         if (other.evCap != null)
            return false;
      } else if (!evCap.equals(other.evCap))
         return false;
      if (evRange == null) {
         if (other.evRange != null)
            return false;
      } else if (!evRange.equals(other.evRange))
         return false;
      if (evSOC == null) {
         if (other.evSOC != null)
            return false;
      } else if (!evSOC.equals(other.evSOC))
         return false;
      if (eventFlag == null) {
         if (other.eventFlag != null)
            return false;
      } else if (!eventFlag.equals(other.eventFlag))
         return false;
      if (groupId == null) {
         if (other.groupId != null)
            return false;
      } else if (!groupId.equals(other.groupId))
         return false;
      if (heading == null) {
         if (other.heading != null)
            return false;
      } else if (!heading.equals(other.heading))
         return false;
      if (hour == null) {
         if (other.hour != null)
            return false;
      } else if (!hour.equals(other.hour))
         return false;
      if (latitude == null) {
         if (other.latitude != null)
            return false;
      } else if (!latitude.equals(other.latitude))
         return false;
      if (lights == null) {
         if (other.lights != null)
            return false;
      } else if (!lights.equals(other.lights))
         return false;
      if (longitude == null) {
         if (other.longitude != null)
            return false;
      } else if (!longitude.equals(other.longitude))
         return false;
      if (minute == null) {
         if (other.minute != null)
            return false;
      } else if (!minute.equals(other.minute))
         return false;
      if (month == null) {
         if (other.month != null)
            return false;
      } else if (!month.equals(other.month))
         return false;
      if (roadSeg == null) {
         if (other.roadSeg != null)
            return false;
      } else if (!roadSeg.equals(other.roadSeg))
         return false;
      if (second == null) {
         if (other.second != null)
            return false;
      } else if (!second.equals(other.second))
         return false;
      if (sizeLength == null) {
         if (other.sizeLength != null)
            return false;
      } else if (!sizeLength.equals(other.sizeLength))
         return false;
      if (sizeWidth == null) {
         if (other.sizeWidth != null)
            return false;
      } else if (!sizeWidth.equals(other.sizeWidth))
         return false;
      if (speed == null) {
         if (other.speed != null)
            return false;
      } else if (!speed.equals(other.speed))
         return false;
      if (steeringAngle == null) {
         if (other.steeringAngle != null)
            return false;
      } else if (!steeringAngle.equals(other.steeringAngle))
         return false;
      if (tempId == null) {
         if (other.tempId != null)
            return false;
      } else if (!tempId.equals(other.tempId))
         return false;
      if (throttlePos == null) {
         if (other.throttlePos != null)
            return false;
      } else if (!throttlePos.equals(other.throttlePos))
         return false;
      if (tirePressureLF == null) {
         if (other.tirePressureLF != null)
            return false;
      } else if (!tirePressureLF.equals(other.tirePressureLF))
         return false;
      if (tirePressureLR == null) {
         if (other.tirePressureLR != null)
            return false;
      } else if (!tirePressureLR.equals(other.tirePressureLR))
         return false;
      if (tirePressureRF == null) {
         if (other.tirePressureRF != null)
            return false;
      } else if (!tirePressureRF.equals(other.tirePressureRF))
         return false;
      if (tirePressureRR == null) {
         if (other.tirePressureRR != null)
            return false;
      } else if (!tirePressureRR.equals(other.tirePressureRR))
         return false;
      if (transmission != other.transmission)
         return false;
      if (weatherAirPres == null) {
         if (other.weatherAirPres != null)
            return false;
      } else if (!weatherAirPres.equals(other.weatherAirPres))
         return false;
      if (weatherAirTemp == null) {
         if (other.weatherAirTemp != null)
            return false;
      } else if (!weatherAirTemp.equals(other.weatherAirTemp))
         return false;
      if (weatherFriction == null) {
         if (other.weatherFriction != null)
            return false;
      } else if (!weatherFriction.equals(other.weatherFriction))
         return false;
      if (weatherIsRaining == null) {
         if (other.weatherIsRaining != null)
            return false;
      } else if (!weatherIsRaining.equals(other.weatherIsRaining))
         return false;
      if (weatherPrecipSituation == null) {
         if (other.weatherPrecipSituation != null)
            return false;
      } else if (!weatherPrecipSituation.equals(other.weatherPrecipSituation))
         return false;
      if (weatherRainRate == null) {
         if (other.weatherRainRate != null)
            return false;
      } else if (!weatherRainRate.equals(other.weatherRainRate))
         return false;
      if (weatherSolarRadiation == null) {
         if (other.weatherSolarRadiation != null)
            return false;
      } else if (!weatherSolarRadiation.equals(other.weatherSolarRadiation))
         return false;
      if (wipersRateFrnt == null) {
         if (other.wipersRateFrnt != null)
            return false;
      } else if (!wipersRateFrnt.equals(other.wipersRateFrnt))
         return false;
      if (wipersRateRear == null) {
         if (other.wipersRateRear != null)
            return false;
      } else if (!wipersRateRear.equals(other.wipersRateRear))
         return false;
      if (wipersStatusFrnt == null) {
         if (other.wipersStatusFrnt != null)
            return false;
      } else if (!wipersStatusFrnt.equals(other.wipersStatusFrnt))
         return false;
      if (wipersStatusRear == null) {
         if (other.wipersStatusRear != null)
            return false;
      } else if (!wipersStatusRear.equals(other.wipersStatusRear))
         return false;
      if (year == null) {
         if (other.year != null)
            return false;
      } else if (!year.equals(other.year))
         return false;
      return true;
   }

   public void setRoadSegment(List<OdeRoadSegment> roadSegments, double tolerance) {
      double minDist = Double.POSITIVE_INFINITY;
      OdeRoadSegment minSeg = null;
      for (OdeRoadSegment seg : roadSegments) {
         Line2D l = seg.toLine2D(seg);
         if (latitude != null && longitude != null) {
            Point2D p = GeoUtils.latLngToMap(latitude.doubleValue(), longitude.doubleValue());
            if (GeoUtils.isPointWithinBounds(p, l, tolerance)) {
               double dist = GeoUtils.distanceToLine(l, p);
               if (dist <= tolerance) {
                  if (dist < minDist) {
                     minDist = dist;
                     minSeg = seg;
                  }
               }
            }
         }
      }
      
      if (minSeg != null)
         setRoadSeg(minSeg.getId());
   }

   @Override
   public ZonedDateTime getTimestamp() {
      ZonedDateTime zdt = null;
      try {
         if (year != null &&
             month != null &&
             day != null &&
             hour != null &&
             minute != null && 
             second != null) {
            int sec = second.intValue();
            int milli = second.remainder(BigDecimal.ONE).multiply(BigDecimal.valueOf(1000)).intValue();
            
            zdt = DateTimeUtils.isoDateTime(year, month, day, hour, minute, sec, milli);
         } else if (dateTime != null) {
            zdt = DateTimeUtils.isoDateTime(dateTime);
         }
      } catch (ParseException e) {
         e.printStackTrace();
      }
      return zdt;
   }

   @Override
   public boolean isOnTime(ZonedDateTime start, ZonedDateTime end) {
      return DateTimeUtils.isBetweenTimesInclusive(getTimestamp(),
            start, end);
   }

   @Override
   public OdePosition3D getPosition() {
      return new OdePosition3D(latitude, longitude, elevation);
   }

   @Override
   public boolean isWithinBounds(OdeGeoRegion region) {
      return GeoUtils.isPositionWithinRegion(getPosition(), region);
   }




}
