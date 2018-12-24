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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import javax.xml.bind.DatatypeConverter;

public class CodecUtils {

   private CodecUtils() {
   }

   /**
    * Converts an array of shorts to a byte array.
    * <p>
    * Example: (short) 5 will be stored as {0, 5} since 5 = 00000000 00000101
    * </p>
    * <p>
    * Example: (short) 257 will be stored as {1, 1} since 257 = 00000001
    * 00000001
    * </p>
    * 
    * @param shorts
    * @return byte array containing shorts as their byte value
    */
   public static byte[] shortsToBytes(short[] shorts) {
      ByteBuffer buffer = ByteBuffer.allocate(shorts.length * 2).order(ByteOrder.BIG_ENDIAN);
      for (short num : shorts) {
         buffer.putShort(num);
      }
      return buffer.array();
   }

   /**
    * Converts a single short to a byte array length 2. See
    * {@link #shortsToBytes(short[])}
    * 
    * @param number
    * @return byte array containing short as a 2-byte array
    */
   public static byte[] shortToBytes(short number) {
      short[] shorts = new short[] { number };
      return shortsToBytes(shorts);
   }

   /**
    * Converts an array of bytes to an array of shorts and returns the first
    * element. See {@link #bytesToShorts(byte[])}
    * 
    * @param bytes
    * @return array of shorts
    */
   public static short bytesToShort(byte[] bytes, int offset, int length, ByteOrder bo) {
      return bytesToShorts(bytes, offset, length, bo)[0];
   }

   /**
    * Converts an array of bytes to an array of shorts.
    * <p>
    * Example: {(byte) 1, (byte) 1} will return {(short) 257} since 257 =
    * 00000001 00000001
    * </p>
    * 
    * @param bytes
    * @return array of shorts
    */
   public static short[] bytesToShorts(byte[] bytes, int offset, int length, ByteOrder bo) {
      ByteBuffer buffer = ByteBuffer.allocate(length).order(bo);
      buffer.put(bytes, offset, length);
      buffer.flip();
      int numberOfShorts = length / 2;
      short[] shorts = new short[numberOfShorts];
      for (int i = 0; i < numberOfShorts; i++) {
         shorts[i] = buffer.getShort();
      }
      return shorts;
   }

   /**
    * Combines byte arrays.
    * 
    * @param bytes
    * @return combined array
    * @throws IOException
    */

   public static byte[] mergeBytes(byte[]... bytes) throws IOException {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      for (byte[] bArray : bytes) {
         outputStream.write(bArray);
      }
      return outputStream.toByteArray();
   }

   public static int bytesToInt(byte[] bytes, int offset, int length, ByteOrder bo) {
      return bytesToInts(bytes, offset, length, bo)[0];
   }

   public static int[] bytesToInts(byte[] bytes, int offset, int length, ByteOrder bo) {
      ByteBuffer buffer = ByteBuffer.allocate(length).order(bo);
      buffer.put(bytes, offset, length);
      buffer.flip();
      int numberOfInts = length / 4;
      int[] ints = new int[numberOfInts];
      for (int i = 0; i < numberOfInts; i++) {
         ints[i] = buffer.getInt();
      }
      return ints;
   }

   public static long bytesToLong(byte[] bytes, int offset, int length, ByteOrder bo) {
      return bytesToLongs(bytes, offset, length, bo)[0];
   }

   public static long[] bytesToLongs(byte[] bytes, int offset, int length, ByteOrder bo) {
      ByteBuffer buffer = ByteBuffer.allocate(length).order(bo);
      buffer.put(bytes, offset, length);
      buffer.flip();
      int numberOfLongs = length / 8;
      long[] longs = new long[numberOfLongs];
      for (int i = 0; i < numberOfLongs; i++) {
         longs[i] = buffer.getLong();
      }
      return longs;
   }

   public static String toHex(byte[] bytes) {
      return bytes != null ? DatatypeConverter.printHexBinary(bytes) : "";
   }
   
   public static String toHex(byte b) {
      return DatatypeConverter.printHexBinary(new byte[]{b});
   }

   public static byte[] fromHex(String hex) {
      return DatatypeConverter.parseHexBinary(hex);
   }

   public static String toBase64(byte[] bytes) {
      return bytes != null ? DatatypeConverter.printBase64Binary(bytes) : "";
   }

   public static byte[] fromBase64(String base64) {
      return DatatypeConverter.parseBase64Binary(base64);
   }

   /**
    * @param strShort
    *           String representation of a short integer value in binary or hex
    *           format. If the string is in binary format, the length must be
    *           exactly 16 1s and zeros. If Hex format, the length must be
    *           exactly 4 Hex digits.
    * 
    * @return a byte array equivalent of strShort
    */
   public static byte[] shortStringToByteArray(String strShort) {

      byte[] byteArrayValue = null;

      int radix = radixOf(strShort);

      if (radix == 0) {
         byteArrayValue = new byte[2]; // NOSONAR
      } else {
         byteArrayValue = Arrays
               .copyOfRange(ByteBuffer.allocate(4).putInt(Integer.parseUnsignedInt(strShort, radix)).array(), 2, 4);
      }

      return byteArrayValue;
   }

   /**
    * @param strShort
    *           String representation of a short integer value in binary or hex
    *           format. If strShort is in binary format, the length must be
    *           exactly 16 ones and zeros. If strShort is in Hex format, the
    *           length must be exactly 4 Hex digits.
    * @return The radix of the strShort: Currently supporting only binary and
    *         hex, therefore the return value is either 2 or 16
    */
   private static int radixOf(String strShort) {
      int radix = 0;
      if (strShort == null || strShort.length() == 0) {
         radix = 0;
      } else if (strShort.length() == 16) {
         radix = 2;
      } else if (strShort.length() == 4) {
         radix = 16;
      } else {
         throw new IllegalArgumentException("Short String length is invalid: " + strShort.length());
      }
      return radix;
   }
}
