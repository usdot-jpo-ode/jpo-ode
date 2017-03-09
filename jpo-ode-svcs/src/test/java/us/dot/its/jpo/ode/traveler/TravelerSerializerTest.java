package us.dot.its.jpo.ode.traveler;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author 583114
 *
 */
public class TravelerSerializerTest {

   @Test
   public void checkLowerBoundMessageCount() {
      int msgcnt = -1;
      try {
         TravelerMessageBuilder.validateMessageCount(msgcnt);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerMessageCount() {
      int msgcnt = 0;
      try {
         TravelerMessageBuilder.validateMessageCount(msgcnt);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperBoundMessageCount() {
      int msgcnt = 128;
      try {
         TravelerMessageBuilder.validateMessageCount(msgcnt);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperMessageCount() {
      int msgcnt = 127;
      try {
         TravelerMessageBuilder.validateMessageCount(msgcnt);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkURLEmpty() {
      String str = "";
      try {
         TravelerMessageBuilder.validateURL(str);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkURLLower() {
      String str = "a";
      try {
         TravelerMessageBuilder.validateURL(str);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkURLUpper() {
      String str = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
      try {
         TravelerMessageBuilder.validateURL(str);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkURLUpperBound() {
      String str = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
      try {
         TravelerMessageBuilder.validateURL(str);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkURLShortEmpty() {
      String str = "";
      try {
         TravelerMessageBuilder.validateURLShort(str);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkURLShortLower() {
      String str = "a";
      try {
         TravelerMessageBuilder.validateURLShort(str);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkURLShortUpper() {
      String str = "aaaaaaaaaaaaaaa";
      try {
         TravelerMessageBuilder.validateURLShort(str);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkURLShortUpperBound() {
      String str = "aaaaaaaaaaaaaaaa";
      try {
         TravelerMessageBuilder.validateURLShort(str);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerFrame() {
      int x = 0;
      try {
         TravelerMessageBuilder.validateFrameCount(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerFrameBound() {
      int x = 1;
      try {
         TravelerMessageBuilder.validateFrameCount(x);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperFrame() {
      int x = 8;
      try {
         TravelerMessageBuilder.validateFrameCount(x);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperFrameBound() {
      int x = 9;
      try {
         TravelerMessageBuilder.validateFrameCount(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkEmptyMessageID() {
      String str = "";
      try {
         TravelerMessageBuilder.validateMessageID(str);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkRoadMessageID() {
      String str = "RoadSignID";
      try {
         TravelerMessageBuilder.validateMessageID(str);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkFurtherMessageID() {
      String str = "furtherInfoID";
      try {
         TravelerMessageBuilder.validateMessageID(str);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkInvalidMessageID() {
      String str = "testString";
      try {
         TravelerMessageBuilder.validateMessageID(str);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerStartTime() {
      long time = -1;
      try {
         TravelerMessageBuilder.validateStartTime(time);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundStartTime() {
      long time = 0;
      try {
         TravelerMessageBuilder.validateStartTime(time);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperStartTime() {
      long time = 527041;
      try {
         TravelerMessageBuilder.validateStartTime(time);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundStartTime() {
      long time = 527040;
      try {
         TravelerMessageBuilder.validateStartTime(time);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLowerYear() {
      int year = -1;
      try {
         TravelerMessageBuilder.validateStartYear(year);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundYear() {
      int year = 0;
      try {
         TravelerMessageBuilder.validateStartYear(year);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperYear() {
      int year = 4096;
      try {
         TravelerMessageBuilder.validateStartYear(year);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundYear() {
      int year = 4095;
      try {
         TravelerMessageBuilder.validateStartYear(year);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLowerMinutes() {
      int minute = -1;
      try {
         TravelerMessageBuilder.validateMinutesDuration(minute);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundMinutes() {
      int minute = 0;
      try {
         TravelerMessageBuilder.validateMinutesDuration(minute);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperMinutes() {
      int minute = 32001;
      try {
         TravelerMessageBuilder.validateMinutesDuration(minute);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundMinutes() {
      int minute = 32000;
      try {
         TravelerMessageBuilder.validateMinutesDuration(minute);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLowerBoundSSPIndex() {
      short index = -1;
      try {
         TravelerMessageBuilder.validateHeaderIndex(index);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerSSPIndex() {
      short index = 0;
      try {
         TravelerMessageBuilder.validateHeaderIndex(index);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperSSPIndex() {
      short index = 31;
      try {
         TravelerMessageBuilder.validateHeaderIndex(index);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperBoundSSPIndex() {
      short index = 32;
      try {
         TravelerMessageBuilder.validateHeaderIndex(index);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundInfoType() {
      int type = -1;
      try {
         TravelerMessageBuilder.validateInfoType(type);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerInfoType() {
      int type = 0;
      try {
         TravelerMessageBuilder.validateInfoType(type);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLowerBoundLat() {
      long lat = -900000001;
      try {
         TravelerMessageBuilder.validateLat(lat);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundLat() {
      long lat = 900000002;
      try {
         TravelerMessageBuilder.validateLat(lat);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerLat() {
      long lat = -900000000;
      try {
         TravelerMessageBuilder.validateLat(lat);
      } catch (RuntimeException e) {
         fail("Unexcpeted Exception");
      }
   }

   @Test
   public void checkUpperLat() {
      long lat = 900000001;
      try {
         TravelerMessageBuilder.validateLat(lat);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLowerBoundLong() {
      long longg = -1800000000;
      try {
         TravelerMessageBuilder.validateLong(longg);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundLong() {
      long longg = 1800000002;
      try {
         TravelerMessageBuilder.validateLong(longg);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerLong() {
      long longg = -1799999999;
      try {
         TravelerMessageBuilder.validateLong(longg);
      } catch (RuntimeException e) {
         fail("Unexcpeted Exception");
      }
   }

   @Test
   public void checkUpperLong() {
      long longg = 1800000001;
      try {
         TravelerMessageBuilder.validateLong(longg);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperHeading() {
      String head = "100110110101010101011001";
      try {
         TravelerMessageBuilder.validateHeading(head);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerElevation() {
      long elev = -4097;
      try {
         TravelerMessageBuilder.validateElevation(elev);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundElevation() {
      long elev = -4096;
      try {
         TravelerMessageBuilder.validateElevation(elev);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperElevation() {
      long elev = 61440;
      try {
         TravelerMessageBuilder.validateElevation(elev);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundElevation() {
      long elev = 61439;
      try {
         TravelerMessageBuilder.validateElevation(elev);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkEmptyHeading() {
      String head = "";
      try {
         TravelerMessageBuilder.validateHeading(head);
         fail("Expected IllegalArgument Exception");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerHeading() {
      String head = "01001010";
      try {
         TravelerMessageBuilder.validateHeading(head);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkHeading() {
      String head = "1011011010101100";
      try {
         TravelerMessageBuilder.validateHeading(head);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLowerMUTCD() {
      int code = -1;
      try {
         TravelerMessageBuilder.validateMUTCDCode(code);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundMUTCD() {
      int code = 0;
      try {
         TravelerMessageBuilder.validateMUTCDCode(code);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperMUTCD() {
      int code = 7;
      try {
         TravelerMessageBuilder.validateMUTCDCode(code);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundMUTCD() {
      int code = 6;
      try {
         TravelerMessageBuilder.validateMUTCDCode(code);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperBoundSign() {
      int sign = 8;
      try {
         TravelerMessageBuilder.validateSign(sign);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundSign() {
      int sign = -1;
      try {
         TravelerMessageBuilder.validateSign(sign);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerSign() {
      int sign = 0;
      try {
         TravelerMessageBuilder.validateSign(sign);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperSign() {
      int sign = 7;
      try {
         TravelerMessageBuilder.validateSign(sign);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperBoundITISCodes() {
      String code = "65536";
      try {
         TravelerMessageBuilder.validateITISCodes(code);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundITISCodes() {
      String code = "-1";
      try {
         TravelerMessageBuilder.validateITISCodes(code);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerITISCodes() {
      String code = "0";
      try {
         TravelerMessageBuilder.validateITISCodes(code);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperITISCodes() {
      String code = "65535";
      try {
         TravelerMessageBuilder.validateITISCodes(code);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkEmptyITISCodesString() {
      String code = "";
      try {
         TravelerMessageBuilder.validateITISCodes(code);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerITISCodesString() {
      String code = "a";
      try {
         TravelerMessageBuilder.validateITISCodes(code);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperITISCodesString() {
      String code = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
      try {
         TravelerMessageBuilder.validateITISCodes(code);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }
   
   @Test
   public void checkUpperBoundITISCodesString() {
      String code = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
      try {
         TravelerMessageBuilder.validateITISCodes(code);
         fail("Unexpected Exception");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkUpperBoundContentCodes() {
      String code = "65536";
      try {
         TravelerMessageBuilder.validateContentCodes(code);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundContentCodes() {
      String code = "-1";
      try {
         TravelerMessageBuilder.validateContentCodes(code);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerContentCodes() {
      String code = "0";
      try {
         TravelerMessageBuilder.validateContentCodes(code);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperContentCodes() {
      String code = "65535";
      try {
         TravelerMessageBuilder.validateContentCodes(code);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkEmptyContentCodesString() {
      String code = "";
      try {
         TravelerMessageBuilder.validateContentCodes(code);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerContentCodesString() {
      String code = "a";
      try {
         TravelerMessageBuilder.validateContentCodes(code);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperContentCodesString() {
      String code = "aaaaaaaaaaaaaaaa";
      try {
         TravelerMessageBuilder.validateITISCodes(code);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }
   
   @Test
   public void checkUpperBoundContentCodesString() {
      String code = "aaaaaaaaaaaaaaaaa";
      try {
         TravelerMessageBuilder.validateContentCodes(code);
         fail("Unexpected Exception");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkEmptyString() {
      String str = "";
      try {
         TravelerMessageBuilder.validateString(str);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkNonEmptyString() {
      String str = "a";
      try {
         TravelerMessageBuilder.validateString(str);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }
   
   @Test
   public void checkEmptyGeoName() {
      String str = "";
      try {
         TravelerMessageBuilder.validateGeoName(str);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e)
      {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkLowerGeoName() {
      String str = "a";
      try {
         TravelerMessageBuilder.validateGeoName(str);
      }
      catch (RuntimeException e)
      {
         fail("Unexpected Exception");
      }
   }
   
   @Test
   public void checkUpperGeoName() {
      String str = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
      try {
         TravelerMessageBuilder.validateGeoName(str);
      }
      catch (RuntimeException e)
      {
         fail("Unexpected Exception");
      }
   }
   
   @Test
   public void checkUpperBoundGeoName() {
      String str = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
      try {
         TravelerMessageBuilder.validateGeoName(str);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e)
      {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkLowerRoadID() {
      int road = -1;
      try {
         TravelerMessageBuilder.validateRoadID(road);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   @Test
   public void checkLowerBoundRoadID() {
      int road = 0;
      try {
         TravelerMessageBuilder.validateRoadID(road);
      }
      catch (RuntimeException e) {
         fail("Unexpected exception");
      }
   }
   @Test
   public void checkUpperRoadID() {
      int road = 65536;
      try {
         TravelerMessageBuilder.validateRoadID(road);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   @Test
   public void checkUpperBoundRoadID() {
      int road = 65535;
      try {
         TravelerMessageBuilder.validateRoadID(road);
      }
      catch (RuntimeException e) {
         fail("Expected IllegalArgumentException");
      }
   }
   @Test
   public void checkLowerLaneWidth() {
      int lane = -1;
      try {
         TravelerMessageBuilder.validateLaneWidth(lane);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkLowerBoundLaneWidth() {
      int lane = 0;
      try {
         TravelerMessageBuilder.validateLaneWidth(lane);
      }
      catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }
   
   @Test
   public void checkUpperLaneWidth() {
      int lane = 32768;
      try {
         TravelerMessageBuilder.validateLaneWidth(lane);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkUpperBoundLaneWidth() {
      int lane = 32767;
      try {
         TravelerMessageBuilder.validateLaneWidth(lane);
      }
      catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }
   
   @Test
   public void checkLowerDirectionality() {
      long dir = -1;
      try {
         TravelerMessageBuilder.validateDirectionality(dir);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkLowerBoundDirectionality() {
      long dir = 0;
      try {
         TravelerMessageBuilder.validateDirectionality(dir);
      }
      catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }
   
   @Test
   public void checkUpperDirectionality() {
      long dir = 4;
      try {
         TravelerMessageBuilder.validateDirectionality(dir);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkUpperBoundDirectionality() {
      long dir = 3;
      try {
         TravelerMessageBuilder.validateDirectionality(dir);
      }
      catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }
   
   @Test
   public void checkLowerZoom() {
      int zoom = -1;
      try {
         TravelerMessageBuilder.validateZoom(zoom);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkLowerBoundZoom() {
      int zoom = 0;
      try {
         TravelerMessageBuilder.validateZoom(zoom);
      }
      catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }
   
   @Test
   public void checkUpperZoom() {
      int zoom = 16;
      try {
         TravelerMessageBuilder.validateZoom(zoom);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkUpperBoundZoom() {
      int zoom = 15;
      try {
         TravelerMessageBuilder.validateZoom(zoom);
      }
      catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }
   
   @Test
   public void checkLowerExtent() {
      int extent = -1;
      try {
         TravelerMessageBuilder.validateExtent(extent);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkLowerBoundExtent() {
      int extent = 0;
      try {
         TravelerMessageBuilder.validateExtent(extent);
      }
      catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }
   
   @Test
   public void checkUpperExtent() {
      int extent = 16;
      try {
         TravelerMessageBuilder.validateExtent(extent);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkUpperBoundExtent() {
      int extent = 15;
      try {
         TravelerMessageBuilder.validateExtent(extent);
      }
      catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }
   
   @Test
   public void checkLowerRadius() {
      int rad = -1;
      try {
         TravelerMessageBuilder.validateRadius(rad);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkLowerBoundRadius() {
      int rad = 0;
      try {
         TravelerMessageBuilder.validateRadius(rad);
      }
      catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }
   
   @Test
   public void checkUpperRadius() {
      int rad = 4096;
      try {
         TravelerMessageBuilder.validateRadius(rad);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkUpperBoundRadius() {
      int rad = 4095;
      try {
         TravelerMessageBuilder.validateRadius(rad);
      }
      catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }
   
   @Test
   public void checkLowerUnits() {
      int unit = -1;
      try {
         TravelerMessageBuilder.validateUnits(unit);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkLowerBoundUnits() {
      int unit = 0;
      try {
         TravelerMessageBuilder.validateUnits(unit);
      }
      catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }
   
   @Test
   public void checkUpperUnits() {
      int unit = 8;
      try {
         TravelerMessageBuilder.validateUnits(unit);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkUpperBoundUnits() {
      int unit = 7;
      try {
         TravelerMessageBuilder.validateUnits(unit);
      }
      catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }
   
   @Test
   public void checkLowerX16Offset() {
      int x = -32769;
      try {
         TravelerMessageBuilder.validatex16Offset(x);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkLowerBoundX16Offset() {
      int x = -32768;
      try {
         TravelerMessageBuilder.validatex16Offset(x);
      }
      catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }
   
   @Test
   public void checkUpperX16Offset() {
      int x = 32768;
      try {
         TravelerMessageBuilder.validatex16Offset(x);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkUpperBoundX16Offset() {
      int x = 32767;
      try {
         TravelerMessageBuilder.validatex16Offset(x);
      }
      catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }
   
   @Test
   public void checkLowerY16Offset() {
      int y = -32769;
      try {
         TravelerMessageBuilder.validatey16Offset(y);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkLowerBoundY16Offset() {
      int y = -32768;
      try {
         TravelerMessageBuilder.validatey16Offset(y);
      }
      catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }
   
   @Test
   public void checkUpperY16Offset() {
      int y = 32768;
      try {
         TravelerMessageBuilder.validatey16Offset(y);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkUpperBoundY16Offset() {
      int y = 32767;
      try {
         TravelerMessageBuilder.validatey16Offset(y);
      }
      catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }
   
   @Test
   public void checkLowerZ16Offset() {
      int z = -32769;
      try {
         TravelerMessageBuilder.validatez16Offset(z);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkLowerBoundZ16Offset() {
      int z = -32768;
      try {
         TravelerMessageBuilder.validatez16Offset(z);
      }
      catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }
   
   @Test
   public void checkUpperZ16Offset() {
      int z = 32768;
      try {
         TravelerMessageBuilder.validatez16Offset(z);
         fail("Expected IllegalArgumentException");
      }
      catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
   
   @Test
   public void checkUpperBoundZ16Offset() {
      int z = 32767;
      try {
         TravelerMessageBuilder.validatez16Offset(z);
      }
      catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }
}
