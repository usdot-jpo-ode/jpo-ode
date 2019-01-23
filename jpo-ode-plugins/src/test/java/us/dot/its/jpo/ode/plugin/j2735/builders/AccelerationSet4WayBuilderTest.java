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

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735AccelerationSet4Way;
import us.dot.its.jpo.ode.util.XmlUtils;
import us.dot.its.jpo.ode.util.XmlUtils.XmlUtilsException;

public class AccelerationSet4WayBuilderTest {

   
   @Test
   public void testConversionNull() {

      JsonNode jsonAccelSet = null;
      try {
         jsonAccelSet = (JsonNode) XmlUtils.fromXmlS(
               "<accelSet><long>2001</long><lat>2001</lat><vert>-127</vert><yaw>0</yaw></accelSet>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      J2735AccelerationSet4Way actualAccelSet = AccelerationSet4WayBuilder.genericAccelerationSet4Way(jsonAccelSet);
      BigDecimal lat = null;
      BigDecimal lon = null;
      BigDecimal vert = null;

      
      assertEquals(lat, actualAccelSet.getAccelLat());
      assertEquals(lon, actualAccelSet.getAccelLong());
      assertEquals(vert, actualAccelSet.getAccelVert());

   }
   
   @Test
   public void testConversionWithinRange() {

      JsonNode jsonAccelSet = null;
      try {
         jsonAccelSet = (JsonNode) XmlUtils.fromXmlS(
               "<accelSet><long>42</long><lat>42</lat><vert>42</vert><yaw>42</yaw></accelSet>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      J2735AccelerationSet4Way actualAccelSet = AccelerationSet4WayBuilder.genericAccelerationSet4Way(jsonAccelSet);
      BigDecimal lat = BigDecimal.valueOf((long) 42,2); 
      BigDecimal lon = BigDecimal.valueOf((long) 42,2); 
      BigDecimal vert = BigDecimal.valueOf((long) 84,2); 
      BigDecimal yaw = BigDecimal.valueOf((long) 42,2); 
      
      assertEquals(lat, actualAccelSet.getAccelLat());
      assertEquals(lon, actualAccelSet.getAccelLong());
      assertEquals(vert, actualAccelSet.getAccelVert());
      assertEquals(yaw, actualAccelSet.getAccelYaw());

   }
   
   @Test
   public void testConversionLower() {

      JsonNode jsonAccelSet = null;
      try {
         jsonAccelSet = (JsonNode) XmlUtils.fromXmlS(
               "<accelSet><long>-2001</long><lat>-2001</lat><vert>-128</vert><yaw>42</yaw></accelSet>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      J2735AccelerationSet4Way actualAccelSet = AccelerationSet4WayBuilder.genericAccelerationSet4Way(jsonAccelSet);
      BigDecimal lat = BigDecimal.valueOf((long) -2000,2); 
      BigDecimal lon = BigDecimal.valueOf((long) -2000,2); 
      BigDecimal vert = BigDecimal.valueOf(-2.52); 
     
      
      assertEquals(lat, actualAccelSet.getAccelLat());
      assertEquals(lon, actualAccelSet.getAccelLong());
      assertEquals(vert, actualAccelSet.getAccelVert());
      //assertEquals(yaw, actualAccelSet.getAccelYaw());

   }
   
   @Test
   public void testConversionUpper() {

      JsonNode jsonAccelSet = null;
      try {
         jsonAccelSet = (JsonNode) XmlUtils.fromXmlS(
               "<accelSet><long>2002</long><lat>2002</lat><vert>129</vert><yaw>42</yaw></accelSet>", JsonNode.class);
      } catch (XmlUtilsException e) {
         fail("XML parsing error:" + e);
      }

      J2735AccelerationSet4Way actualAccelSet = AccelerationSet4WayBuilder.genericAccelerationSet4Way(jsonAccelSet);
      BigDecimal lat = BigDecimal.valueOf((long) 2000,2); 
      BigDecimal lon = BigDecimal.valueOf((long) 2000,2); 
      BigDecimal vert = BigDecimal.valueOf(2.54); 
      
      
      assertEquals(lat, actualAccelSet.getAccelLat());
      assertEquals(lon, actualAccelSet.getAccelLong());
      assertEquals(vert, actualAccelSet.getAccelVert());
    

   }

   @Test
   public void shouldThrowExceptionAboveUpperYawBound() throws JsonProcessingException, IOException {

	      JsonNode jsonAccelSet = null;
	      try {
	         jsonAccelSet = (JsonNode) XmlUtils.fromXmlS(
	               "<accelSet><long>2002</long><lat>2002</lat><vert>129</vert><yaw>32768</yaw></accelSet>", JsonNode.class);
	      } catch (XmlUtilsException e) {
	         fail("XML parsing error:" + e);
	      }


      try {
        AccelerationSet4WayBuilder.genericAccelerationSet4Way(jsonAccelSet);
        fail("Expected IllegalArgumentException");
      } catch (Exception e) {
         assertTrue(e.getClass().equals(IllegalArgumentException.class));
      }
   }
   
   @Test
   public void shouldThrowExceptionAboveLowerYawBound() throws JsonProcessingException, IOException {

	      JsonNode jsonAccelSet = null;
	      try {
	         jsonAccelSet = (JsonNode) XmlUtils.fromXmlS(
	               "<accelSet><long>2002</long><lat>2002</lat><vert>129</vert><yaw>-32768</yaw></accelSet>", JsonNode.class);
	      } catch (XmlUtilsException e) {
	         fail("XML parsing error:" + e);
	      }


      try {
        AccelerationSet4WayBuilder.genericAccelerationSet4Way(jsonAccelSet);
        fail("Expected IllegalArgumentException");
      } catch (Exception e) {
         assertTrue(e.getClass().equals(IllegalArgumentException.class));
      }
   }
   
}
