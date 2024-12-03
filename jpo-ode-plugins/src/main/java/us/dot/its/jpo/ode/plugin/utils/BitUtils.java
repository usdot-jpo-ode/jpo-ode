package us.dot.its.jpo.ode.plugin.utils;


public class BitUtils {


    public static byte reverseBits(final byte b) {
        var reversedInt = Integer.reverse((int)b << 24) & 0xff;
        return (byte)reversedInt;
    }

    public static byte[] reverseBits(final byte[] bytes) {
        byte[] reversed = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            reversed[i] = reverseBits(bytes[i]);
        }
        return reversed;
    }


}

