package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.AntennaOffsetSet;
import us.dot.its.jpo.ode.j2735.dsrc.GNSSstatus;
import us.dot.its.jpo.ode.j2735.dsrc.Offset_B09;
import us.dot.its.jpo.ode.j2735.dsrc.Offset_B10;
import us.dot.its.jpo.ode.j2735.dsrc.Offset_B12;
import us.dot.its.jpo.ode.j2735.dsrc.RTCMheader;
import us.dot.its.jpo.ode.plugin.j2735.J2735GNSSstatus;
import us.dot.its.jpo.ode.plugin.j2735.J2735RTCMheader;

/**
 * -- Summary --
 * JUnit test class for OssRTCMheader
 * 
 * Verifies correct conversion from generic RTCMheader to compliant-J2735RTCMheader
 * 
 * This is a trivial test class. GNSStatus element is tested by OssGNSSstatusTest and AntennaOffsetSet is tested
 * by OssAntennaOffsetSetTest. This class tests a known GNSSstatus and AntennaOffsetSet pair is successfully created
 * 
 * -- Documentation --
 * Data Frame: DF_RTCMheader
 * Use: The DF_RTCMheader data frame is a collection of data values used to convey RTCM information between users. 
 * It is not required or used when sending RTCM data from a corrections source to end users (from a base station 
 * to devices deployed in the field which are called rovers).
 * ASN.1 Representation:
 *    RTCMheader ::= SEQUENCE {
 *       status GNSSstatus,
 *       offsetSet AntennaOffsetSet
 *       }
 */
public class OssRTCMheaderTest {
    
    /**
     * Test that a known RTCM header with known GNSS status "isHealthy"
     */
    @Test
    public void shouldCreateRTCMheaderWithKnownGNSSstatus() {
        
        Integer testInput = 0b00000010;
        String elementTested = "isHealthy";
        
        byte[] testInputBytes = {testInput.byteValue()};
        
        GNSSstatus testGNSSstatus = new GNSSstatus(testInputBytes);
        
        Offset_B12 testXOffset = new Offset_B12(0);
        Offset_B09 testYOffset = new Offset_B09(0);
        Offset_B10 testZOffset = new Offset_B10(0);
        
        AntennaOffsetSet testAntennaOffsetSet = new AntennaOffsetSet(
                testXOffset,
                testYOffset,
                testZOffset);
        
        RTCMheader testHeader = new RTCMheader();
        testHeader.offsetSet = testAntennaOffsetSet;
        testHeader.status = testGNSSstatus;
        
        J2735RTCMheader actualHeader = OssRTCMheader.genericRTCMheader(testHeader);
        
        for (Map.Entry<String, Boolean> curVal : actualHeader.status.entrySet()) {
            if(curVal.getKey() == elementTested) {
                assertTrue("Expected " + curVal.getKey() + " to be true", curVal.getValue());
            } else {
                assertFalse("Expected " + curVal.getKey() + " to be false", curVal.getValue());
            }
        }
        
    }
    
    /**
     * Test that a known RTCM header with a known antenna offset set with x offset value (0.11)
     */
    @Test
    public void shouldCreateRTCMheaderWithKnownAntennaOffsetSet() {
        
        Integer testInput = 11;
        BigDecimal expectedValue = BigDecimal.valueOf(0.11);
        
        byte[] testInputBytes = {testInput.byteValue()};
        
        GNSSstatus testGNSSstatus = new GNSSstatus(testInputBytes);
        
        Offset_B12 testXOffset = new Offset_B12(testInput);
        Offset_B09 testYOffset = new Offset_B09(0);
        Offset_B10 testZOffset = new Offset_B10(0);
        
        AntennaOffsetSet testAntennaOffsetSet = new AntennaOffsetSet(
                testXOffset,
                testYOffset,
                testZOffset);
        
        RTCMheader testHeader = new RTCMheader();
        testHeader.offsetSet = testAntennaOffsetSet;
        testHeader.status = testGNSSstatus;
        
        BigDecimal actualValue = OssRTCMheader.genericRTCMheader(testHeader).offsetSet.getAntOffsetX();
        
        assertEquals(expectedValue, actualValue);
    }

}
