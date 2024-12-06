package us.dot.its.jpo.ode.plugin.utils;

/**
   * Utility class containing static methods for bit manipulation.
   */
public class BitUtils {

  /**
   * Reverse bits in a single byte.
   *
   * @return The reversed byte.
   */
  public static byte reverseBits(final byte b) {
    var reversedInt = Integer.reverse((int) b << 24) & 0xff;
    return (byte) reversedInt;
  }

  /**
   * Reverse bits in a byte array.
   *
   * @return The reversed byte array.
   */
  public static byte[] reverseBits(final byte[] bytes) {
    byte[] reversed = new byte[bytes.length];
    for (int i = 0; i < bytes.length; i++) {
      reversed[i] = reverseBits(bytes[i]);
    }
    return reversed;
  }

}
