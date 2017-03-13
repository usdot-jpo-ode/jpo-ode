package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Ignore;
import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.TravelerInformation;
import us.dot.its.jpo.ode.plugin.j2735.J2735Position3D;
import us.dot.its.jpo.ode.plugin.j2735.J2735TravelerInputData;

public class OssTravelerMessageBuilderTest {
   @Test
   public void checkLowerBoundMessageCount() {
      int msgcnt = -1;
      try {
         OssTravelerMessageBuilder.validateMessageCount(msgcnt);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerMessageCount() {
      int msgcnt = 0;
      try {
         OssTravelerMessageBuilder.validateMessageCount(msgcnt);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperBoundMessageCount() {
      int msgcnt = 128;
      try {
         OssTravelerMessageBuilder.validateMessageCount(msgcnt);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperMessageCount() {
      int msgcnt = 127;
      try {
         OssTravelerMessageBuilder.validateMessageCount(msgcnt);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkURLEmpty() {
      String str = "";
      try {
         OssTravelerMessageBuilder.validateURL(str);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkURLLower() {
      String str = "a";
      try {
         OssTravelerMessageBuilder.validateURL(str);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkURLUpper() {
      String str = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
      try {
         OssTravelerMessageBuilder.validateURL(str);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkURLUpperBound() {
      String str = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
      try {
         OssTravelerMessageBuilder.validateURL(str);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkURLShortEmpty() {
      String str = "";
      try {
         OssTravelerMessageBuilder.validateURLShort(str);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkURLShortLower() {
      String str = "a";
      try {
         OssTravelerMessageBuilder.validateURLShort(str);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkURLShortUpper() {
      String str = "aaaaaaaaaaaaaaa";
      try {
         OssTravelerMessageBuilder.validateURLShort(str);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkURLShortUpperBound() {
      String str = "aaaaaaaaaaaaaaaa";
      try {
         OssTravelerMessageBuilder.validateURLShort(str);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerFrame() {
      int x = 0;
      try {
         OssTravelerMessageBuilder.validateFrameCount(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerFrameBound() {
      int x = 1;
      try {
         OssTravelerMessageBuilder.validateFrameCount(x);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperFrame() {
      int x = 8;
      try {
         OssTravelerMessageBuilder.validateFrameCount(x);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperFrameBound() {
      int x = 9;
      try {
         OssTravelerMessageBuilder.validateFrameCount(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkEmptyMessageID() {
      String str = "";
      try {
         OssTravelerMessageBuilder.validateMessageID(str);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkRoadMessageID() {
      String str = "RoadSignID";
      try {
         OssTravelerMessageBuilder.validateMessageID(str);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkFurtherMessageID() {
      String str = "furtherInfoID";
      try {
         OssTravelerMessageBuilder.validateMessageID(str);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkInvalidMessageID() {
      String str = "testString";
      try {
         OssTravelerMessageBuilder.validateMessageID(str);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerStartTime() {
      long time = -1;
      try {
         OssTravelerMessageBuilder.validateStartTime(time);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundStartTime() {
      long time = 0;
      try {
         OssTravelerMessageBuilder.validateStartTime(time);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperStartTime() {
      long time = 527041;
      try {
         OssTravelerMessageBuilder.validateStartTime(time);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundStartTime() {
      long time = 527040;
      try {
         OssTravelerMessageBuilder.validateStartTime(time);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLowerYear() {
      int year = -1;
      try {
         OssTravelerMessageBuilder.validateStartYear(year);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundYear() {
      int year = 0;
      try {
         OssTravelerMessageBuilder.validateStartYear(year);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperYear() {
      int year = 4096;
      try {
         OssTravelerMessageBuilder.validateStartYear(year);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundYear() {
      int year = 4095;
      try {
         OssTravelerMessageBuilder.validateStartYear(year);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLowerMinutes() {
      int minute = -1;
      try {
         OssTravelerMessageBuilder.validateMinutesDuration(minute);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundMinutes() {
      int minute = 0;
      try {
         OssTravelerMessageBuilder.validateMinutesDuration(minute);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperMinutes() {
      int minute = 32001;
      try {
         OssTravelerMessageBuilder.validateMinutesDuration(minute);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundMinutes() {
      int minute = 32000;
      try {
         OssTravelerMessageBuilder.validateMinutesDuration(minute);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLowerBoundSSPIndex() {
      short index = -1;
      try {
         OssTravelerMessageBuilder.validateHeaderIndex(index);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerSSPIndex() {
      short index = 0;
      try {
         OssTravelerMessageBuilder.validateHeaderIndex(index);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperSSPIndex() {
      short index = 31;
      try {
         OssTravelerMessageBuilder.validateHeaderIndex(index);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperBoundSSPIndex() {
      short index = 32;
      try {
         OssTravelerMessageBuilder.validateHeaderIndex(index);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundInfoType() {
      int type = -1;
      try {
         OssTravelerMessageBuilder.validateInfoType(type);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerInfoType() {
      int type = 0;
      try {
         OssTravelerMessageBuilder.validateInfoType(type);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLowerBoundLat() {
      J2735Position3D position = new J2735Position3D(
            BigDecimal.valueOf(-90.1),
            BigDecimal.valueOf(45.0),
            BigDecimal.valueOf(1000.0));
      try {
         OssTravelerMessageBuilder.validatePosition(position);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundLat() {
      J2735Position3D position = new J2735Position3D(
            BigDecimal.valueOf(90.1),
            BigDecimal.valueOf(45.0),
            BigDecimal.valueOf(1000.0));
      try {
         OssTravelerMessageBuilder.validatePosition(position);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerLat() {
      J2735Position3D position = new J2735Position3D(
            BigDecimal.valueOf(-90.0),
            BigDecimal.valueOf(45.0),
            BigDecimal.valueOf(1000.0));
      try {
         OssTravelerMessageBuilder.validatePosition(position);
      } catch (RuntimeException e) {
         fail("Unexcpeted Exception");
      }
   }

   @Test
   public void checkUpperLat() {
      J2735Position3D position = new J2735Position3D(
            BigDecimal.valueOf(90.0),
            BigDecimal.valueOf(45.0),
            BigDecimal.valueOf(1000.0));
      try {
         OssTravelerMessageBuilder.validatePosition(position);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLowerBoundLong() {
      J2735Position3D position = new J2735Position3D(
            BigDecimal.valueOf(45.0),
            BigDecimal.valueOf(-180.1),
            BigDecimal.valueOf(1000.0));
      try {
         OssTravelerMessageBuilder.validatePosition(position);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundLong() {
      J2735Position3D position = new J2735Position3D(
            BigDecimal.valueOf(45),
            BigDecimal.valueOf(180.1),
            BigDecimal.valueOf(1000.0));
      try {
         OssTravelerMessageBuilder.validatePosition(position);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerLong() {
      J2735Position3D position = new J2735Position3D(
            BigDecimal.valueOf(45),
            BigDecimal.valueOf(-180.0),
            BigDecimal.valueOf(1000.0));
      try {
         OssTravelerMessageBuilder.validatePosition(position);
      } catch (RuntimeException e) {
         fail("Unexcpeted Exception");
      }
   }

   @Test
   public void checkUpperLong() {
      J2735Position3D position = new J2735Position3D(
            BigDecimal.valueOf(45),
            BigDecimal.valueOf(180.0),
            BigDecimal.valueOf(1000.0));
      try {
         OssTravelerMessageBuilder.validatePosition(position);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLowerBoundElevation() {
      J2735Position3D position = new J2735Position3D(
            BigDecimal.valueOf(45),
            BigDecimal.valueOf(45.0),
            BigDecimal.valueOf(-409.6));
      try {
         OssTravelerMessageBuilder.validatePosition(position);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundElevation() {
      J2735Position3D position = new J2735Position3D(
            BigDecimal.valueOf(45),
            BigDecimal.valueOf(45.0),
            BigDecimal.valueOf(6143.91));
      try {
         OssTravelerMessageBuilder.validatePosition(position);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerElevation() {
      J2735Position3D position = new J2735Position3D(
            BigDecimal.valueOf(45),
            BigDecimal.valueOf(45.0),
            BigDecimal.valueOf(-409.5));
      try {
         OssTravelerMessageBuilder.validatePosition(position);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperElevation() {
      J2735Position3D position = new J2735Position3D(
            BigDecimal.valueOf(45),
            BigDecimal.valueOf(45.0),
            BigDecimal.valueOf(6143.9));
      try {
         OssTravelerMessageBuilder.validatePosition(position);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperHeading() {
      String head = "100110110101010101011001";
      try {
         OssTravelerMessageBuilder.validateHeading(head);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkEmptyHeading() {
      String head = "";
      try {
         OssTravelerMessageBuilder.validateHeading(head);
         fail("Expected IllegalArgument Exception");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerHeading() {
      String head = "01001010";
      try {
         OssTravelerMessageBuilder.validateHeading(head);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkHeading() {
      String head = "1011011010101100";
      try {
         OssTravelerMessageBuilder.validateHeading(head);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLowerMUTCD() {
      int code = -1;
      try {
         OssTravelerMessageBuilder.validateMUTCDCode(code);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundMUTCD() {
      int code = 0;
      try {
         OssTravelerMessageBuilder.validateMUTCDCode(code);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperMUTCD() {
      int code = 7;
      try {
         OssTravelerMessageBuilder.validateMUTCDCode(code);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundMUTCD() {
      int code = 6;
      try {
         OssTravelerMessageBuilder.validateMUTCDCode(code);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperBoundSign() {
      int sign = 8;
      try {
         OssTravelerMessageBuilder.validateSign(sign);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundSign() {
      int sign = -1;
      try {
         OssTravelerMessageBuilder.validateSign(sign);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerSign() {
      int sign = 0;
      try {
         OssTravelerMessageBuilder.validateSign(sign);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperSign() {
      int sign = 7;
      try {
         OssTravelerMessageBuilder.validateSign(sign);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperBoundITISCodes() {
      String code = "65536";
      try {
         OssTravelerMessageBuilder.validateITISCodes(code);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundITISCodes() {
      String code = "-1";
      try {
         OssTravelerMessageBuilder.validateITISCodes(code);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerITISCodes() {
      String code = "0";
      try {
         OssTravelerMessageBuilder.validateITISCodes(code);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperITISCodes() {
      String code = "65535";
      try {
         OssTravelerMessageBuilder.validateITISCodes(code);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkEmptyITISCodesString() {
      String code = "";
      try {
         OssTravelerMessageBuilder.validateITISCodes(code);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerITISCodesString() {
      String code = "a";
      try {
         OssTravelerMessageBuilder.validateITISCodes(code);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperITISCodesString() {
      String code = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
      try {
         OssTravelerMessageBuilder.validateITISCodes(code);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperBoundITISCodesString() {
      String code = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
      try {
         OssTravelerMessageBuilder.validateITISCodes(code);
         fail("Unexpected Exception");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundContentCodes() {
      String code = "65536";
      try {
         OssTravelerMessageBuilder.validateContentCodes(code);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundContentCodes() {
      String code = "-1";
      try {
         OssTravelerMessageBuilder.validateContentCodes(code);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerContentCodes() {
      String code = "0";
      try {
         OssTravelerMessageBuilder.validateContentCodes(code);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperContentCodes() {
      String code = "65535";
      try {
         OssTravelerMessageBuilder.validateContentCodes(code);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkEmptyContentCodesString() {
      String code = "";
      try {
         OssTravelerMessageBuilder.validateContentCodes(code);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerContentCodesString() {
      String code = "a";
      try {
         OssTravelerMessageBuilder.validateContentCodes(code);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperContentCodesString() {
      String code = "aaaaaaaaaaaaaaaa";
      try {
         OssTravelerMessageBuilder.validateITISCodes(code);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperBoundContentCodesString() {
      String code = "aaaaaaaaaaaaaaaaa";
      try {
         OssTravelerMessageBuilder.validateContentCodes(code);
         fail("Unexpected Exception");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkEmptyString() {
      String str = "";
      try {
         OssTravelerMessageBuilder.validateString(str);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkNonEmptyString() {
      String str = "a";
      try {
         OssTravelerMessageBuilder.validateString(str);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkEmptyGeoName() {
      String str = "";
      try {
         OssTravelerMessageBuilder.validateGeoName(str);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerGeoName() {
      String str = "a";
      try {
         OssTravelerMessageBuilder.validateGeoName(str);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperGeoName() {
      String str = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
      try {
         OssTravelerMessageBuilder.validateGeoName(str);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperBoundGeoName() {
      String str = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
      try {
         OssTravelerMessageBuilder.validateGeoName(str);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerRoadID() {
      int road = -1;
      try {
         OssTravelerMessageBuilder.validateRoadID(road);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundRoadID() {
      int road = 0;
      try {
         OssTravelerMessageBuilder.validateRoadID(road);
      } catch (RuntimeException e) {
         fail("Unexpected exception");
      }
   }

   @Test
   public void checkUpperRoadID() {
      int road = 65536;
      try {
         OssTravelerMessageBuilder.validateRoadID(road);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundRoadID() {
      int road = 65535;
      try {
         OssTravelerMessageBuilder.validateRoadID(road);
      } catch (RuntimeException e) {
         fail("Expected IllegalArgumentException");
      }
   }

   @Test
   public void checkLowerLaneWidth() {
      int lane = -1;
      try {
         OssTravelerMessageBuilder.validateLaneWidth(lane);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundLaneWidth() {
      int lane = 0;
      try {
         OssTravelerMessageBuilder.validateLaneWidth(lane);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperLaneWidth() {
      int lane = 32768;
      try {
         OssTravelerMessageBuilder.validateLaneWidth(lane);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundLaneWidth() {
      int lane = 32767;
      try {
         OssTravelerMessageBuilder.validateLaneWidth(lane);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLowerDirectionality() {
      long dir = -1;
      try {
         OssTravelerMessageBuilder.validateDirectionality(dir);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundDirectionality() {
      long dir = 0;
      try {
         OssTravelerMessageBuilder.validateDirectionality(dir);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperDirectionality() {
      long dir = 4;
      try {
         OssTravelerMessageBuilder.validateDirectionality(dir);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundDirectionality() {
      long dir = 3;
      try {
         OssTravelerMessageBuilder.validateDirectionality(dir);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLowerZoom() {
      int zoom = -1;
      try {
         OssTravelerMessageBuilder.validateZoom(zoom);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundZoom() {
      int zoom = 0;
      try {
         OssTravelerMessageBuilder.validateZoom(zoom);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperZoom() {
      int zoom = 16;
      try {
         OssTravelerMessageBuilder.validateZoom(zoom);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundZoom() {
      int zoom = 15;
      try {
         OssTravelerMessageBuilder.validateZoom(zoom);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLowerExtent() {
      int extent = -1;
      try {
         OssTravelerMessageBuilder.validateExtent(extent);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundExtent() {
      int extent = 0;
      try {
         OssTravelerMessageBuilder.validateExtent(extent);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperExtent() {
      int extent = 16;
      try {
         OssTravelerMessageBuilder.validateExtent(extent);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundExtent() {
      int extent = 15;
      try {
         OssTravelerMessageBuilder.validateExtent(extent);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLowerRadius() {
      int rad = -1;
      try {
         OssTravelerMessageBuilder.validateRadius(rad);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundRadius() {
      int rad = 0;
      try {
         OssTravelerMessageBuilder.validateRadius(rad);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperRadius() {
      int rad = 4096;
      try {
         OssTravelerMessageBuilder.validateRadius(rad);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundRadius() {
      int rad = 4095;
      try {
         OssTravelerMessageBuilder.validateRadius(rad);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLowerUnits() {
      int unit = -1;
      try {
         OssTravelerMessageBuilder.validateUnits(unit);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundUnits() {
      int unit = 0;
      try {
         OssTravelerMessageBuilder.validateUnits(unit);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperUnits() {
      int unit = 8;
      try {
         OssTravelerMessageBuilder.validateUnits(unit);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundUnits() {
      int unit = 7;
      try {
         OssTravelerMessageBuilder.validateUnits(unit);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLowerX16Offset() {
      int x = -32769;
      try {
         OssTravelerMessageBuilder.validatex16Offset(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundX16Offset() {
      int x = -32768;
      try {
         OssTravelerMessageBuilder.validatex16Offset(x);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperX16Offset() {
      int x = 32768;
      try {
         OssTravelerMessageBuilder.validatex16Offset(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundX16Offset() {
      int x = 32767;
      try {
         OssTravelerMessageBuilder.validatex16Offset(x);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLowerY16Offset() {
      int y = -32769;
      try {
         OssTravelerMessageBuilder.validatey16Offset(y);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundY16Offset() {
      int y = -32768;
      try {
         OssTravelerMessageBuilder.validatey16Offset(y);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperY16Offset() {
      int y = 32768;
      try {
         OssTravelerMessageBuilder.validatey16Offset(y);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundY16Offset() {
      int y = 32767;
      try {
         OssTravelerMessageBuilder.validatey16Offset(y);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLowerZ16Offset() {
      int z = -32769;
      try {
         OssTravelerMessageBuilder.validatez16Offset(z);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundZ16Offset() {
      int z = -32768;
      try {
         OssTravelerMessageBuilder.validatez16Offset(z);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperZ16Offset() {
      int z = 32768;
      try {
         OssTravelerMessageBuilder.validatez16Offset(z);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundZ16Offset() {
      int z = 32767;
      try {
         OssTravelerMessageBuilder.validatez16Offset(z);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkNodeEnumeration() {
      String str = "reseved";
      try {
         OssTravelerMessageBuilder.validateNodeAttribute(str);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkBoundNodeEnumeration() {
      String str = "reserved";
      try {
         OssTravelerMessageBuilder.validateNodeAttribute(str);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkSegmentEnumeration() {
      String str = "freParking";
      try {
         OssTravelerMessageBuilder.validateSegmentAttribute(str);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkBoundSegmentEnumeration() {
      String str = "freeParking";
      try {
         OssTravelerMessageBuilder.validateSegmentAttribute(str);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkSpeedLimitEnumeration() {
      String str = "maxSpedInSchoolZoneWhenChildrenArePresent";
      try {
         OssTravelerMessageBuilder.validateSpeedLimitType(str);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkBoundSpeedLimitEnumeration() {
      String str = "maxSpeedInSchoolZoneWhenChildrenArePresent";
      try {
         OssTravelerMessageBuilder.validateSpeedLimitType(str);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerB10Offset() {
      int x = -513;
      try {
         OssTravelerMessageBuilder.validateB10Offset(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundB10Offset() {
      int x = 511;
      try {
         OssTravelerMessageBuilder.validateB10Offset(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkUpperB10Offset() {
      int x = 512;
      try {
         OssTravelerMessageBuilder.validateB10Offset(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundB10Offset() {
      int x = 511;
      try {
         OssTravelerMessageBuilder.validateB10Offset(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerB11Offset() {
      int x = -1025;
      try {
         OssTravelerMessageBuilder.validateB11Offset(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundB11Offset() {
      int x = -1024;
      try {
         OssTravelerMessageBuilder.validateB11Offset(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkUpperB11Offset() {
      int x = 1024;
      try {
         OssTravelerMessageBuilder.validateB11Offset(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundB11Offset() {
      int x = 1023;
      try {
         OssTravelerMessageBuilder.validateB11Offset(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerB12Offset() {
      int x = -2049;
      try {
         OssTravelerMessageBuilder.validateB12Offset(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundB12Offset() {
      int x = -2048;
      try {
         OssTravelerMessageBuilder.validateB12Offset(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkUpperB12Offset() {
      int x = 2048;
      try {
         OssTravelerMessageBuilder.validateB12Offset(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundB12Offset() {
      int x = 2047;
      try {
         OssTravelerMessageBuilder.validateB12Offset(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerB13Offset() {
      int x = -4097;
      try {
         OssTravelerMessageBuilder.validateB13Offset(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundB13Offset() {
      int x = -4096;
      try {
         OssTravelerMessageBuilder.validateB13Offset(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkUpperB13Offset() {
      int x = 4096;
      try {
         OssTravelerMessageBuilder.validateB13Offset(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundB13Offset() {
      int x = 4095;
      try {
         OssTravelerMessageBuilder.validateB13Offset(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerB14Offset() {
      int x = -8193;
      try {
         OssTravelerMessageBuilder.validateB14Offset(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundB14Offset() {
      int x = -8192;
      try {
         OssTravelerMessageBuilder.validateB14Offset(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkUpperB14Offset() {
      int x = 8192;
      try {
         OssTravelerMessageBuilder.validateB14Offset(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundB14Offset() {
      int x = 8191;
      try {
         OssTravelerMessageBuilder.validateB14Offset(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerB16Offset() {
      int x = -32769;
      try {
         OssTravelerMessageBuilder.validateB16Offset(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundB16Offset() {
      int x = -32768;
      try {
         OssTravelerMessageBuilder.validateB16Offset(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkUpperB16Offset() {
      int x = 32768;
      try {
         OssTravelerMessageBuilder.validateB16Offset(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundB16Offset() {
      int x = 32767;
      try {
         OssTravelerMessageBuilder.validateB16Offset(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerLL12Offset() {
      int x = -2049;
      try {
         OssTravelerMessageBuilder.validateLL12Offset(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundLL12Offset() {
      int x = -2048;
      try {
         OssTravelerMessageBuilder.validateLL12Offset(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkUpperLL12Offset() {
      int x = 2048;
      try {
         OssTravelerMessageBuilder.validateLL12Offset(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundLL12Offset() {
      int x = 2047;
      try {
         OssTravelerMessageBuilder.validateLL12Offset(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerLL14Offset() {
      int x = -8193;
      try {
         OssTravelerMessageBuilder.validateLL14Offset(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundLL14Offset() {
      int x = -8192;
      try {
         OssTravelerMessageBuilder.validateLL14Offset(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkUpperLL14Offset() {
      int x = 8192;
      try {
         OssTravelerMessageBuilder.validateLL14Offset(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundLL14Offset() {
      int x = 8191;
      try {
         OssTravelerMessageBuilder.validateLL14Offset(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerLL16Offset() {
      int x = -32769;
      try {
         OssTravelerMessageBuilder.validateLL16Offset(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundLL16Offset() {
      int x = -32768;
      try {
         OssTravelerMessageBuilder.validateLL16Offset(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkUpperLL16Offset() {
      int x = 32768;
      try {
         OssTravelerMessageBuilder.validateLL16Offset(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundLL16Offset() {
      int x = 32767;
      try {
         OssTravelerMessageBuilder.validateLL16Offset(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerLL18Offset() {
      int x = -131073;
      try {
         OssTravelerMessageBuilder.validateLL18Offset(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundLL18Offset() {
      int x = -131072;
      try {
         OssTravelerMessageBuilder.validateLL18Offset(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkUpperLL18Offset() {
      int x = 131072;
      try {
         OssTravelerMessageBuilder.validateLL18Offset(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundLL18Offset() {
      int x = 131071;
      try {
         OssTravelerMessageBuilder.validateLL18Offset(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerLL22Offset() {
      int x = -2097153;
      try {
         OssTravelerMessageBuilder.validateLL22Offset(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundLL22Offset() {
      int x = -2097152;
      try {
         OssTravelerMessageBuilder.validateLL22Offset(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkUpperLL22Offset() {
      int x = 2097152;
      try {
         OssTravelerMessageBuilder.validateLL22Offset(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundLL22Offset() {
      int x = 2097151;
      try {
         OssTravelerMessageBuilder.validateLL22Offset(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerLL24Offset() {
      int x = -8388609;
      try {
         OssTravelerMessageBuilder.validateLL24Offset(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundLL24Offset() {
      int x = -8388608;
      try {
         OssTravelerMessageBuilder.validateLL24Offset(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkUpperLL24Offset() {
      int x = 8388608;
      try {
         OssTravelerMessageBuilder.validateLL24Offset(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundLL24Offset() {
      int x = 8388607;
      try {
         OssTravelerMessageBuilder.validateLL24Offset(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerLaneID() {
      int x = -1;
      try {
         OssTravelerMessageBuilder.validateLaneID(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundLaneID() {
      int x = 0;
      try {
         OssTravelerMessageBuilder.validateLaneID(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkUpperLaneID() {
      int x = 256;
      try {
         OssTravelerMessageBuilder.validateLaneID(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundLaneID() {
      int x = 255;
      try {
         OssTravelerMessageBuilder.validateLaneID(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerSmallDrivenLine() {
      int x = -2048;
      try {
         OssTravelerMessageBuilder.validateSmallDrivenLine(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundSmallDrivenLine() {
      int x = -2047;
      try {
         OssTravelerMessageBuilder.validateSmallDrivenLine(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkUpperSmallDrivenLine() {
      int x = 2048;
      try {
         OssTravelerMessageBuilder.validateSmallDrivenLine(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundSmallDrivenLine() {
      int x = 2047;
      try {
         OssTravelerMessageBuilder.validateSmallDrivenLine(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerLargeDrivenLine() {
      int x = -32768;
      try {
         OssTravelerMessageBuilder.validateLargeDrivenLine(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundLargeDrivenLine() {
      int x = -32767;
      try {
         OssTravelerMessageBuilder.validateLargeDrivenLine(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkUpperLargeDrivenLine() {
      int x = 32768;
      try {
         OssTravelerMessageBuilder.validateLargeDrivenLine(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundLargeDrivenLine() {
      int x = 32767;
      try {
         OssTravelerMessageBuilder.validateLargeDrivenLine(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerAngle() {
      int x = -1;
      try {
         OssTravelerMessageBuilder.validateAngle(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundAngle() {
      int x = 0;
      try {
         OssTravelerMessageBuilder.validateAngle(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkUpperAngle() {
      int x = 28801;
      try {
         OssTravelerMessageBuilder.validateAngle(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundAngle() {
      int x = 28800;
      try {
         OssTravelerMessageBuilder.validateAngle(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerB12Scale() {
      int x = -2049;
      try {
         OssTravelerMessageBuilder.validateB12Scale(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundB12Scale() {
      int x = -2048;
      try {
         OssTravelerMessageBuilder.validateB12Scale(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkUpperB12Scale() {
      int x = 2048;
      try {
         OssTravelerMessageBuilder.validateB12Scale(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundB12Scale() {
      int x = 2047;
      try {
         OssTravelerMessageBuilder.validateB12Scale(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerVelocity() {
      int x = -1;
      try {
         OssTravelerMessageBuilder.validateVelocity(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundVelocity() {
      int x = 0;
      try {
         OssTravelerMessageBuilder.validateVelocity(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkUpperVelocity() {
      int x = 8192;
      try {
         OssTravelerMessageBuilder.validateVelocity(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundVelocity() {
      int x = 8191;
      try {
         OssTravelerMessageBuilder.validateVelocity(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerDeltaAngle() {
      int x = -151;
      try {
         OssTravelerMessageBuilder.validateDeltaAngle(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundDeltaAngle() {
      int x = -150;
      try {
         OssTravelerMessageBuilder.validateDeltaAngle(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkUpperDeltaAngle() {
      int x = 151;
      try {
         OssTravelerMessageBuilder.validateDeltaAngle(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundDeltaAngle() {
      int x = 150;
      try {
         OssTravelerMessageBuilder.validateDeltaAngle(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerCrownPoint() {
      int x = -129;
      try {
         OssTravelerMessageBuilder.validateCrownPoint(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundCrownPoint() {
      int x = -128;
      try {
         OssTravelerMessageBuilder.validateCrownPoint(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkUpperCrownPoint() {
      int x = 128;
      try {
         OssTravelerMessageBuilder.validateCrownPoint(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundCrownPoint() {
      int x = 127;
      try {
         OssTravelerMessageBuilder.validateCrownPoint(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerLaneAngle() {
      int x = -181;
      try {
         OssTravelerMessageBuilder.validateLaneAngle(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundLaneAngle() {
      int x = -180;
      try {
         OssTravelerMessageBuilder.validateLaneAngle(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkUpperLaneAngle() {
      int x = 181;
      try {
         OssTravelerMessageBuilder.validateLaneAngle(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundLaneAngle() {
      int x = 180;
      try {
         OssTravelerMessageBuilder.validateLaneAngle(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }
   
   @Ignore
   @Test
   public void checkTravelerMessageBuilder() {
      J2735TravelerInputData ti = new J2735TravelerInputData();
      TravelerInformation travelerinfo = new TravelerInformation();
      ti.tim.msgCnt = 10;
      ti.tim.urlB = "null";
      ti.tim.dataframes = null;
      OssTravelerMessageBuilder builder = new OssTravelerMessageBuilder();
      try {
         travelerinfo = builder.buildTravelerInformation(ti);
         assertEquals(10, travelerinfo.msgCnt);
         assertEquals("null", travelerinfo.urlB);
         assertNull(travelerinfo.dataFrames);
      }
      catch (Exception e)
      {
         fail("Unexpected Exception");
      }
   }
}
