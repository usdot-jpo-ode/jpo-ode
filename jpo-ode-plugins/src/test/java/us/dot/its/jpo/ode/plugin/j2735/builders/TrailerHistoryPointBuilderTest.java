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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735TrailerHistoryPoint;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

/**
 * -- Summary -- JUnit test class for TrailerHistoryPointBuilder
 * 
 * Verifies correct conversion from JsonNode to
 * compliant-J2735TrailerHistoryPoint
 * 
 * This is a trivial test that verifies a mock J2735TrailerHistoryPoint object
 * with known contents can be successfully created. All elements of this class
 * are checked by other tests.
 * 
 * -- Documentation -- Data Frame: DF_TrailerHistoryPoint Use: The
 * DF_TrailerHistoryPoint data frame contains a single position point for a
 * trailer, expressed relative to the vehicle’s BSM positional estimate at the
 * same point in time. ASN.1 Representation: TrailerHistoryPoint ::= SEQUENCE {
 * pivotAngle Angle, -- angle with respect to the lead unit timeOffset
 * TimeOffset, -- offset backwards in time -- Position relative to the hauling
 * Vehicle positionOffset Node-XY-24b, elevationOffset VertOffset-B07 OPTIONAL,
 * heading CoarseHeading OPTIONAL, -- overall heading ... }
 */
public class TrailerHistoryPointBuilderTest {

   @Test
   public void shouldCreateMockTrailerHistoryPoint() {

      int pivotAngle = 0;
      int timeOffset = 1;
      int xint = 0;
      int yint = 0;
      String positionOffset = "<positionOffset><x>" + xint + "</x><y>" + yint + "</y></positionOffset>";
      int elevationOffset = 0;
      int heading = 0;

      JsonNode testthp = null;
      try {
         testthp = (JsonNode) XmlUtils.fromXmlS("<thp><pivotAngle>" + pivotAngle + "</pivotAngle><timeOffset>"
               + timeOffset + "</timeOffset>" + positionOffset + "<elevationOffset>" + elevationOffset
               + "</elevationOffset><heading>" + heading + "</heading></thp>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      try {
         J2735TrailerHistoryPoint actualthp = TrailerHistoryPointBuilder.genericTrailerHistoryPoint(testthp);
         assertEquals("Incorrect angle", BigDecimal.ZERO.setScale(4), actualthp.getPivotAngle());

         assertEquals("Incorrect position offset x", BigDecimal.ZERO.setScale(2), actualthp.getPositionOffset().getX());
         assertEquals("Incorrect position offset y", BigDecimal.ZERO.setScale(2), actualthp.getPositionOffset().getY());
         assertEquals("Incorrect elevation offset", BigDecimal.ZERO.setScale(1), actualthp.getElevationOffset());
         assertEquals("Incorrect heading", BigDecimal.ZERO.setScale(4), actualthp.getHeading());
         assertEquals("Incorrect time offset", BigDecimal.valueOf(0.01), actualthp.getTimeOffset());
      } catch (Exception e) {
         fail("Unexpected exception: " + e.getClass());
      }
   }

   @Test
   public void testConstructorIsPrivate()
         throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<TrailerHistoryPointBuilder> constructor = TrailerHistoryPointBuilder.class.getDeclaredConstructor();
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
