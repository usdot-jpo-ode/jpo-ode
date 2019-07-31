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
package us.dot.its.jpo.ode.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Arrays;

import org.junit.Test;

public class CodecUtilsTest {

   @Test
   public void testShortsToBytes() {

      byte[] expectedValue = new byte[] { 0, 5, 0, -2, 1, 1 };

      byte[] actualValue = CodecUtils.shortsToBytes(new short[] { (short) 5, (short) 254, (short) 257 }, ByteOrder.BIG_ENDIAN);

      assertEquals(Arrays.toString(expectedValue), Arrays.toString(actualValue));
   }

   @Test
   public void testShortToBytes() {
      byte[] expectedValue = new byte[] { 1, 37 };

      byte[] actualValue = CodecUtils.shortToBytes((short) 293, ByteOrder.BIG_ENDIAN);

      assertEquals(Arrays.toString(expectedValue), Arrays.toString(actualValue));
   }

   @Test
   public void testBytesToShorts() {
      short[] expectedValue = new short[] { (short) 513 };

      short[] actualValue = CodecUtils.bytesToShorts(new byte[] { 2, 1 }, 0, 2, ByteOrder.BIG_ENDIAN);

      assertEquals(Arrays.toString(expectedValue), Arrays.toString(actualValue));
   }

   @Test
   public void testBytesToShort() {
      short expectedValue = (short) 258;

      short actualValue = CodecUtils.bytesToShort(new byte[] { 1, 2, 5, 2 }, 0, 4, ByteOrder.BIG_ENDIAN);

      assertEquals(expectedValue, actualValue);
   }

   @Test
   public void testMergeBytes() {

      byte[] expectedValue = new byte[] { 1, 1, 2, 3, 5, 8 };

      try {
         byte[] actualValue = CodecUtils.mergeBytes(new byte[] { 1 }, new byte[] { 1, 2 }, new byte[] { 3, 5, 8 });
         assertEquals(Arrays.toString(expectedValue), Arrays.toString(actualValue));
      } catch (IOException e) {
         fail("Unexpected exception: " + e);
      }
   }

   @Test
   public void bytesToLongs() {
      // 598273498 decimal == 23A8EDDA hex == 0, 0, 0, 0, 35, 168, 237, 218
      // 54 decimal = 0, 0, 0, 0, 0, 0, 0, 54
      long[] expectedValue = new long[] { (long) 598273498, (long) 54 };
      long[] actualValue = CodecUtils.bytesToLongs(
            new byte[] { (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 35, (byte) 168, (byte) 237, (byte) 218,
                  (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 54 },
            0, 16, ByteOrder.BIG_ENDIAN);
      assertEquals(Arrays.toString(expectedValue), Arrays.toString(actualValue));
   }

}
