/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.ode.plugin.j2735;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.plugin.j2735.OdeTravelerInformationMessage.DataFrame.MsgId;
import us.dot.its.jpo.ode.plugin.j2735.OdeTravelerInformationMessage.DataFrame.RoadSignID;
import us.dot.its.jpo.ode.util.CodecUtils;

public class TimFieldValidator {
   private static final int FURTHER_INFOR_ID_LENGTH = 4;

   private TimFieldValidator() {
      throw new UnsupportedOperationException();
   }
   
   public static void validateMessageCount(int msg) {
      if (msg > 127 || msg < 0)
         throw new IllegalArgumentException("Invalid message count [0..127]: " + msg);
   }

   public static void validateURL(String url) {
      if (url.isEmpty())
         throw new IllegalArgumentException("Invalid empty url");
      if (url.length() > 45)
         throw new IllegalArgumentException("Invalid URL length [1..45]: " + url.length());
   }

   public static void validateURLShort(String url) {
      if (url.isEmpty())
         throw new IllegalArgumentException("Invalid empty Short url");
      if (url.length() > 15)
         throw new IllegalArgumentException("Invalid URL lenth [1..15]: " + url.length());
   }

   public static void validateFrameCount(int count) {
      if (count < 1 || count > 8)
         throw new IllegalArgumentException("Invalid number of dataFrames [1..8]: " + count);
   }

   public static void validateMessageID(MsgId msgId) {
      if (msgId == null || (msgId.getFurtherInfoID() == null && msgId.getRoadSignID() == null))
         throw new IllegalArgumentException("Invalid msgID either \"roadSignID or furtherInfoID\" must be valid");
      
      if (msgId.getFurtherInfoID() != null)
         validateFurtherInfoID(msgId.getFurtherInfoID());
      
      if (msgId.getRoadSignID() != null)
         validateRoadSignID(msgId.getRoadSignID());
   }
   
   private static void validateRoadSignID(RoadSignID roadSignID) {
      validateShortHexString(roadSignID.getCrc());
      validatePosition(roadSignID.getPosition());
      validateShortHexString(roadSignID.getViewAngle());
   }

   private static void validateFurtherInfoID(String furtherInfoID) {
      validateString(furtherInfoID);
      if (furtherInfoID.length() > FURTHER_INFOR_ID_LENGTH) {
         throw new IllegalArgumentException("furtherInfoID must be or less Hex characters: " + furtherInfoID);
      }
      
      validateShortHexString(furtherInfoID);
   }
   
   public static void validateShortHexString(String shortHexString) {
      CodecUtils.shortStringToByteArray(shortHexString);
   }

   public static void validateStartYear(int year) {
      if (year < 0 || year > 4095)
         throw new IllegalArgumentException("Not a valid start year [0..4095]: " + year);
   }

   public static void validateStartTime(long time) {
      if (time < 0 || time > 527040)
         throw new IllegalArgumentException("Invalid start Time [0..527040]");
   }
   
   public static void validateMinutesDuration(long dur) {
      if (dur < 0 || dur > 32000)
         throw new IllegalArgumentException("Invalid Duration [0..32000]");
   }

   public static void validateHeaderIndex(short count) {
      if (count < 0 || count > 31)
         throw new IllegalArgumentException("Invalid header sspIndex [0..31]: " + count);
   }

   public static void validatePosition(OdePosition3D position) {
      if (position.getLatitude().doubleValue() < -90.0 || position.getLatitude().doubleValue() > 90.0)
         throw new IllegalArgumentException("Invalid Latitude [-90..90]: " + position.getLatitude().doubleValue());
      if (position.getLongitude().doubleValue() < -180.0 || position.getLongitude().doubleValue() > 180.0)
         throw new IllegalArgumentException("Invalid Longitude [-180..180]: " + position.getLongitude().doubleValue());
      if (position.getElevation().doubleValue() < -409.5 || position.getElevation().doubleValue() > 6143.9)
         throw new IllegalArgumentException("Invalid Elevation [-409.5..6143.9]: " + position.getElevation().doubleValue());
   }

   public static void validateLatitude(BigDecimal lat) {
      if (lat.doubleValue() < -90.0 || lat.doubleValue() > 90.0)
         throw new IllegalArgumentException("Invalid Latitude[-90..90]: " + lat);
   }

   public static void validateLongitude(BigDecimal lonng) {
      if (lonng.doubleValue() < -180.0 || lonng.doubleValue() > 180.0)
         throw new IllegalArgumentException("Invalid Longitude[-180..180]: " + lonng);
   }

   public static void validateHeading(String head) {
      validateString(head);
      if (head.length() != 16) {
         throw new IllegalArgumentException("Invalid BitString length [16]: " + head.length());
      }
   }

   public static void validateSign(int sign) {
      if (sign < 0 || sign > 7)
         throw new IllegalArgumentException("Invalid Sign Priority [0..7]: " + sign);
   }

   public static void validateITISCodes(String code) {
      int cd;
      try {
         cd = Integer.parseInt(code);
         if (cd < 0 || cd > 65535)
            throw new IllegalArgumentException("Invalid ITIS code [0..65535]: " + cd);
      } catch (NumberFormatException e) {
         if (code.isEmpty())
            throw new IllegalArgumentException("Empty ITIS Code or Text");
         if (code.length() > 500)
            throw new IllegalArgumentException("Invalid text phrase length [1..500]: " + code.length());
      }
   }

   public static void validateString(String str) {
      if (str.isEmpty())
         throw new IllegalArgumentException("Invalid Empty String");
   }

   public static void validateGeoName(String name) {
      if (name.length() < 1 || name.length() > 63)
         throw new IllegalArgumentException("Invalid Descriptive name length [1..63]: " + name.length());
   }

   public static void validateRoadID(int id) {
      if (id < 0 || id > 65535)
         throw new IllegalArgumentException("Invalid RoadID [0..65535]: " + id);
   }

   public static void validateLaneWidth(BigDecimal width) {
      if (width.doubleValue() < 0 || width.doubleValue() > 327.67)
         throw new IllegalArgumentException("Invalid lane width [0..327.67]: " + width);
   }

   public static void validateDirectionality(long dir) {
      if (dir < 0 || dir > 3)
         throw new IllegalArgumentException("Invalid enumeration [0..3]: " + dir);
   }

   public static void validateZoom(int z) {
      if (z < 0 || z > 15)
         throw new IllegalArgumentException("Invalid zoom [0..15]: " + z);
   }

   public static void validateExtent(int ex) {
      if (ex < 0 || ex > 15)
         throw new IllegalArgumentException("Invalid extent enumeration [0..15]: " + ex);
   }

   public static void validateRadius(int rad) {
      if (rad < 0 || rad > 4095)
         throw new IllegalArgumentException("Invalid radius [0..4095]: " + rad);
   }

   public static void validateUnits(int unit) {
      if (unit < 0 || unit > 7)
         throw new IllegalArgumentException("Invalid units enumeration [0..7]: " + unit);
   }

   public static void validatex16Offset(BigDecimal x) {
      if (x.doubleValue() < -0.0032767 || x.doubleValue() > 0.0032767)
         throw new IllegalArgumentException("Invalid x offset [-0.0032767..0.0032767]: " + x);
   }

   public static void validatey16Offset(BigDecimal y) {
      if (y.doubleValue() < -0.0032767 || y.doubleValue() > 0.0032767)
         throw new IllegalArgumentException("Invalid y offset [-0.0032767..0.0032767]: " + y);
   }

   public static void validatez16Offset(BigDecimal z) {
      if (z.doubleValue() < -0.0032767 || z.doubleValue() > 0.0032767)
         throw new IllegalArgumentException("Invalid z offset [-0.0032767..0.0032767]: " + z);
   }

   public static void validateB10Offset(BigDecimal b) {
      if (b.doubleValue() < -5.12 || b.doubleValue() > 5.11)
         throw new IllegalArgumentException("Invalid B10_Offset [-5.12..5.11]: " + b);
   }

   public static void validateB11Offset(BigDecimal b) {
      if (b.doubleValue() < -10.24 || b.doubleValue() > 10.23)
         throw new IllegalArgumentException("Invalid B11_Offset [-10.24..10.23]: " + b);
   }

   public static void validateB12Offset(BigDecimal b) {
      if (b.doubleValue() < -20.48 || b.doubleValue() > 20.47)
         throw new IllegalArgumentException("Invalid B12_Offset [-20.48..20.47]: " + b);
   }

   public static void validateB13Offset(BigDecimal b) {
      if (b.doubleValue() < -40.96 || b.doubleValue() > 40.95)
         throw new IllegalArgumentException("Invalid B13_Offset [-40.96..40.95]: " + b);
   }

   public static void validateB14Offset(BigDecimal b) {
      if (b.doubleValue() < -81.92 || b.doubleValue() > 81.91)
         throw new IllegalArgumentException("Invalid B14_Offset [-81.92..81.91]: " + b);
   }

   public static void validateB16Offset(BigDecimal b) {
      if (b.doubleValue() < -327.68 || b.doubleValue() > 327.67)
         throw new IllegalArgumentException("Invalid B16_Offset [-327.68..327.67]: " + b);
   }

   public static void validateLL12Offset(BigDecimal b) {
      if (b.doubleValue() < -0.0002047 || b.doubleValue() > 0.0002047)
         throw new IllegalArgumentException("Invalid LL_B12_Offset [-0.0002047..0.0002047]: " + b);
   }

   public static void validateLL14Offset(BigDecimal b) {
      if (b.doubleValue() < -0.0008191 || b.doubleValue() > 0.0008191)
         throw new IllegalArgumentException("Invalid LL_B14_Offset [-0.0008191..0.0008191]: " + b);
   }

   public static void validateLL16Offset(BigDecimal b) {
      if (b.doubleValue() < -0.0032767 || b.doubleValue() > 0.0032767)
         throw new IllegalArgumentException("Invalid LL_B16_Offset [-0.0032767..0.0032767]: " + b);
   }

   public static void validateLL18Offset(BigDecimal b) {
      if (b.doubleValue() < -0.0131071 || b.doubleValue() > 0.0131071)
         throw new IllegalArgumentException("Invalid LL_B18_Offset [-0.0131071..0.0131071]: " + b);
   }

   public static void validateLL22Offset(BigDecimal b) {
      if (b.doubleValue() < -0.2097151 || b.doubleValue() > 0.2097151)
         throw new IllegalArgumentException("Invalid LL_B22_Offset [-0.2097151..0.2097151]: " + b);
   }

   public static void validateLL24Offset(BigDecimal b) {
      if (b.doubleValue() < -0.8388607 || b.doubleValue() > 0.8388607)
         throw new IllegalArgumentException("Invalid LL_B24_Offset [-0.8388607..0.8388607]: " + b);
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

   public static void validateAngle(BigDecimal ang) {
      if (ang.doubleValue() < 0 || ang.doubleValue() > 359.9875)
         throw new IllegalArgumentException("Invalid Angle [0..359.9875]: " + ang);
   }

   public static void validateB12Scale(BigDecimal b) {
      if (b.doubleValue() < 0 || b.doubleValue() > 202.35)
         throw new IllegalArgumentException("Invalid B12 Scale [0..202.35]: " + b);
   }

   public static void validateNodeAttribute(String str) {
      String myString = "reserved stopLine roundedCapStyleA roundedCapStyleB mergePoint divergePoint downstreamStopLine donwstreamStartNode closedToTraffic safeIsland curbPresentAtStepOff hydrantPresent";
      CharSequence cs = str;
      if (!myString.contains(cs)) {
         throw new IllegalArgumentException("Invalid NodeAttribute Enumeration");
      }
   }

   public static void validateSegmentAttribute(String str) {
      String myString = "reserved doNotBlock whiteLine mergingLaneLeft mergingLaneRight curbOnLeft curbOnRight loadingzoneOnLeft loadingzoneOnRight turnOutPointOnLeft turnOutPointOnRight adjacentParkingOnLeft adjacentParkingOnRight sharedBikeLane bikeBoxInFront transitStopOnLeft transitStopOnRight transitStopInLane sharedWithTrackedVehicle safeIsland lowCurbsPresent rumbleStripPresent audibleSignalingPresent adaptiveTimingPresent rfSignalRequestPresent partialCurbIntrusion taperToLeft taperToRight taperToCenterLine parallelParking headInParking freeParking timeRestrictionsOnParking costToPark midBlockCurbPresent unEvenPavementPresent";
      CharSequence cs = str;
      if (!myString.contains(cs)) {
         throw new IllegalArgumentException("Invalid SegmentAttribute Enumeration");
      }
   }

   public static void validateSpeedLimitType(String str) {
      String myString = "unknown maxSpeedInSchoolZone maxSpeedInSchoolZoneWhenChildrenArePresent maxSpeedInConstructionZone vehicleMinSpeed vehicleMaxSpeed vehicleNightMaxSpeed truckMinSpeed truckMaxSpeed truckNightMaxSpeed vehiclesWithTrailerMinSpeed vehiclesWithTrailersMaxSpeed vehiclesWithTrailersNightMaxSpeed";
      CharSequence cs = str;
      if (!myString.contains(cs)) {
         throw new IllegalArgumentException("Invalid SpeedLimitAttribute Enumeration");
      }
   }

   public static void validateVelocity(BigDecimal vel) {
      if (vel.doubleValue() < 0 || vel.doubleValue() > 163.82)
         throw new IllegalArgumentException("Invalid Velocity [0..163.82]: " + vel);
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
   
}
