package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;
import java.util.Map;

import us.dot.its.jpo.ode.j2735.dsrc.Acceleration;
import us.dot.its.jpo.ode.j2735.dsrc.AccelerationSet4Way;
import us.dot.its.jpo.ode.j2735.dsrc.AntiLockBrakeStatus;
import us.dot.its.jpo.ode.j2735.dsrc.AuxiliaryBrakeStatus;
import us.dot.its.jpo.ode.j2735.dsrc.BrakeAppliedStatus;
import us.dot.its.jpo.ode.j2735.dsrc.BrakeBoostApplied;
import us.dot.its.jpo.ode.j2735.dsrc.BrakeSystemStatus;
import us.dot.its.jpo.ode.j2735.dsrc.DDateTime;
import us.dot.its.jpo.ode.j2735.dsrc.DDay;
import us.dot.its.jpo.ode.j2735.dsrc.DHour;
import us.dot.its.jpo.ode.j2735.dsrc.DMinute;
import us.dot.its.jpo.ode.j2735.dsrc.DMonth;
import us.dot.its.jpo.ode.j2735.dsrc.DOffset;
import us.dot.its.jpo.ode.j2735.dsrc.DSecond;
import us.dot.its.jpo.ode.j2735.dsrc.DYear;
import us.dot.its.jpo.ode.j2735.dsrc.Elevation;
import us.dot.its.jpo.ode.j2735.dsrc.Heading;
import us.dot.its.jpo.ode.j2735.dsrc.Latitude;
import us.dot.its.jpo.ode.j2735.dsrc.Longitude;
import us.dot.its.jpo.ode.j2735.dsrc.Position3D;
import us.dot.its.jpo.ode.j2735.dsrc.StabilityControlStatus;
import us.dot.its.jpo.ode.j2735.dsrc.SteeringWheelAngle;
import us.dot.its.jpo.ode.j2735.dsrc.TemporaryID;
import us.dot.its.jpo.ode.j2735.dsrc.TractionControlStatus;
import us.dot.its.jpo.ode.j2735.dsrc.TransmissionState;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleLength;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleSize;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleWidth;
import us.dot.its.jpo.ode.j2735.dsrc.Velocity;
import us.dot.its.jpo.ode.j2735.dsrc.VerticalAcceleration;
import us.dot.its.jpo.ode.j2735.dsrc.YawRate;
import us.dot.its.jpo.ode.j2735.semi.FundamentalSituationalStatus;
import us.dot.its.jpo.ode.j2735.semi.VehSitRecord;
import us.dot.its.jpo.ode.j2735.semi.VsmEventFlag;
import us.dot.its.jpo.ode.plugin.j2735.J2735AccelerationSet4Way;
import us.dot.its.jpo.ode.plugin.j2735.J2735BitString;
import us.dot.its.jpo.ode.plugin.j2735.J2735BrakeSystemStatus;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735BsmCoreData;
import us.dot.its.jpo.ode.plugin.j2735.J2735Position3D;
import us.dot.its.jpo.ode.plugin.j2735.J2735TransmissionState;
import us.dot.its.jpo.ode.plugin.j2735.J2735VehicleSize;
import us.dot.its.jpo.ode.util.CodecUtils;

public class OssVehicleSituationRecord {

   private OssVehicleSituationRecord() {
   }

   /**
    * Converts a generic J2735Bsm to ASN1-compliant VehSitRecord
    * 
    * @param bsm
    * @return
    */
   public static VehSitRecord convertBsmToVsr(J2735Bsm bsm) {

      VehSitRecord vsr = new VehSitRecord();
      vsr.tempID = new TemporaryID(CodecUtils.fromHex(bsm.getCoreData().getId()));

      // BSM does not contain full time, put in "unavailable" values
      vsr.time = new DDateTime(new DYear(0), new DMonth(0), new DDay(0), new DHour(31), new DMinute(60),
            new DSecond(65535), new DOffset(0));

      vsr.pos = convertPosition3D(bsm.getCoreData().getPosition());
      vsr.fundamental = createFundamentalSituationalStatus(bsm.getCoreData());

      // vsr.vehstat = createVehStat();
      // vsr.weather = createWeather();
      // vsr.env = createEnvironmental();
      // vsr.elevh = createElectricVeh();

      return vsr;

   }

   /**
    * Converts a generic J2735Position3D to ASN1-compliant Position3D
    * 
    * @param jPos
    * @return
    */
   private static Position3D convertPosition3D(J2735Position3D jPos) {

      Position3D nPos = new Position3D();

      if (null == jPos.getLongitude()) {
         nPos._long = new Longitude(1800000001);
      } else {
         nPos._long = new Longitude((short) (jPos.getLongitude().longValue() * Math.pow(10, 7)));
      }

      if (null == jPos.getLatitude()) {
         nPos.lat = new Latitude(900000001);
      } else {
         nPos.lat = new Latitude((short) (jPos.getLatitude().longValue() * Math.pow(10, 7)));
      }

      if (null == jPos.getElevation()) {
         nPos.elevation = new Elevation(-4096);
      } else {
         nPos.elevation = new Elevation((short) (jPos.getElevation().longValue() * Math.pow(10, 1)));
      }

      return nPos;
   }

   /**
    * Takes a generic J2735BsmCoreData to ASN1-compliant
    * FundamentalSituationalStatus
    * 
    * @param bsmcd
    * @return
    */
   private static FundamentalSituationalStatus createFundamentalSituationalStatus(J2735BsmCoreData bsmcd) {

      FundamentalSituationalStatus fss = new FundamentalSituationalStatus();
      fss.speed.speed = convertSpeed(bsmcd.getSpeed());
      fss.speed.transmisson = convertTransmissionState(bsmcd.getTransmission());
      fss.heading = convertHeading(bsmcd.getHeading());
      fss.steeringAngle = convertSteeringWheelAngle(bsmcd.getAngle());
      fss.accelSet = convertAccelerationSet4Way(bsmcd.getAccelSet());
      fss.brakes = convertBrakeSystemStatus(bsmcd.getBrakes());
      fss.vehSize = convertVehicleSize(bsmcd.getSize());
      fss.vsmEventFlag = new VsmEventFlag(new byte[0]);
      return fss;
   }

   /**
    * Takes a generic BigDecimal speed and converts to ASN1-compliant integer
    * Velocity
    * 
    * @param speed
    * @return
    */
   private static Velocity convertSpeed(BigDecimal speed) {
      Velocity vel;
      if (null == speed) {
         vel = new Velocity(8191);
      } else {
         vel = new Velocity((int) Math.floor(speed.doubleValue() * Math.pow(10, 2)));
      }
      return vel;
   }

   /**
    * Convert from J2735TransmissionState enum to TransmissionState enum
    * 
    * @param tstate
    * @return
    */
   private static TransmissionState convertTransmissionState(J2735TransmissionState tstate) {

      TransmissionState rts;

      switch (tstate) {
      case NEUTRAL:
         rts = TransmissionState.neutral;
         break;
      case PARK:
         rts = TransmissionState.park;
         break;
      case FORWARDGEARS:
         rts = TransmissionState.forwardGears;
         break;
      case RESERVED1:
         rts = TransmissionState.reserved1;
         break;
      case RESERVED2:
         rts = TransmissionState.reserved2;
         break;
      case RESERVED3:
         rts = TransmissionState.reserved3;
         break;
      case REVERSEGEARS:
         rts = TransmissionState.reverseGears;
         break;
      case UNAVAILABLE:
         rts = TransmissionState.unavailable;
         break;
      default:
         throw new IllegalArgumentException("Invalid transmission state " + tstate);
      }

      return rts;
   }

   /**
    * Converts from generic BigDecimal heading to ASN1-compliant Heading
    * 
    * @param jHead
    * @return
    */
   private static Heading convertHeading(BigDecimal jHead) {
      Heading nHead;
      if (null == jHead) {
         nHead = new Heading(28800);
      } else {
         nHead = new Heading((int) Math.floor(jHead.doubleValue() / 0.0125));
      }
      return nHead;
   }

   /**
    * Converts from a generic BigDecimal angle to ASN1-compliant Angle
    * 
    * @param jAng
    * @return
    */
   private static SteeringWheelAngle convertSteeringWheelAngle(BigDecimal jAng) {
      SteeringWheelAngle nAng;
      if (null == jAng) {
         nAng = new SteeringWheelAngle(127);
      } else {
         nAng = new SteeringWheelAngle((int) Math.floor(jAng.doubleValue() / 1.5));
      }
      return nAng;
   }

   private static AccelerationSet4Way convertAccelerationSet4Way(J2735AccelerationSet4Way jAccSet) {

      AccelerationSet4Way nAccSet = new AccelerationSet4Way();
      nAccSet._long = convertAcceleration(jAccSet.getAccelLong());
      nAccSet.lat = convertAcceleration(jAccSet.getAccelLong());
      nAccSet.vert = convertVerticalAcceleration(jAccSet.getAccelLong());
      nAccSet.yaw = convertYawRate(jAccSet.getAccelYaw());
      return nAccSet;
   }

   /**
    * Convert a generic acceleration to ASN1-compliant Acceleration
    * 
    * @param jAcc
    * @return
    */
   private static Acceleration convertAcceleration(BigDecimal jAcc) {
      Acceleration nAcc;
      if (null == jAcc) {
         nAcc = new Acceleration(2001);
      } else {
         nAcc = new Acceleration((int) Math.floor(jAcc.doubleValue() / 100));
      }
      return nAcc;
   }

   /**
    * Convert a generic vertical acceleration to ASN1-compliant
    * VerticalAcceleration
    * 
    * @param jVerta
    * @return
    */
   private static VerticalAcceleration convertVerticalAcceleration(BigDecimal jVerta) {
      VerticalAcceleration nVerta;
      if (null == jVerta) {
         nVerta = new VerticalAcceleration(-127);
      } else {
         nVerta = new VerticalAcceleration((int) Math.floor(jVerta.doubleValue() / 0.02));
      }
      return nVerta;
   }

   /**
    * Convert a generic yaw rate to ASN1-compliant YawRate
    * 
    * @param jyr
    * @return
    */
   private static YawRate convertYawRate(BigDecimal jyr) {
      return new YawRate((int) Math.floor(jyr.doubleValue() * 100));
   }

   /**
    * Convert a generic set of brake status information into ASN1-compliant
    * BrakeSystemStatus
    * 
    * @param jBss
    * @return
    */
   private static BrakeSystemStatus convertBrakeSystemStatus(J2735BrakeSystemStatus jBss) {
      BrakeSystemStatus nBss = new BrakeSystemStatus();
      nBss.wheelBrakes = convertBrakeAppliedStatus(jBss.getWheelBrakes());
      nBss.traction = convertTractionControlStatus(jBss.getTraction());
      nBss.abs = convertAntiLockBrakeStatus(jBss.getAbs());
      nBss.scs = convertStabilityControlStatus(jBss.getScs());
      nBss.brakeBoost = convertBrakeBoostApplied(jBss.getBrakeBoost());
      nBss.auxBrakes = convertAuxiliaryBrakeStatus(jBss.getAuxBrakes());
      return nBss;
   }

   /**
    * Creates a BrakeAppliedStatus bit string from a generic hashmap
    * 
    * @param jBas
    * @return
    */
   private static BrakeAppliedStatus convertBrakeAppliedStatus(J2735BitString jBas) {

      byte nb = 0b00000000;
      int bitPow = 1;

      for (Map.Entry<String, Boolean> entry : jBas.entrySet()) {
         switch (entry.getKey()) {
         case "leftFront":
            bitPow = 2;
            break;
         case "leftRear":
            bitPow = 4;
            break;
         case "rightFront":
            bitPow = 8;
            break;
         case "rightRear":
            bitPow = 16;
            break;
         default:
            // aka "unavailable"
            bitPow = 1;
            break;
         }

         nb = (byte) (nb | (entry.getValue() ? bitPow : 0));
      }

      return new BrakeAppliedStatus(new byte[] { nb });
   }

   private static TractionControlStatus convertTractionControlStatus(String jTrac) {

      int tval = 0;

      switch (jTrac) {
      case "off":
         tval = 1;
         break;
      case "on":
         tval = 2;
         break;
      case "engaged":
         tval = 3;
         break;
      default:
         // aka "unavailable"
         tval = 0;
         break;
      }

      return new TractionControlStatus(tval);
   }

   private static AntiLockBrakeStatus convertAntiLockBrakeStatus(String jAbs) {
      int tval = 0;

      switch (jAbs) {
      case "off":
         tval = 1;
         break;
      case "on":
         tval = 2;
         break;
      case "engaged":
         tval = 3;
         break;
      default:
         // aka "unavailable"
         tval = 0;
         break;
      }

      return new AntiLockBrakeStatus(tval);
   }

   private static StabilityControlStatus convertStabilityControlStatus(String jScs) {
      int tval = 0;

      switch (jScs) {
      case "off":
         tval = 1;
         break;
      case "on":
         tval = 2;
         break;
      case "engaged":
         tval = 3;
         break;
      default:
         // aka "unavailable"
         tval = 0;
         break;
      }

      return new StabilityControlStatus(tval);
   }

   private static BrakeBoostApplied convertBrakeBoostApplied(String jBba) {
      int tval = 0;

      switch (jBba) {
      case "off":
         tval = 1;
         break;
      case "on":
         tval = 2;
         break;
      default:
         // aka "unavailable"
         tval = 0;
         break;
      }

      return new BrakeBoostApplied(tval);
   }

   private static AuxiliaryBrakeStatus convertAuxiliaryBrakeStatus(String jAbs) {

      int tval = 0;
      switch (jAbs) {
      case "off":
         tval = 1;
         break;
      case "on":
         tval = 2;
         break;
      case "reserved":
         tval = 3;
         break;
      default:
         // aka "unavailable"
         tval = 0;
         break;
      }

      return new AuxiliaryBrakeStatus(tval);
   }

   private static VehicleSize convertVehicleSize(J2735VehicleSize jvs) {
      VehicleSize nvs = new VehicleSize();
      nvs.width = new VehicleWidth(jvs.getWidth());
      nvs.length = new VehicleLength(jvs.getLength());
      return nvs;
   }
}
