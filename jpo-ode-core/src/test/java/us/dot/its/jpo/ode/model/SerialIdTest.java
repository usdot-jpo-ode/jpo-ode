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
package us.dot.its.jpo.ode.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import us.dot.its.jpo.ode.util.JsonUtils;

public class SerialIdTest {

   @Test
   public void testNoArgConstructor() {
      SerialId testSerialId = new SerialId();

      assertNotNull(testSerialId.getStreamId());
   }

   @Test
   public void testFourArgConstructorNullStreamId() {
      // streamId = null
      // bundleSize = 3
      // bundleId = 6L
      // recordId = 12
      SerialId testSerialId = new SerialId(null, 3, 6L, 12);

      assertNotNull(testSerialId.getStreamId());
      assertTrue(testSerialId.getStreamId().endsWith("_null"));
      assertEquals(3, testSerialId.getBundleSize()); // bundle size = bundle size
      assertEquals(10L, testSerialId.getBundleId()); // bundle id = bundle id + (record id / bundle size) = 6L + ( 12 /
                                                     // 3) = 10L
      assertEquals(0, testSerialId.getRecordId()); // record id = record id % bundle size = 12 % 3 = 0
      assertEquals(30L, testSerialId.getSerialNumber()); // serial number = (bundle id * bundle size) + record id = (6L
                                                         // * 3) + 12 = 30L
   }

   @Test
   public void testFourArgConstructorWithStreamId() {
      SerialId testSerialId = new SerialId("bob", 3, 6L, 12);

      assertEquals("bob", testSerialId.getStreamId());
      assertEquals(3, testSerialId.getBundleSize());
      assertEquals(10L, testSerialId.getBundleId());
      assertEquals(0, testSerialId.getRecordId());
      assertEquals(30L, testSerialId.getSerialNumber());

      assertEquals("bob_3.10.0#30", testSerialId.toString());
   }

   @Test
   public void testSingleArgConstructor() throws Exception {
      SerialId testSerialId = new SerialId("bob_3.10.0#30");

      assertEquals("bob", testSerialId.getStreamId());
      assertEquals(3, testSerialId.getBundleSize());
      assertEquals(10L, testSerialId.getBundleId());
      assertEquals(0, testSerialId.getRecordId());
      assertEquals(30L, testSerialId.getSerialNumber());
   }

   @Test
   public void testSingleArgConstructorInvalidSerialId() {
      try {
         new SerialId("bob_3.10.0");
         fail("Expected Exception for invalid length");
      } catch (Exception e) {
         assertEquals("Invalid serialId! Expected length 5 but was 4", e.getMessage());
      }
   }

   @Test
   public void testFiveArgConstructor() {
      SerialId testSerialId = new SerialId("allison", 6, 12L, 24, 15);

      assertEquals("allison", testSerialId.getStreamId());
      assertEquals(6, testSerialId.getBundleSize());
      assertEquals(12L, testSerialId.getBundleId());
      assertEquals(24, testSerialId.getRecordId());
      assertEquals(15L, testSerialId.getSerialNumber());

      assertEquals("allison_6.12.24#15", testSerialId.toString());
   }

   @Test
   public void testJsonNodeConstructor() {
      ObjectNode testNode = JsonUtils.newNode();
      JsonUtils.addNode(testNode, "streamId", "bob");
      JsonUtils.addNode(testNode, "bundleSize", 3);
      JsonUtils.addNode(testNode, "bundleId", 10);
      JsonUtils.addNode(testNode, "recordId", 0);
      JsonUtils.addNode(testNode, "serialNumber", 30);

      SerialId testSerialId = new SerialId(testNode);

      assertEquals("bob", testSerialId.getStreamId());
      assertEquals(3, testSerialId.getBundleSize());
      assertEquals(10L, testSerialId.getBundleId());
      assertEquals(0, testSerialId.getRecordId());
      assertEquals(30L, testSerialId.getSerialNumber());

      assertEquals("bob_3.10.0#30", testSerialId.toString());
   }

   @Test
   public void testNextRecordId() {
      SerialId testSerialId = new SerialId("bob", 3, 6L, 12);

      assertEquals(1, testSerialId.nextRecordId()); // nextRecordId = (recordId + 1) % bundleSize = (0 + 1) % 3 = 1
   }

   @Test
   public void testNextSerialNumber() {
      SerialId testSerialId = new SerialId("bob", 3, 6L, 12);

      assertEquals(31L, testSerialId.nextSerialNumber()); // nextSerialNumber = nextSerialNumber + 1
   }

   @Test
   public void testNextSerialId() {
      SerialId testSerialId = new SerialId("bob", 3, 6L, 12);

      // nextSerialId:
      // bundleId = bundleId + ((recordId + 1) / bundleSize) = 10 + ((0 + 1) / 3) = 10
      // recordId = (recordId + 1) % bundleSize = (0 + 1) % 3 = 1

      SerialId nextSerialId = testSerialId.nextSerialId();

      assertEquals(10L, nextSerialId.getBundleId());
      assertEquals(1, nextSerialId.getRecordId());
   }

   @Test
   public void testIsRightAfter() {
      SerialId testSerialId0 = new SerialId("bob", 3, 6L, 12);
      SerialId testSerialId1 = new SerialId("bob", 3, 6L, 13);

      assertTrue(testSerialId1.isRightAfter(testSerialId0));
      assertFalse(testSerialId0.isRightAfter(testSerialId1));
   }

   @Test
   public void testIsRightBefore() {
      SerialId testSerialId0 = new SerialId("bob", 3, 6L, 12);
      SerialId testSerialId1 = new SerialId("bob", 3, 6L, 13);

      assertTrue(testSerialId0.isRightBefore(testSerialId1));
      assertFalse(testSerialId1.isRightBefore(testSerialId0));
   }

   @Test
   public void testAddBundleId() {
      SerialId testSerialId = new SerialId("bob", 3, 6L, 12);
      testSerialId.addBundleId(5L);

      assertEquals(15L, testSerialId.getBundleId());
   }

   @Test
   public void testAddRecordId() {
      SerialId testSerialId = new SerialId("bob", 3, 6L, 12);
      testSerialId.addRecordId(7);

      assertEquals(7, testSerialId.getRecordId());
   }

   @Test
   public void testHashCode() {
      SerialId testSerialId0 = new SerialId("bob", 3, 6L, 12);
      SerialId testSerialId1 = new SerialId("bob", 3, 6L, 12);
      SerialId testSerialId2 = new SerialId("bob", 3, 6L, 16);

      assertEquals(testSerialId0.hashCode(), testSerialId1.hashCode());
      assertNotEquals(testSerialId0.hashCode(), testSerialId2.hashCode());
      
      testSerialId2.setStreamId(null);
      assertNotEquals(testSerialId1.hashCode(), testSerialId2.hashCode());
   }

   @Test
   public void testEquals() {
      SerialId testSerialId0 = new SerialId("bob", 3, 6L, 12);
      SerialId testSerialId1 = new SerialId("bob", 3, 6L, 12);

      assertTrue(testSerialId0.equals(testSerialId0));
      assertFalse(testSerialId0.equals(null));

      assertTrue(testSerialId0.equals(testSerialId1));
      testSerialId1.setBundleId(7L);
      assertFalse(testSerialId0.equals(testSerialId1));
      testSerialId1.setBundleId(10L);

      assertTrue(testSerialId0.equals(testSerialId1));
      testSerialId1.setBundleSize(7);
      assertFalse(testSerialId0.equals(testSerialId1));
      testSerialId1.setBundleSize(3);

      assertTrue(testSerialId0.equals(testSerialId1));
      testSerialId1.setRecordId(1);
      assertFalse(testSerialId0.equals(testSerialId1));
      testSerialId1.setRecordId(0);

      assertTrue(testSerialId0.equals(testSerialId1));
      testSerialId1.setStreamId(null);
      assertFalse(testSerialId0.equals(testSerialId1));
      testSerialId0.setStreamId(null);
      assertTrue(testSerialId0.equals(testSerialId1));
      
      testSerialId0.setStreamId("bob");
      assertFalse(testSerialId1.equals(testSerialId0));

      testSerialId1.setStreamId("allison");
      assertFalse(testSerialId0.equals(testSerialId1));
   }

}
