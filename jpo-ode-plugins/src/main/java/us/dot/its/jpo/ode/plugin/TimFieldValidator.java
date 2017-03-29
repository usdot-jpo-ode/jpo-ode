package us.dot.its.jpo.ode.plugin;

import java.nio.ByteBuffer;
import java.text.ParseException;
import java.time.ZonedDateTime;

import us.dot.its.jpo.ode.j2735.dsrc.HeadingSlice;
import us.dot.its.jpo.ode.j2735.dsrc.MsgCRC;
import us.dot.its.jpo.ode.plugin.j2735.J2735Position3D;
import us.dot.its.jpo.ode.util.DateTimeUtils;

public class TimFieldValidator {
   private TimFieldValidator() {
      throw new UnsupportedOperationException();
   }
   
   public static void validateMessageCount(int msg) {
      if (msg > 127 || msg < 0)
         throw new IllegalArgumentException("Invalid message count [0-127]");
   }

   public static void validateURL(String url) {
      if (url.isEmpty())
         throw new IllegalArgumentException("Invalid empty url");
      if (url.length() > 45)
         throw new IllegalArgumentException("Invalid URL length [1-45]");
   }

   public static void validateURLShort(String url) {
      if (url.isEmpty())
         throw new IllegalArgumentException("Invalid empty Short url");
      if (url.length() > 15)
         throw new IllegalArgumentException("Invalid URL lenth [1-15]");
   }

   public static void validateFrameCount(int count) {
      if (count < 1 || count > 8)
         throw new IllegalArgumentException("Invalid number of dataFrames[1-8]");
   }

   public static void validateMessageID(String str) {
      validateString(str);
      if (!("RoadSignID").equals(str) && !("FurtherInfoID").equals(str))
         throw new IllegalArgumentException("Invalid messageID \"RoadSignID or FurtherInfoID\"");
   }
   
   public static void validateInfoType(int num) {
      if (num < 0)
         throw new IllegalArgumentException("Invalid enumeration [0<]");
   }

   public static void validateStartYear(int year) {
      if (year < 0 || year > 4095)
         throw new IllegalArgumentException("Not a valid start year [0-4095]");
   }

   public static void validateStartTime(long time) {
      if (time < 0 || time > 527040)
         throw new IllegalArgumentException("Invalid start Time [0-527040]");
   }
   
   public static void validateMinutesDuration(long dur) {
      if (dur < 0 || dur > 32000)
         throw new IllegalArgumentException("Invalid Duration [0-32000]");
   }

   public static void validateHeaderIndex(short count) {
      if (count < 0 || count > 31)
         throw new IllegalArgumentException("Invalid header sspIndex[0-31]");
   }

   public static void validatePosition(J2735Position3D position) {
      if (position.getLatitude().doubleValue() < -90.0 || position.getLatitude().doubleValue() > 90.0)
         throw new IllegalArgumentException("Invalid Latitude [-90 - 90]");
      if (position.getLongitude().doubleValue() < -180.0 || position.getLongitude().doubleValue() > 180.0)
         throw new IllegalArgumentException("Invalid Longitude [-180 - 180]");
      if (position.getElevation().doubleValue() < -409.5 || position.getElevation().doubleValue() > 6143.9)
         throw new IllegalArgumentException("Invalid Elevation [-409.5 - 6143.9]");
   }

   public static void validateLatitude(long lat) {
      if (lat < -90.0 || lat > 90)
         throw new IllegalArgumentException("Invalid Latitude[-90 - 90]");
   }

   public static void validateLongitude(long lonng) {
      if (lonng < -180.0 || lonng > 180.0)
         throw new IllegalArgumentException("Invalid Longitude[-180 - 180]");
   }

   public static void validateHeading(String head) {
      validateString(head);
      if (head.length() != 16) {
         throw new IllegalArgumentException("Invalid BitString, must be 16 bits!");
      }
   }

   public static void validateMUTCDCode(int mutc) {
      if (mutc < 0 || mutc > 6)
         throw new IllegalArgumentException("Invalid Enumeration [0-6]");
   }

   public static void validateSign(int sign) {
      if (sign < 0 || sign > 7)
         throw new IllegalArgumentException("Invalid Sign Priority [0-7]");
   }

   public static void validateITISCodes(String code) {
      int cd;
      try {
         cd = Integer.parseInt(code);
         if (cd < 0 || cd > 65535)
            throw new IllegalArgumentException("Invalid ITIS code [0-65535]");
      } catch (NumberFormatException e) {
         if (code.isEmpty())
            throw new IllegalArgumentException("Invalid empty string");
         if (code.length() > 500)
            throw new IllegalArgumentException("Invalid test Phrase length [1-500]");
      }
   }

   public static void validateContentCodes(String code) {
      int cd;
      try {
         cd = Integer.parseInt(code);
         if (cd < 0 || cd > 65535)
            throw new IllegalArgumentException("Invalid ITIS code [0-65535]");
      } catch (NumberFormatException e) {
         if (code.isEmpty())
            throw new IllegalArgumentException("Invalid empty string");
         if (code.length() > 16)
            throw new IllegalArgumentException("Invalid test Phrase length [1-16]");
      }
   }

   public static void validateString(String str) {
      if (str.isEmpty())
         throw new IllegalArgumentException("Invalid Empty String");
   }

   public static void validateGeoName(String name) {
      if (name.length() < 1 || name.length() > 63)
         throw new IllegalArgumentException("Invalid Descriptive name length [1-63]");
   }

   public static void validateRoadID(int id) {
      if (id < 0 || id > 65535)
         throw new IllegalArgumentException("Invalid RoadID [0-65535]");
   }

   public static void validateLaneWidth(int width) {
      if (width < 0 || width > 32767)
         throw new IllegalArgumentException("Invalid lane width [0-32767]");
   }

   public static void validateDirectionality(long dir) {
      if (dir < 0 || dir > 3)
         throw new IllegalArgumentException("Invalid enumeration [0-3]");
   }

   public static void validateZoom(int z) {
      if (z < 0 || z > 15)
         throw new IllegalArgumentException("Invalid zoom [0-15]");
   }

   public static void validateExtent(int ex) {
      if (ex < 0 || ex > 15)
         throw new IllegalArgumentException("Invalid extent enumeration [0-15]");
   }

   public static void validateRadius(int rad) {
      if (rad < 0 || rad > 4095)
         throw new IllegalArgumentException("Invalid radius [0-4095]");
   }

   public static void validateUnits(int unit) {
      if (unit < 0 || unit > 7)
         throw new IllegalArgumentException("Invalid units enumeration [0-7]");
   }

   public static void validatex16Offset(int x) {
      if (x < -32768 || x > 32767)
         throw new IllegalArgumentException("Invalid x offset [-32768 - 32767]");
   }

   public static void validatey16Offset(int y) {
      if (y < -32768 || y > 32767)
         throw new IllegalArgumentException("Invalid y offset [-32768 - 32767]");
   }

   public static void validatez16Offset(int z) {
      if (z < -32768 || z > 32767)
         throw new IllegalArgumentException("Invalid z offset [-32768 - 32767]");
   }

   public static void validateB10Offset(int b) {
      if (b < -512 || b > 511)
         throw new IllegalArgumentException("Invalid B10_Offset [-512 - 511]");
   }

   public static void validateB11Offset(int b) {
      if (b < -1024 || b > 1023)
         throw new IllegalArgumentException("Invalid B11_Offset [-1024 - 1023]");
   }

   public static void validateB12Offset(int b) {
      if (b < -2048 || b > 2047)
         throw new IllegalArgumentException("Invalid B12_Offset [-2048 - 2047]");
   }

   public static void validateB13Offset(int b) {
      if (b < -4096 || b > 4095)
         throw new IllegalArgumentException("Invalid B13_Offset [-4096 - 4095]");
   }

   public static void validateB14Offset(int b) {
      if (b < -8192 || b > 8191)
         throw new IllegalArgumentException("Invalid B14_Offset [-8192 - 8191]");
   }

   public static void validateB16Offset(int b) {
      if (b < -32768 || b > 32767)
         throw new IllegalArgumentException("Invalid B16_Offset [-32768 - 32767]");
   }

   public static void validateLL12Offset(long b) {
      if (b < -2048 || b > 2047)
         throw new IllegalArgumentException("Invalid B10_Offset [-2048 - 2047]");
   }

   public static void validateLL14Offset(long b) {
      if (b < -8192 || b > 8191)
         throw new IllegalArgumentException("Invalid B11_Offset [-8192 - 8191]");
   }

   public static void validateLL16Offset(long b) {
      if (b < -32768 || b > 32767)
         throw new IllegalArgumentException("Invalid B12_Offset [-32768 - 32767]");
   }

   public static void validateLL18Offset(long b) {
      if (b < -131072 || b > 131071)
         throw new IllegalArgumentException("Invalid B13_Offset [-131072 - 131071]");
   }

   public static void validateLL22Offset(long b) {
      if (b < -2097152 || b > 2097151)
         throw new IllegalArgumentException("Invalid B14_Offset [-2097152 - 2097151]");
   }

   public static void validateLL24Offset(long b) {
      if (b < -8388608 || b > 8388607)
         throw new IllegalArgumentException("Invalid B16_Offset [-8388608 - 8388608]");
   }

   public static void validateLaneID(int lane) {
      if (lane < 0 || lane > 255)
         throw new IllegalArgumentException("Invalid LaneID [0 - 255]");
   }

   public static void validateSmallDrivenLine(int line) {
      if (line < -2047 || line > 2047)
         throw new IllegalArgumentException("Invalid Small Offset [-2047 - 2047]");
   }

   public static void validateLargeDrivenLine(int line) {
      if (line < -32767 || line > 32767)
         throw new IllegalArgumentException("Invalid Large Offset [-32767 - 32767]");
   }

   public static void validateAngle(int ang) {
      if (ang < 0 || ang > 28800)
         throw new IllegalArgumentException("Invalid Angle [0 - 28800]");
   }

   public static void validateB12Scale(int b) {
      if (b < -2048 || b > 2047)
         throw new IllegalArgumentException("Invalid B12 Scale [-2048 - 2047]");
   }

   public static void validateNodeAttribute(String str) {
      String myString = "reserved stopLine roundedCapStyleA roundedCapStyleB mergePoint divergePoint downstreamStopLine donwstreamStartNode closedToTraffic safeIsland curbPresentAtStepOff hydrantPresent";
      CharSequence cs = str;
      if (myString.contains(cs)) {
         return;
      } else {
         throw new IllegalArgumentException("Invalid NodeAttribute Enumeration");
      }
   }

   public static void validateSegmentAttribute(String str) {
      String myString = "reserved doNotBlock whiteLine mergingLaneLeft mergingLaneRight curbOnLeft curbOnRight loadingzoneOnLeft loadingzoneOnRight turnOutPointOnLeft turnOutPointOnRight adjacentParkingOnLeft adjacentParkingOnRight sharedBikeLane bikeBoxInFront transitStopOnLeft transitStopOnRight transitStopInLane sharedWithTrackedVehicle safeIsland lowCurbsPresent rumbleStripPresent audibleSignalingPresent adaptiveTimingPresent rfSignalRequestPresent partialCurbIntrusion taperToLeft taperToRight taperToCenterLine parallelParking headInParking freeParking timeRestrictionsOnParking costToPark midBlockCurbPresent unEvenPavementPresent";
      CharSequence cs = str;
      if (myString.contains(cs)) {
         return;
      } else {
         throw new IllegalArgumentException("Invalid SegmentAttribute Enumeration");
      }
   }

   public static void validateSpeedLimitType(String str) {
      String myString = "unknown maxSpeedInSchoolZone maxSpeedInSchoolZoneWhenChildrenArePresent maxSpeedInConstructionZone vehicleMinSpeed vehicleMaxSpeed vehicleNightMaxSpeed truckMinSpeed truckMaxSpeed truckNightMaxSpeed vehiclesWithTrailerMinSpeed vehiclesWithTrailersMaxSpeed vehiclesWithTrailersNightMaxSpeed";
      CharSequence cs = str;
      if (myString.contains(cs)) {
         return;
      } else {
         throw new IllegalArgumentException("Invalid SpeedLimitAttribute Enumeration");
      }
   }

   public static void validateVelocity(int vel) {
      if (vel < 0 || vel > 8191)
         throw new IllegalArgumentException("Invalid Velocity [0 - 8191]");
   }

   public static void validateDeltaAngle(int d) {
      if (d < -150 || d > 150)
         throw new IllegalArgumentException("Invalid Delta Angle [-150 - 150]");
   }

   public static void validateCrownPoint(int c) {
      if (c < -128 || c > 127)
         throw new IllegalArgumentException("Invalid Crown Point [-128 - 127]");
   }

   public static void validateLaneAngle(int a) {
      if (a < -180 || a > 180)
         throw new IllegalArgumentException("Invalid LaneAngle [-180 -180]");
   }
   
   public static long getMinuteOfTheYear(String timestamp) throws ParseException {
      ZonedDateTime start = DateTimeUtils.isoDateTime(timestamp);
      long diff = DateTimeUtils.difference(DateTimeUtils.isoDateTime(start.getYear() + "-01-01T00:00:00+00:00"), start);
      long minutes = diff / 60000;
      validateStartTime(minutes);
      return minutes;
   }
   
   public static HeadingSlice getHeadingSlice(String heading) {
      if (heading == null || heading.length() == 0) {
         return new HeadingSlice(new byte[] { 0x00, 0x00 });
      } else {
         short result = 0;
         for (int i = 0; i < 16; i++) {
            if (heading.charAt(i) == '1') {
               result |= 1;
            }
            result <<= 1;
         }
         return new HeadingSlice(ByteBuffer.allocate(2).putShort(result).array());
      }
   }
   
   public static MsgCRC getMsgCrc(String sum) {
      if (sum == null || sum.length() == 0) {
         return new MsgCRC(new byte[] { 0X00, 0X00 });
      } else {
         short result = 0;
         for (int i = 0; i < 16; i++) {
            if (sum.charAt(i) == '1') {
               result |= 1;
            }
            result <<= 1;
         }
         return new MsgCRC(ByteBuffer.allocate(2).putShort(result).array());
      }
   }
}
