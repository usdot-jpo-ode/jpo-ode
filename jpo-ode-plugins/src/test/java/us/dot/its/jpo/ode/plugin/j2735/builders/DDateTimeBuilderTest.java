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
package us.dot.its.jpo.ode.plugin.j2735.builders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735DDateTime;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

/**
 * Test class for the conversion from generic DDateTime to proper ASN.1
 * representation:
 * 
 * Test DYear - The DSRC year consists of integer values from zero to 4095
 * representing the year according to the Gregorian calendar date system. The
 * value of zero shall represent an unknown value. - ASN.1 Representation: DYear
 * ::= INTEGER (0..4095) -- units of years
 * 
 * Test DMonth - The DSRC month consists of integer values from one to 12,
 * representing the month within a year. The value of 0 shall represent an
 * unknown value. - ASN.1 Representation: DMonth ::= INTEGER (0..12) -- units of
 * months
 * 
 * Test DDay - The DSRC style day is a simple value consisting of integer values
 * from zero to 31. The value of zero shall represent an unknown value. - ASN.1
 * Representation: DDay ::= INTEGER (0..31) -- units of days
 * 
 * Test DHour - The DSRC hour consists of integer values from zero to 23
 * representing the hours within a day. The value of 31 shall represent an
 * unknown value. The range 24 to 30 is used in some transit applications to
 * represent schedule adherence. - ASN.1 Representation: DHour ::= INTEGER
 * (0..31) -- units of hours
 * 
 * Test DMinute - The DSRC style minute is a simple value consisting of integer
 * values from zero to 59 representing the minutes within an hour. The value of
 * 60 SHALL represent an unknown value. - ASN.1 Representation: DMinute ::=
 * INTEGER (0..60) -- units of minutes
 * 
 * Test DSecond - The DSRC second expressed in this data element consists of
 * integer values from zero to 60999, representing the milliseconds within a
 * minute. A leap second is represented by the value range 60000 to 60999. The
 * value of 65535 shall represent an unavailable value in the range of the
 * minute. The values from 61000 to 65534 are reserved. - ASN.1 Representation:
 * DSecond ::= INTEGER (0..65535) -- units of milliseconds
 * 
 * Test DOffset - The DSRC (time zone) offset consists of a signed integer
 * representing an hour and minute value set from -14:00 to +14:00, representing
 * all the world’s local time zones in units of minutes. The value of zero
 * (00:00) may also represent an unknown value. Note some time zones are do not
 * align to hourly boundaries - ASN.1 Representation: DOffset ::= INTEGER
 * (-840..840) -- units of minutes from UTC time
 * 
 * Test DDateTime - The DSRC style date is a compound value consisting of
 * finite-length sequences of integers (not characters) of the form: "yyyy, mm,
 * dd, hh, mm, ss (sss+)" - ASN.1 Representation: DDateTime ::= SEQUENCE { year
 * DYear OPTIONAL, month DMonth OPTIONAL, day DDay OPTIONAL, hour DHour
 * OPTIONAL, minute DMinute OPTIONAL, second DSecond OPTIONAL, offset DOffset
 * OPTIONAL -- time zone }
 *
 */
public class DDateTimeBuilderTest {

   /**
    * Test undefined flag values for each element of DDateTime return null values:
    * 
    * DYear undefined flag (0) returns (null) DMonth undefined flag (0) returns
    * (null) DDay undefined flag (0) returns (null) DHour undefined flag (31)
    * returns (null) DMinute undefined flag (60) returns (null) DSecond undefined
    * flag (65535) returns (null) DOffset value (0) may or may not be undefined -
    * should return (null) or (0)
    */

   @Test
   public void shouldReturnUndefinedDDateTimeYear() {

      JsonNode testDDateTime = null;

      try {
         testDDateTime = (JsonNode) XmlUtils
               .fromXmlS("<DDateTime>"
                     + "<year>0</year><month>0</month><day>0</day>"
                     + "<hour>31</hour><minute>60</minute><second>65535</second><offset>0</offset>"
                     + "</DDateTime>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      J2735DDateTime actualDDateTimeUndefined = DDateTimeBuilder.genericDDateTime(testDDateTime);

      assertNull("Undefined year value 0 should return null:", actualDDateTimeUndefined.getYear());
      assertNull("Undefined month value 0 should return null:", actualDDateTimeUndefined.getMonth());
      assertNull("Undefined day value 0 should return null:", actualDDateTimeUndefined.getDay());
      assertNull("Undefined hour value 31 should return null:", actualDDateTimeUndefined.getHour());
      assertNull("Undefined minute value 60 should return null:", actualDDateTimeUndefined.getMinute());
      assertNull("Undefined second value 65535 should return null:", actualDDateTimeUndefined.getSecond());
      assertTrue("Undefined offset value 0 should return null or 0:",
            (actualDDateTimeUndefined.getOffset() == null || actualDDateTimeUndefined.getOffset().intValue() == 0));

   }

   /**
    * Test minimum values for each element of DDateTime return minimum values:
    * 
    * DYear minimum (1) returns (1) DMonth minimum (1) returns (1) DDay minimum (1)
    * returns (1) DHour minimum (0) returns (0) DMinute minimum (0) returns (0)
    * DSecond minimum (0) returns (0) DOffset minimum (-840) returns (-840)
    */
   @Test
   public void shouldReturnMinimumDateTime() {

      Integer expectedYear = 1;
      Integer expectedMonth = 1;
      Integer expectedDay = 1;
      Integer expectedHour = 0;
      Integer expectedMinute = 0;
      Integer expectedSecond = 0;
      Integer expectedOffset = -840;

      JsonNode testDDateTime = null;

      try {
         testDDateTime = (JsonNode) XmlUtils
               .fromXmlS("<DDateTime>"
                     + "<year>1</year><month>1</month><day>1</day>"
                     + "<hour>0</hour><minute>0</minute><second>0</second><offset>-840</offset>"
                     + "</DDateTime>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      J2735DDateTime actualDDateTimeMinimum = DDateTimeBuilder.genericDDateTime(testDDateTime);

      assertEquals("Minimum year value 1 should return 1:", expectedYear, actualDDateTimeMinimum.getYear());
      assertEquals("Minimum month value 1 should return 1:", expectedMonth, actualDDateTimeMinimum.getMonth());
      assertEquals("Minimum day value 1 should return 1:", expectedDay, actualDDateTimeMinimum.getDay());
      assertEquals("Minimum hour value 0 should return 0:", expectedHour, actualDDateTimeMinimum.getHour());
      assertEquals("Minimum minute value 0 should return 0:", expectedMinute, actualDDateTimeMinimum.getMinute());
      assertEquals("Minimum second value 0 should return 0:", expectedSecond, actualDDateTimeMinimum.getSecond());
      assertEquals("Minimum offset value -840 should return -840:", expectedOffset, actualDDateTimeMinimum.getOffset());

   }

   /**
    * Test known values for each element of DDateTime return correct values:
    *
    * DYear known (2016) returns (2016) DMonth known (7) returns (7) DDay known
    * (16) returns (16) DHour known (11) returns (11) DMinute known (2) returns (2)
    * DSecond known (879) returns (879) DOffset known (45) returns (45)
    */
   @Test
   public void shouldReturnKnownDateTime() {
      Integer expectedYear = 2016;
      Integer expectedMonth = 7;
      Integer expectedDay = 16;
      Integer expectedHour = 11;
      Integer expectedMinute = 2;
      Integer expectedSecond = 879;
      Integer expectedOffset = 45;

      JsonNode testDDateTime = null;

      try {
         testDDateTime = (JsonNode) XmlUtils
               .fromXmlS("<DDateTime>"
                     + "<year>2016</year><month>7</month><day>16</day>"
                     + "<hour>11</hour><minute>2</minute><second>879</second><offset>45</offset>"
                     + "</DDateTime>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      J2735DDateTime actualDDateTimeKnown = DDateTimeBuilder.genericDDateTime(testDDateTime);

      assertEquals("Known year value 1 should return 1:", expectedYear, actualDDateTimeKnown.getYear());
      assertEquals("Known month value 1 should return 1:", expectedMonth, actualDDateTimeKnown.getMonth());
      assertEquals("Known day value 1 should return 1:", expectedDay, actualDDateTimeKnown.getDay());
      assertEquals("Known hour value 0 should return 0:", expectedHour, actualDDateTimeKnown.getHour());
      assertEquals("Known minute value 0 should return 0:", expectedMinute, actualDDateTimeKnown.getMinute());
      assertEquals("Known second value 0 should return 0:", expectedSecond, actualDDateTimeKnown.getSecond());
      assertEquals("Known offset value -840 should return -840:", expectedOffset, actualDDateTimeKnown.getOffset());

   }

   /**
    * Test maximum values for each element of DDateTime return correct values:
    *
    * DYear maximum (4095) returns (4095) DMonth maximum (12) returns (12) DDay
    * maximum (31) returns (31) DHour maximum (30) returns (30) DMinute maximum
    * (59) returns (59) DSecond maximum (65534) returns (65534) DOffset maximum
    * (840) returns (840)
    */
   @Test
   public void shouldReturnMaximumDateTime() {

      Integer expectedYear = 4095;
      Integer expectedMonth = 12;
      Integer expectedDay = 31;
      Integer expectedHour = 30;
      Integer expectedMinute = 59;
      Integer expectedSecond = 65534;
      Integer expectedOffset = 840;

      JsonNode testDDateTime = null;

      try {
         testDDateTime = (JsonNode) XmlUtils
               .fromXmlS("<DDateTime>"
                     + "<year>4095</year><month>12</month><day>31</day>"
                     + "<hour>30</hour><minute>59</minute><second>65534</second><offset>840</offset>"
                     + "</DDateTime>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      J2735DDateTime actualDDateTimeMaximum = DDateTimeBuilder.genericDDateTime(testDDateTime);

      assertEquals("Maximum year value 4095 should return 4095:", expectedYear, actualDDateTimeMaximum.getYear());
      assertEquals("Maximum month value 12 should return 12:", expectedMonth, actualDDateTimeMaximum.getMonth());
      assertEquals("Maximum day value 31 should return 31:", expectedDay, actualDDateTimeMaximum.getDay());
      assertEquals("Maximum hour value 30 should return 30:", expectedHour, actualDDateTimeMaximum.getHour());
      assertEquals("Maximum minute value 59 should return 59:", expectedMinute, actualDDateTimeMaximum.getMinute());
      assertEquals("Maximum second value 65534 should return 65534:", expectedSecond,
            actualDDateTimeMaximum.getSecond());
      assertEquals("Maximum offset value 840 should return 840", expectedOffset, actualDDateTimeMaximum.getOffset());

   }

   /**
    * Test that a year value (-1) below the lower bound (0) throws
    * IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionYearBelowLowerBound() {
      JsonNode testDDateTime = null;

      try {
         testDDateTime = (JsonNode) XmlUtils
               .fromXmlS("<DDateTime>"
                     + "<year>-1</year><month>0</month><day>0</day>"
                     + "<hour>0</hour><minute>0</minute><second>0</second><offset>0</offset>"
                     + "</DDateTime>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      try {
         DDateTimeBuilder.genericDDateTime(testDDateTime);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }

   }

   /**
    * Test that a year value (4096) above the upper bound (4095) throws
    * IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionYearAboveUpperBound() {
      JsonNode testDDateTime = null;

      try {
         testDDateTime = (JsonNode) XmlUtils
               .fromXmlS("<DDateTime>"
                     + "<year>4096</year><month>0</month><day>0</day>"
                     + "<hour>0</hour><minute>0</minute><second>0</second><offset>0</offset>"
                     + "</DDateTime>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      try {
         DDateTimeBuilder.genericDDateTime(testDDateTime);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   /**
    * Test that a month value (-1) below the lower bound (0) throws
    * IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionMonthBelowLowerBound() {
      JsonNode testDDateTime = null;

      try {
         testDDateTime = (JsonNode) XmlUtils
               .fromXmlS("<DDateTime>"
                     + "<year>0</year><month>-1</month><day>0</day>"
                     + "<hour>0</hour><minute>0</minute><second>0</second><offset>0</offset>"
                     + "</DDateTime>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      try {
         DDateTimeBuilder.genericDDateTime(testDDateTime);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   /**
    * Test that a month value (13) above the upper bound (12) throws
    * IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionMonthAboveUpperBound() {
      JsonNode testDDateTime = null;

      try {
         testDDateTime = (JsonNode) XmlUtils
               .fromXmlS("<DDateTime>"
                     + "<year>0</year><month>13</month><day>0</day>"
                     + "<hour>0</hour><minute>0</minute><second>0</second><offset>0</offset>"
                     + "</DDateTime>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      try {
         DDateTimeBuilder.genericDDateTime(testDDateTime);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   /**
    * Test that a day value (-1) below the lower bound (0) throws
    * IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionDayBelowLowerBound() {
      JsonNode testDDateTime = null;

      try {
         testDDateTime = (JsonNode) XmlUtils
               .fromXmlS("<DDateTime>"
                     + "<year>0</year><month>0</month><day>-1</day>"
                     + "<hour>0</hour><minute>0</minute><second>0</second><offset>0</offset>"
                     + "</DDateTime>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      try {
         DDateTimeBuilder.genericDDateTime(testDDateTime);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   /**
    * Test that a day value (32) above the upper bound (31) throws
    * IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionDayAboveUpperBound() {
      JsonNode testDDateTime = null;

      try {
         testDDateTime = (JsonNode) XmlUtils
               .fromXmlS("<DDateTime>"
                     + "<year>0</year><month>0</month><day>32</day>"
                     + "<hour>0</hour><minute>0</minute><second>0</second><offset>0</offset>"
                     + "</DDateTime>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      try {
         DDateTimeBuilder.genericDDateTime(testDDateTime);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   /**
    * Test that an hour value (-1) below the lower bound (0) throws
    * IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionHourBelowLowerBound() {
      JsonNode testDDateTime = null;

      try {
         testDDateTime = (JsonNode) XmlUtils
               .fromXmlS("<DDateTime>"
                     + "<year>0</year><month>0</month><day>0</day>"
                     + "<hour>-1</hour><minute>0</minute><second>0</second><offset>0</offset>"
                     + "</DDateTime>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      try {
         DDateTimeBuilder.genericDDateTime(testDDateTime);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   /**
    * Test that an hour value (32) above the upper bound (32) throws
    * IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionHourAboveUpperBound() {
      JsonNode testDDateTime = null;

      try {
         testDDateTime = (JsonNode) XmlUtils
               .fromXmlS("<DDateTime>"
                     + "<year>0</year><month>0</month><day>0</day>"
                     + "<hour>32</hour><minute>0</minute><second>0</second><offset>0</offset>"
                     + "</DDateTime>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      try {
         DDateTimeBuilder.genericDDateTime(testDDateTime);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   /**
    * Test that a minute value (-1) below the lower bound (0) throws
    * IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionMinuteBelowLowerBound() {
      JsonNode testDDateTime = null;

      try {
         testDDateTime = (JsonNode) XmlUtils
               .fromXmlS("<DDateTime>"
                     + "<year>0</year><month>0</month><day>0</day>"
                     + "<hour>0</hour><minute>-1</minute><second>65536</second>0<offset></offset>"
                     + "</DDateTime>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      try {
         DDateTimeBuilder.genericDDateTime(testDDateTime);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   /**
    * Test that a minute value (61) above the upper bound (60) throws
    * IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionMinuteAboveUpperBound() {
      JsonNode testDDateTime = null;

      try {
         testDDateTime = (JsonNode) XmlUtils
               .fromXmlS("<DDateTime>"
                     + "<year>0</year><month>0</month><day>0</day>"
                     + "<hour>0</hour><minute>61</minute><second>0</second><offset>0</offset>"
                     + "</DDateTime>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      try {
         DDateTimeBuilder.genericDDateTime(testDDateTime);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   /**
    * Test that a second value (-1) below the lower bound (0) throws
    * IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionSecondBelowLowerBound() {
      JsonNode testDDateTime = null;

      try {
         testDDateTime = (JsonNode) XmlUtils
               .fromXmlS("<DDateTime>"
                     + "<year>0</year><month>0</month><day>0</day>"
                     + "<hour>0</hour><minute>0</minute><second>-1</second><offset>0</offset>"
                     + "</DDateTime>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      try {
         DDateTimeBuilder.genericDDateTime(testDDateTime);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   /**
    * Test that a second value (65536) above the upper bound (65535) throws
    * IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionSecondAboveUpperBound() {
      JsonNode testDDateTime = null;

      try {
         testDDateTime = (JsonNode) XmlUtils
               .fromXmlS("<DDateTime>"
                     + "<year>0</year><month>0</month><day>0</day>"
                     + "<hour>0</hour><minute>0</minute><second>65536</second><offset></offset>"
                     + "</DDateTime>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      try {
         DDateTimeBuilder.genericDDateTime(testDDateTime);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   /**
    * Test that an offset value (-841) below the lower bound (-840) throws
    * IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionOffsetBelowLowerBound() {
      JsonNode testDDateTime = null;

      try {
         testDDateTime = (JsonNode) XmlUtils
               .fromXmlS("<DDateTime>"
                     + "<year>0</year><month>0</month><day>0</day>"
                     + "<hour>0</hour><minute>0</minute><second>0</second><offset>-841</offset>"
                     + "</DDateTime>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      try {
         DDateTimeBuilder.genericDDateTime(testDDateTime);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   /**
    * Test that an offset value (841) above the upper bound (840) throws
    * IllegalArgumentException
    */
   @Test
   public void shouldThrowExceptionOffsetAboveUpperBound() {

      JsonNode testDDateTime = null;

      try {
         testDDateTime = (JsonNode) XmlUtils
               .fromXmlS("<DDateTime>"
                     + "<year>-1</year><month>0</month><day>0</day>"
                     + "<hour>0</hour><minute>0</minute><second>0</second><offset>840</offset>"
                     + "</DDateTime>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }
      try {
         DDateTimeBuilder.genericDDateTime(testDDateTime);
         fail("Expected IllegalArgumentException");
      } catch (RuntimeException e) {
         assertEquals(IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void testConstructorIsPrivate()
         throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<DDateTimeBuilder> constructor = DDateTimeBuilder.class.getDeclaredConstructor();
      assertTrue(Modifier.isPrivate(constructor.getModifiers()));
      constructor.setAccessible(true);
      try {
         constructor.newInstance();
         fail("Expected IllegalAccessException.class");
      } catch (Exception e) {
         assertEquals(InvocationTargetException.class, e.getClass());
      }
   }

}
