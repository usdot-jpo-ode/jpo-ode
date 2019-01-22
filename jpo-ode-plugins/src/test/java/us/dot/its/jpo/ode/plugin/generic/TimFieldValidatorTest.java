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
package us.dot.its.jpo.ode.plugin.generic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;

import org.junit.Test;

import us.dot.its.jpo.ode.plugin.j2735.OdePosition3D;
import us.dot.its.jpo.ode.plugin.j2735.TimFieldValidator;

public class TimFieldValidatorTest {
   @Test
   public void testConstructorIsPrivate()
         throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<TimFieldValidator> constructor = TimFieldValidator.class.getDeclaredConstructor();
      assertTrue(Modifier.isPrivate(constructor.getModifiers()));
      constructor.setAccessible(true);
      try {
         constructor.newInstance();
         fail("Expected IllegalAccessException.class");
      } catch (Exception e) {
         assertEquals(InvocationTargetException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundMessageCount() {
      int msgcnt = -1;
      try {
         TimFieldValidator.validateMessageCount(msgcnt);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerMessageCount() {
      int msgcnt = 0;
      try {
         TimFieldValidator.validateMessageCount(msgcnt);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperBoundMessageCount() {
      int msgcnt = 128;
      try {
         TimFieldValidator.validateMessageCount(msgcnt);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperMessageCount() {
      int msgcnt = 127;
      try {
         TimFieldValidator.validateMessageCount(msgcnt);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkURLEmpty() {
      String str = "";
      try {
         TimFieldValidator.validateURL(str);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkURLLower() {
      String str = "a";
      try {
         TimFieldValidator.validateURL(str);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkURLUpper() {
      String str = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
      try {
         TimFieldValidator.validateURL(str);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkURLUpperBound() {
      String str = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
      try {
         TimFieldValidator.validateURL(str);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkURLShortEmpty() {
      String str = "";
      try {
         TimFieldValidator.validateURLShort(str);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkURLShortLower() {
      String str = "a";
      try {
         TimFieldValidator.validateURLShort(str);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkURLShortUpper() {
      String str = "aaaaaaaaaaaaaaa";
      try {
         TimFieldValidator.validateURLShort(str);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkURLShortUpperBound() {
      String str = "aaaaaaaaaaaaaaaa";
      try {
         TimFieldValidator.validateURLShort(str);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerFrame() {
      int x = 0;
      try {
         TimFieldValidator.validateFrameCount(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerFrameBound() {
      int x = 1;
      try {
         TimFieldValidator.validateFrameCount(x);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperFrame() {
      int x = 8;
      try {
         TimFieldValidator.validateFrameCount(x);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperFrameBound() {
      int x = 9;
      try {
         TimFieldValidator.validateFrameCount(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerStartTime() {
      long time = -1;
      try {
         TimFieldValidator.validateStartTime(time);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundStartTime() {
      long time = 0;
      try {
         TimFieldValidator.validateStartTime(time);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperStartTime() {
      long time = 527041;
      try {
         TimFieldValidator.validateStartTime(time);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundStartTime() {
      long time = 527040;
      try {
         TimFieldValidator.validateStartTime(time);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLowerYear() {
      int year = -1;
      try {
         TimFieldValidator.validateStartYear(year);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundYear() {
      int year = 0;
      try {
         TimFieldValidator.validateStartYear(year);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperYear() {
      int year = 4096;
      try {
         TimFieldValidator.validateStartYear(year);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundYear() {
      int year = 4095;
      try {
         TimFieldValidator.validateStartYear(year);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLowerMinutes() {
      int minute = -1;
      try {
         TimFieldValidator.validateMinutesDuration(minute);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundMinutes() {
      int minute = 0;
      try {
         TimFieldValidator.validateMinutesDuration(minute);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperMinutes() {
      int minute = 32001;
      try {
         TimFieldValidator.validateMinutesDuration(minute);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundMinutes() {
      int minute = 32000;
      try {
         TimFieldValidator.validateMinutesDuration(minute);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLowerBoundSSPIndex() {
      short index = -1;
      try {
         TimFieldValidator.validateHeaderIndex(index);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerSSPIndex() {
      short index = 0;
      try {
         TimFieldValidator.validateHeaderIndex(index);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperSSPIndex() {
      short index = 31;
      try {
         TimFieldValidator.validateHeaderIndex(index);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperBoundSSPIndex() {
      short index = 32;
      try {
         TimFieldValidator.validateHeaderIndex(index);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundLat() {
      OdePosition3D position = new OdePosition3D(BigDecimal.valueOf(-90.1), BigDecimal.valueOf(45.0),
            BigDecimal.valueOf(1000.0));
      try {
         TimFieldValidator.validatePosition(position);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundLat() {
      OdePosition3D position = new OdePosition3D(BigDecimal.valueOf(90.1), BigDecimal.valueOf(45.0),
            BigDecimal.valueOf(1000.0));
      try {
         TimFieldValidator.validatePosition(position);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerLat() {
      OdePosition3D position = new OdePosition3D(BigDecimal.valueOf(-90.0), BigDecimal.valueOf(45.0),
            BigDecimal.valueOf(1000.0));
      try {
         TimFieldValidator.validatePosition(position);
      } catch (RuntimeException e) {
         fail("Unexcpeted Exception");
      }
   }

   @Test
   public void checkUpperLat() {
      OdePosition3D position = new OdePosition3D(BigDecimal.valueOf(90.0), BigDecimal.valueOf(45.0),
            BigDecimal.valueOf(1000.0));
      try {
         TimFieldValidator.validatePosition(position);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLowerBoundLong() {
      OdePosition3D position = new OdePosition3D(BigDecimal.valueOf(45.0), BigDecimal.valueOf(-180.1),
            BigDecimal.valueOf(1000.0));
      try {
         TimFieldValidator.validatePosition(position);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundLong() {
      OdePosition3D position = new OdePosition3D(BigDecimal.valueOf(45), BigDecimal.valueOf(180.1),
            BigDecimal.valueOf(1000.0));
      try {
         TimFieldValidator.validatePosition(position);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerLong() {
      OdePosition3D position = new OdePosition3D(BigDecimal.valueOf(45), BigDecimal.valueOf(-180.0),
            BigDecimal.valueOf(1000.0));
      try {
         TimFieldValidator.validatePosition(position);
      } catch (RuntimeException e) {
         fail("Unexcpeted Exception");
      }
   }

   @Test
   public void checkUpperLong() {
      OdePosition3D position = new OdePosition3D(BigDecimal.valueOf(45), BigDecimal.valueOf(180.0),
            BigDecimal.valueOf(1000.0));
      try {
         TimFieldValidator.validatePosition(position);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLowerBoundElevation() {
      OdePosition3D position = new OdePosition3D(BigDecimal.valueOf(45), BigDecimal.valueOf(45.0),
            BigDecimal.valueOf(-409.6));
      try {
         TimFieldValidator.validatePosition(position);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundElevation() {
      OdePosition3D position = new OdePosition3D(BigDecimal.valueOf(45), BigDecimal.valueOf(45.0),
            BigDecimal.valueOf(6143.91));
      try {
         TimFieldValidator.validatePosition(position);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerElevation() {
      OdePosition3D position = new OdePosition3D(BigDecimal.valueOf(45), BigDecimal.valueOf(45.0),
            BigDecimal.valueOf(-409.5));
      try {
         TimFieldValidator.validatePosition(position);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperElevation() {
      OdePosition3D position = new OdePosition3D(BigDecimal.valueOf(45), BigDecimal.valueOf(45.0),
            BigDecimal.valueOf(6143.9));
      try {
         TimFieldValidator.validatePosition(position);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperHeading() {
      String head = "100110110101010101011001";
      try {
         TimFieldValidator.validateHeading(head);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkEmptyHeading() {
      String head = "";
      try {
         TimFieldValidator.validateHeading(head);
         fail("Expected IllegalArgument Exception");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerHeading() {
      String head = "01001010";
      try {
         TimFieldValidator.validateHeading(head);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkHeading() {
      String head = "1011011010101100";
      try {
         TimFieldValidator.validateHeading(head);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperBoundSign() {
      int sign = 8;
      try {
         TimFieldValidator.validateSign(sign);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundSign() {
      int sign = -1;
      try {
         TimFieldValidator.validateSign(sign);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerSign() {
      int sign = 0;
      try {
         TimFieldValidator.validateSign(sign);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperSign() {
      int sign = 7;
      try {
         TimFieldValidator.validateSign(sign);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperBoundITISCodes() {
      String code = "65536";
      try {
         TimFieldValidator.validateITISCodes(code);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundITISCodes() {
      String code = "-1";
      try {
         TimFieldValidator.validateITISCodes(code);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerITISCodes() {
      String code = "0";
      try {
         TimFieldValidator.validateITISCodes(code);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperITISCodes() {
      String code = "65535";
      try {
         TimFieldValidator.validateITISCodes(code);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkEmptyITISCodesString() {
      String code = "";
      try {
         TimFieldValidator.validateITISCodes(code);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerITISCodesString() {
      String code = "a";
      try {
         TimFieldValidator.validateITISCodes(code);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperITISCodesString() {
      String code = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
      try {
         TimFieldValidator.validateITISCodes(code);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperBoundITISCodesString() {
      String code = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
      try {
         TimFieldValidator.validateITISCodes(code);
         fail("Unexpected Exception");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkEmptyString() {
      String str = "";
      try {
         TimFieldValidator.validateString(str);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkNonEmptyString() {
      String str = "a";
      try {
         TimFieldValidator.validateString(str);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkEmptyGeoName() {
      String str = "";
      try {
         TimFieldValidator.validateGeoName(str);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerGeoName() {
      String str = "a";
      try {
         TimFieldValidator.validateGeoName(str);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperGeoName() {
      String str = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
      try {
         TimFieldValidator.validateGeoName(str);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperBoundGeoName() {
      String str = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
      try {
         TimFieldValidator.validateGeoName(str);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerRoadID() {
      int road = -1;
      try {
         TimFieldValidator.validateRoadID(road);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundRoadID() {
      int road = 0;
      try {
         TimFieldValidator.validateRoadID(road);
      } catch (RuntimeException e) {
         fail("Unexpected exception");
      }
   }

   @Test
   public void checkUpperRoadID() {
      int road = 65536;
      try {
         TimFieldValidator.validateRoadID(road);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundRoadID() {
      int road = 65535;
      try {
         TimFieldValidator.validateRoadID(road);
      } catch (RuntimeException e) {
         fail("Expected IllegalArgumentException");
      }
   }

   @Test
   public void checkLowerLaneWidth() {
      int lane = -1;
      try {
         TimFieldValidator.validateLaneWidth(BigDecimal.valueOf(lane));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundLaneWidth() {
      int lane = 0;
      try {
         TimFieldValidator.validateLaneWidth(BigDecimal.valueOf(lane));
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperLaneWidth() {
      double lane = 327.68;
      try {
         TimFieldValidator.validateLaneWidth(BigDecimal.valueOf(lane));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundLaneWidth() {
      double lane = 327.67;
      try {
         TimFieldValidator.validateLaneWidth(BigDecimal.valueOf(lane));
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLowerDirectionality() {
      long dir = -1;
      try {
         TimFieldValidator.validateDirectionality(dir);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundDirectionality() {
      long dir = 0;
      try {
         TimFieldValidator.validateDirectionality(dir);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperDirectionality() {
      long dir = 4;
      try {
         TimFieldValidator.validateDirectionality(dir);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundDirectionality() {
      long dir = 3;
      try {
         TimFieldValidator.validateDirectionality(dir);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLowerZoom() {
      int zoom = -1;
      try {
         TimFieldValidator.validateZoom(zoom);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundZoom() {
      int zoom = 0;
      try {
         TimFieldValidator.validateZoom(zoom);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperZoom() {
      int zoom = 16;
      try {
         TimFieldValidator.validateZoom(zoom);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundZoom() {
      int zoom = 15;
      try {
         TimFieldValidator.validateZoom(zoom);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLowerExtent() {
      int extent = -1;
      try {
         TimFieldValidator.validateExtent(extent);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundExtent() {
      int extent = 0;
      try {
         TimFieldValidator.validateExtent(extent);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperExtent() {
      int extent = 16;
      try {
         TimFieldValidator.validateExtent(extent);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundExtent() {
      int extent = 15;
      try {
         TimFieldValidator.validateExtent(extent);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLowerRadius() {
      int rad = -1;
      try {
         TimFieldValidator.validateRadius(rad);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundRadius() {
      int rad = 0;
      try {
         TimFieldValidator.validateRadius(rad);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperRadius() {
      int rad = 4096;
      try {
         TimFieldValidator.validateRadius(rad);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundRadius() {
      int rad = 4095;
      try {
         TimFieldValidator.validateRadius(rad);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLowerUnits() {
      int unit = -1;
      try {
         TimFieldValidator.validateUnits(unit);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundUnits() {
      int unit = 0;
      try {
         TimFieldValidator.validateUnits(unit);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkUpperUnits() {
      int unit = 8;
      try {
         TimFieldValidator.validateUnits(unit);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundUnits() {
      int unit = 7;
      try {
         TimFieldValidator.validateUnits(unit);
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLowerX16Offset() {
      double x = -0.0032769;
      try {
         TimFieldValidator.validatex16Offset(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundX16Offset() {
      double x = -0.0032768;
      try {
         TimFieldValidator.validatex16Offset(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperX16Offset() {
      double x = 0.0032768;
      try {
         TimFieldValidator.validatex16Offset(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundX16Offset() {
      double x = 0.0032767;
      try {
         TimFieldValidator.validatex16Offset(BigDecimal.valueOf(x));
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLowerY16Offset() {
      double y = -0.0032769;
      try {
         TimFieldValidator.validatey16Offset(BigDecimal.valueOf(y));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundY16Offset() {
      double y = -0.0032768;
      try {
         TimFieldValidator.validatey16Offset(BigDecimal.valueOf(y));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperY16Offset() {
      double y = 0.0032768;
      try {
         TimFieldValidator.validatey16Offset(BigDecimal.valueOf(y));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundY16Offset() {
      double y = 0.0032767;
      try {
         TimFieldValidator.validatey16Offset(BigDecimal.valueOf(y));
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLowerZ16Offset() {
      double z = -0.0032769;
      try {
         TimFieldValidator.validatez16Offset(BigDecimal.valueOf(z));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundZ16Offset() {
      double z = -0.0032768;
      try {
         TimFieldValidator.validatez16Offset(BigDecimal.valueOf(z));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperZ16Offset() {
      double z = 0.0032768;
      try {
         TimFieldValidator.validatez16Offset(BigDecimal.valueOf(z));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundZ16Offset() {
      double z = 0.0032767;
      try {
         TimFieldValidator.validatez16Offset(BigDecimal.valueOf(z));
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkNodeEnumeration() {
      String str = "reseved";
      try {
         TimFieldValidator.validateNodeAttribute(str);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkBoundNodeEnumeration() {
      String str = "reserved";
      try {
         TimFieldValidator.validateNodeAttribute(str);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkSegmentEnumeration() {
      String str = "freParking";
      try {
         TimFieldValidator.validateSegmentAttribute(str);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkBoundSegmentEnumeration() {
      String str = "freeParking";
      try {
         TimFieldValidator.validateSegmentAttribute(str);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkSpeedLimitEnumeration() {
      String str = "maxSpedInSchoolZoneWhenChildrenArePresent";
      try {
         TimFieldValidator.validateSpeedLimitType(str);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkBoundSpeedLimitEnumeration() {
      String str = "maxSpeedInSchoolZoneWhenChildrenArePresent";
      try {
         TimFieldValidator.validateSpeedLimitType(str);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerB10Offset() {
      double x = -5.13;
      try {
         TimFieldValidator.validateB10Offset(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundB10Offset() {
      double x = 5.11;
      try {
         TimFieldValidator.validateB10Offset(BigDecimal.valueOf(x));
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkUpperB10Offset() {
      double x = 5.12;
      try {
         TimFieldValidator.validateB10Offset(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundB10Offset() {
      double x = 5.11;
      try {
         TimFieldValidator.validateB10Offset(BigDecimal.valueOf(x));
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerB11Offset() {
      double x = -10.25;
      try {
         TimFieldValidator.validateB11Offset(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundB11Offset() {
      double x = -10.24;
      try {
         TimFieldValidator.validateB11Offset(BigDecimal.valueOf(x));
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkUpperB11Offset() {
      double x = 10.24;
      try {
         TimFieldValidator.validateB11Offset(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundB11Offset() {
      double x = 10.23;
      try {
         TimFieldValidator.validateB11Offset(BigDecimal.valueOf(x));
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerB12Offset() {
      double x = -20.49;
      try {
         TimFieldValidator.validateB12Offset(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundB12Offset() {
      double x = -20.48;
      try {
         TimFieldValidator.validateB12Offset(BigDecimal.valueOf(x));
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkUpperB12Offset() {
      double x = 20.48;
      try {
         TimFieldValidator.validateB12Offset(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundB12Offset() {
      double x = 20.47;
      try {
         TimFieldValidator.validateB12Offset(BigDecimal.valueOf(x));
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerB13Offset() {
      double x = -40.97;
      try {
         TimFieldValidator.validateB13Offset(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundB13Offset() {
      double x = -40.96;
      try {
         TimFieldValidator.validateB13Offset(BigDecimal.valueOf(x));
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkUpperB13Offset() {
      double x = 40.96;
      try {
         TimFieldValidator.validateB13Offset(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundB13Offset() {
      double x = 40.95;
      try {
         TimFieldValidator.validateB13Offset(BigDecimal.valueOf(x));
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerB14Offset() {
      double x = -81.93;
      try {
         TimFieldValidator.validateB14Offset(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundB14Offset() {
      double x = -81.92;
      try {
         TimFieldValidator.validateB14Offset(BigDecimal.valueOf(x));
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkUpperB14Offset() {
      double x = 81.92;
      try {
         TimFieldValidator.validateB14Offset(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundB14Offset() {
      double x = 81.91;
      try {
         TimFieldValidator.validateB14Offset(BigDecimal.valueOf(x));
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerB16Offset() {
      double x = -327.69;
      try {
         TimFieldValidator.validateB16Offset(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundB16Offset() {
      double x = -327.68;
      try {
         TimFieldValidator.validateB16Offset(BigDecimal.valueOf(x));
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkUpperB16Offset() {
      double x = 327.68;
      try {
         TimFieldValidator.validateB16Offset(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundB16Offset() {
      double x = 327.67;
      try {
         TimFieldValidator.validateB16Offset(BigDecimal.valueOf(x));
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerLL12Offset() {
      double x = -0.0002049;
      try {
         TimFieldValidator.validateLL12Offset(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundLL12Offset() {
      double x = -0.0002048;
      try {
         TimFieldValidator.validateLL12Offset(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperLL12Offset() {
      double x = 0.0002048;
      try {
         TimFieldValidator.validateLL12Offset(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundLL12Offset() {
      double x = 0.0002047;
      try {
         TimFieldValidator.validateLL12Offset(BigDecimal.valueOf(x));
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerLL14Offset() {
      double x = -0.0008193;
      try {
         TimFieldValidator.validateLL14Offset(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundLL14Offset() {
      double x = -0.0008192;
      try {
         TimFieldValidator.validateLL14Offset(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperLL14Offset() {
      double x = 0.0008192;
      try {
         TimFieldValidator.validateLL14Offset(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundLL14Offset() {
      double x = 0.0008191;
      try {
         TimFieldValidator.validateLL14Offset(BigDecimal.valueOf(x));
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerLL16Offset() {
      double x = -327.69;
      try {
         TimFieldValidator.validateLL16Offset(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundLL16Offset() {
      double x = -0.0032768;
      try {
         TimFieldValidator.validateLL16Offset(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperLL16Offset() {
      double x = 0.0032768;
      try {
         TimFieldValidator.validateLL16Offset(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundLL16Offset() {
      double x = 0.0032767;
      try {
         TimFieldValidator.validateLL16Offset(BigDecimal.valueOf(x));
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerLL18Offset() {
      double x = -0.0131073;
      try {
         TimFieldValidator.validateLL18Offset(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundLL18Offset() {
      double x = -0.0131072;
      try {
         TimFieldValidator.validateLL18Offset(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperLL18Offset() {
      double x = 0.0131072;
      try {
         TimFieldValidator.validateLL18Offset(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundLL18Offset() {
      double x = 0.0131071;
      try {
         TimFieldValidator.validateLL18Offset(BigDecimal.valueOf(x));
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerLL22Offset() {
      double x = -0.2097153;
      try {
         TimFieldValidator.validateLL22Offset(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundLL22Offset() {
      double x = -0.2097152;
      try {
         TimFieldValidator.validateLL22Offset(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperLL22Offset() {
      double x = 0.2097152;
      try {
         TimFieldValidator.validateLL22Offset(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundLL22Offset() {
      double x = 0.2097151;
      try {
         TimFieldValidator.validateLL22Offset(BigDecimal.valueOf(x));
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerLL24Offset() {
      double x = -0.8388609;
      try {
         TimFieldValidator.validateLL24Offset(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundLL24Offset() {
      double x = -0.8388608;
      try {
         TimFieldValidator.validateLL24Offset(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperLL24Offset() {
      double x = 0.8388608;
      try {
         TimFieldValidator.validateLL24Offset(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundLL24Offset() {
      double x = 0.8388607;
      try {
         TimFieldValidator.validateLL24Offset(BigDecimal.valueOf(x));
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerLaneID() {
      int x = -1;
      try {
         TimFieldValidator.validateLaneID(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundLaneID() {
      int x = 0;
      try {
         TimFieldValidator.validateLaneID(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkUpperLaneID() {
      int x = 256;
      try {
         TimFieldValidator.validateLaneID(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundLaneID() {
      int x = 255;
      try {
         TimFieldValidator.validateLaneID(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerSmallDrivenLine() {
      int x = -2048;
      try {
         TimFieldValidator.validateSmallDrivenLine(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundSmallDrivenLine() {
      int x = -2047;
      try {
         TimFieldValidator.validateSmallDrivenLine(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkUpperSmallDrivenLine() {
      int x = 2048;
      try {
         TimFieldValidator.validateSmallDrivenLine(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundSmallDrivenLine() {
      int x = 2047;
      try {
         TimFieldValidator.validateSmallDrivenLine(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerLargeDrivenLine() {
      int x = -32768;
      try {
         TimFieldValidator.validateLargeDrivenLine(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundLargeDrivenLine() {
      int x = -32767;
      try {
         TimFieldValidator.validateLargeDrivenLine(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkUpperLargeDrivenLine() {
      int x = 32768;
      try {
         TimFieldValidator.validateLargeDrivenLine(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundLargeDrivenLine() {
      int x = 32767;
      try {
         TimFieldValidator.validateLargeDrivenLine(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerAngle() {
      double x = -1;
      try {
         TimFieldValidator.validateAngle(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundAngle() {
      double x = 0;
      try {
         TimFieldValidator.validateAngle(BigDecimal.valueOf(x));
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkUpperAngle() {
      double x = 360;
      try {
         TimFieldValidator.validateAngle(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundAngle() {
      double x = 359.9875;
      try {
         TimFieldValidator.validateAngle(BigDecimal.valueOf(x));
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerB12Scale() {
      double x = -0.1;
      try {
         TimFieldValidator.validateB12Scale(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundB12Scale() {
      double x = 0;
      try {
         TimFieldValidator.validateB12Scale(BigDecimal.valueOf(x));
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkUpperB12Scale() {
      double x = 202.36;
      try {
         TimFieldValidator.validateB12Scale(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundB12Scale() {
      double x = 202.35;
      try {
         TimFieldValidator.validateB12Scale(BigDecimal.valueOf(x));
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerVelocity() {
      double x = -0.02;
      try {
         TimFieldValidator.validateVelocity(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundVelocity() {
      double x = 0;
      try {
         TimFieldValidator.validateVelocity(BigDecimal.valueOf(x));
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkUpperVelocity() {
      double x = 163.84;
      try {
         TimFieldValidator.validateVelocity(BigDecimal.valueOf(x));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundVelocity() {
      double x = 163.82;
      try {
         TimFieldValidator.validateVelocity(BigDecimal.valueOf(x));
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerDeltaAngle() {
      int x = -151;
      try {
         TimFieldValidator.validateDeltaAngle(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundDeltaAngle() {
      int x = -150;
      try {
         TimFieldValidator.validateDeltaAngle(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkUpperDeltaAngle() {
      int x = 151;
      try {
         TimFieldValidator.validateDeltaAngle(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundDeltaAngle() {
      int x = 150;
      try {
         TimFieldValidator.validateDeltaAngle(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerCrownPoint() {
      int x = -129;
      try {
         TimFieldValidator.validateCrownPoint(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundCrownPoint() {
      int x = -128;
      try {
         TimFieldValidator.validateCrownPoint(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkUpperCrownPoint() {
      int x = 128;
      try {
         TimFieldValidator.validateCrownPoint(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundCrownPoint() {
      int x = 127;
      try {
         TimFieldValidator.validateCrownPoint(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLowerLaneAngle() {
      int x = -181;
      try {
         TimFieldValidator.validateLaneAngle(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLowerBoundLaneAngle() {
      int x = -180;
      try {
         TimFieldValidator.validateLaneAngle(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkUpperLaneAngle() {
      int x = 181;
      try {
         TimFieldValidator.validateLaneAngle(x);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkUpperBoundLaneAngle() {
      int x = 180;
      try {
         TimFieldValidator.validateLaneAngle(x);
      } catch (RuntimeException e) {
         fail("Unexcpected Exception");
      }
   }

   @Test
   public void checkLatitudeLower() {
      double lat = -90.0;
      try {
         TimFieldValidator.validateLatitude(BigDecimal.valueOf(lat));
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLatitudeLowerBound() {
      double lat = -91.0;
      try {
         TimFieldValidator.validateLatitude(BigDecimal.valueOf(lat));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLatitudeUpper() {
      double lat = 90.0;
      try {
         TimFieldValidator.validateLatitude(BigDecimal.valueOf(lat));
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLatitudeUpperBound() {
      double lat = 91.0;
      try {
         TimFieldValidator.validateLatitude(BigDecimal.valueOf(lat));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLongitudeLower() {
      double lonng = -180.0;
      try {
         TimFieldValidator.validateLongitude(BigDecimal.valueOf(lonng));
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLongitudeLowerBound() {
      double lonng = -181.0;
      try {
         TimFieldValidator.validateLongitude(BigDecimal.valueOf(lonng));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void checkLongitudeUpper() {
      double lonng = 180.0;
      try {
         TimFieldValidator.validateLongitude(BigDecimal.valueOf(lonng));
      } catch (RuntimeException e) {
         fail("Unexpected Exception");
      }
   }

   @Test
   public void checkLongitudeUpperBound() {
      double lonng = 181.0;
      try {
         TimFieldValidator.validateLongitude(BigDecimal.valueOf(lonng));
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }
}
