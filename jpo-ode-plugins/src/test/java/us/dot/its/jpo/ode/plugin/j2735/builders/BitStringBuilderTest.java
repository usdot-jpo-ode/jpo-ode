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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735BitString;
import us.dot.its.jpo.ode.plugin.j2735.J2735GNSSstatusNames;
import us.dot.its.jpo.ode.util.JsonUtils;

public class BitStringBuilderTest {

   /**
    * Test input bit string "00000000" returns "false" for all flag values
    */
   @Test
   public void shouldReturnAllBitsFalse() {

      JsonNode testBitString = JsonUtils.newNode().put("status", "00000000");

      J2735BitString actualBitString = BitStringBuilder.genericBitString(testBitString.get("status"),
            J2735GNSSstatusNames.values());

      for (Map.Entry<String, Boolean> curVal : actualBitString.entrySet()) {
         assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
      }

   }

   /**
    * Test input bit string "11111111" returns "true" for all flag values
    */
   @Test
   public void shouldReturnAllBitsTrue() {

      JsonNode testBitString = JsonUtils.newNode().put("status", "11111111");

      J2735BitString actualBitString = BitStringBuilder.genericBitString(testBitString.get("status"),
            J2735GNSSstatusNames.values());

      for (Map.Entry<String, Boolean> curVal : actualBitString.entrySet()) {
         assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
      }
   }

   /**
    * Test input bit string "10000000" returns "true" for "unavailable" only
    */
   @Test
   public void shouldReturnGNSSstatusUnavailable() {

      JsonNode testBitString = JsonUtils.newNode().put("status", "10000000");
      String elementTested = "unavailable";

      J2735BitString actualBitString = BitStringBuilder.genericBitString(testBitString.get("status"),
            J2735GNSSstatusNames.values());

      for (Map.Entry<String, Boolean> curVal : actualBitString.entrySet()) {
         if (curVal.getKey() == elementTested) {
            assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
         } else {
            assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
         }
      }
   }

   /**
    * Test input bit string "00000010" returns "true" for "isHealthy" only
    */
   @Test
   public void shouldReturnGNSSstatusIsHealthy() {

      JsonNode testBitString = JsonUtils.newNode().put("status", "01000000");
      String elementTested = "isHealthy";

      J2735BitString actualBitString = BitStringBuilder.genericBitString(testBitString.get("status"),
            J2735GNSSstatusNames.values());

      for (Map.Entry<String, Boolean> curVal : actualBitString.entrySet()) {
         if (curVal.getKey() == elementTested) {
            assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
         } else {
            assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
         }
      }
   }

   /**
    * Test input bit string "01000000" returns "true" for "localCorrectionsPresent"
    * only
    */
   @Test
   public void shouldReturnGNSSstatusLocalCorrectionsPresent() {

      JsonNode testBitString = JsonUtils.newNode().put("status", "00000010");
      String elementTested = "localCorrectionsPresent";

      J2735BitString actualBitString = BitStringBuilder.genericBitString(testBitString.get("status"),
            J2735GNSSstatusNames.values());

      for (Map.Entry<String, Boolean> curVal : actualBitString.entrySet()) {
         if (curVal.getKey() == elementTested) {
            assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
         } else {
            assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
         }
      }
   }

   /**
    * Test input bit string "10000000" returns "true" for
    * "networkCorrectionsPresent" only
    */
   @Test
   public void shouldReturnGNSSstatusNetworkCorrectionsPresent() {

      JsonNode testBitString = JsonUtils.newNode().put("status", "00000001");
      String elementTested = "networkCorrectionsPresent";

      J2735BitString actualBitString = BitStringBuilder.genericBitString(testBitString.get("status"),
            J2735GNSSstatusNames.values());

      for (Map.Entry<String, Boolean> curVal : actualBitString.entrySet()) {
         if (curVal.getKey() == elementTested) {
            assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
         } else {
            assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
         }
      }
   }

   /**
    * Test input bit string "01000010" returns "true" for "isHealthy" and
    * "localCorrectionsPresent" only
    */
   @Test
   public void shouldReturnTwoGNSSstatus() {

      JsonNode testBitString = JsonUtils.newNode().put("status", "01000010");
      String elementTested1 = "isHealthy";
      String elementTested2 = "localCorrectionsPresent";

      J2735BitString actualBitString = BitStringBuilder.genericBitString(testBitString.get("status"),
            J2735GNSSstatusNames.values());

      for (Map.Entry<String, Boolean> curVal : actualBitString.entrySet()) {
         if (curVal.getKey() == elementTested1 || curVal.getKey() == elementTested2) {
            assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
         } else {
            assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
         }
      }
   }

   @Test
   public void testConstructorIsPrivate()
         throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      Constructor<BitStringBuilder> constructor = BitStringBuilder.class.getDeclaredConstructor();
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
