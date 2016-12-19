package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.oss.asn1.BitString;

import us.dot.its.jpo.ode.plugin.j2735.J2735BitString;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssBitString;

/**
 * Test class for OssBitString - NOTE! Current implementation of OssBitString is
 * incomplete and cannot be tested
 * 
 * OssBitString takes BitString and converts to J2735BitString - BitString is a
 * byte[] array with information encoded in individual bits - J2735BitString is
 * a HashMap<String, Boolean> - [Note] BitStrings are reverse-mapped to
 * J2735BitString i.e. "0011" -> {true,true,false,false} - [Note] Method
 * BitString().getTypeName() is not implemented yet
 * 
 * Tests: - BitString "0" returns J2735BitString with value (false) - BitString
 * "1" returns J2735BitString with value (true) - BitString "00" returns
 * J2735BitString with value ({false,false}) - BitString "01" returns
 * J2735BitString with value ({true,false}) - BitString "00000" returns
 * J2735BitString with value ({false,false,false,false,false}) - BitString
 * "11111" returns J2735BitString with value ({true,true,true,true,true}) -
 * BitString "01100" returns J2735BitString with value
 * ({false,false,true,true,false})
 *
 */
public class OssBitStringTest {

    /**
     * Note - this test class is incomplete!
     * 
     * Test that the input bit string 01 correctly returns a hashmap with
     * {false, true}
     */
    @Ignore
    @Test
    public void shouldReturnSequence01() {

        Integer testString = 0b01;
        byte[] testStringBytes = { testString.byteValue() };
        Boolean[] expectedValues = { true, false };
        Boolean[] actualValues = { null, null };

        BitString testBitString = new BitString(testStringBytes);
        J2735BitString testJ2735BitString = OssBitString.genericBitString(testBitString);

        Integer counter = 0;
        for (Boolean bitValue : testJ2735BitString.values()) {

            actualValues[counter] = bitValue;
            counter++;

        }

        assertArrayEquals(expectedValues, actualValues);

    }

}
