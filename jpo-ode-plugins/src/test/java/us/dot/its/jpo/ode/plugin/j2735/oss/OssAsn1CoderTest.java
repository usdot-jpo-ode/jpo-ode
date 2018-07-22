package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.IOException;

import javax.xml.bind.DatatypeConverter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;

import com.oss.asn1.AbstractData;
import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;
import com.oss.asn1.PERUnalignedCoder;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.dsrc.BasicSafetyMessage;
import us.dot.its.jpo.ode.j2735.dsrc.MessageFrame;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;

@RunWith(JMockit.class)
public class OssAsn1CoderTest {
    
    @Mocked(stubOutClassInitialization = true)
    final LoggerFactory unused = null;

    @Tested
    OssJ2735Coder coder;

    @Test
    public void testUPER_DecodeBsmHex() {
        String inputString = "401480CA4000000000000000000000000000000000000000000000000000000000000000F800D9EFFFB7FFF00000000000000000000000000000000000000000000000000000001FE07000000000000000000000000000000000001FF0";

        String expectedValue = "{\"coreData\":{\"msgCnt\":0,\"id\":\"52032900\",\"secMark\":0,\"position\":{\"latitude\":-90.0000000,\"longitude\":-179.9999999},\"accelSet\":{\"accelLat\":-20.00,\"accelLong\":-20.00,\"accelYaw\":-327.67},\"accuracy\":{\"semiMajor\":0.00,\"semiMinor\":0.00,\"orientation\":0E-10},\"transmission\":\"NEUTRAL\",\"speed\":0.00,\"heading\":0.0000,\"angle\":-189.0,\"brakes\":{\"wheelBrakes\":{\"leftFront\":false,\"rightFront\":false,\"unavailable\":false,\"leftRear\":false,\"rightRear\":false},\"traction\":\"unavailable\",\"abs\":\"unavailable\",\"scs\":\"unavailable\",\"brakeBoost\":\"unavailable\",\"auxBrakes\":\"unavailable\"},\"size\":{\"length\":31}},\"partII\":[{\"id\":\"VehicleSafetyExtensions\",\"value\":{\"events\":{\"eventReserved1\":true,\"eventLightsChanged\":true,\"eventStabilityControlactivated\":true,\"eventHazardousMaterials\":true,\"eventWipersChanged\":true,\"eventHazardLights\":true,\"eventTractionControlLoss\":true,\"eventABSactivated\":true,\"eventAirBagDeployment\":true,\"eventFlatTire\":true,\"eventDisabledVehicle\":true,\"eventHardBraking\":true,\"eventStopLineViolation\":true},\"pathHistory\":{\"initialPosition\":{\"position\":{\"latitude\":-90.0000000,\"longitude\":-179.9999999},\"heading\":0.0000,\"posAccuracy\":{\"semiMajor\":0.00,\"semiMinor\":0.00,\"orientation\":0E-10},\"posConfidence\":{\"pos\":\"UNAVAILABLE\",\"elevation\":\"UNAVAILABLE\"},\"speed\":{\"speed\":0.00,\"transmisson\":\"NEUTRAL\"},\"speedConfidence\":{\"heading\":\"UNAVAILABLE\",\"speed\":\"UNAVAILABLE\",\"throttle\":\"UNAVAILABLE\"},\"timeConfidence\":\"UNAVAILABLE\",\"utcTime\":{\"hour\":0,\"minute\":0,\"offset\":-840,\"second\":0}},\"currGNSSstatus\":{\"localCorrectionsPresent\":true,\"baseStationType\":true,\"inViewOfUnder5\":true,\"unavailable\":true,\"aPDOPofUnder5\":true,\"isMonitored\":true,\"isHealthy\":true,\"networkCorrectionsPresent\":true},\"crumbData\":[{\"heading\":0.0,\"posAccuracy\":{\"semiMajor\":0.00,\"semiMinor\":0.00,\"orientation\":0E-10},\"speed\":0.00,\"timeOffset\":0.01}]},\"pathPrediction\":{\"confidence\":0.0,\"radiusOfCurve\":-3276.7},\"lights\":{\"daytimeRunningLightsOn\":true,\"automaticLightControlOn\":true,\"rightTurnSignalOn\":true,\"parkingLightsOn\":true,\"lowBeamHeadlightsOn\":true,\"hazardSignalOn\":true,\"highBeamHeadlightsOn\":true,\"leftTurnSignalOn\":true,\"fogLightOn\":true}}}]}";
        assertEquals(expectedValue, coder.decodeUPERBsmHex(inputString).toJson(false));
    }

    @Test
    public void testUPER_DecodeMessageFrameHex() {

        String inputString = "001480ad59afa8400023efe717087d9665fde4ad0cfffffffff0006451fdfa1fa1007fff8000000000020214c1c10011fffff0be19ee101727ffff05c22d6101d840067046268210391404fbffe380210290c04bdffe3ea6102ffc05d9ffe48921033940927ffe62be102a9c07d1ffe6a521025640809ffe6eda1022bc07a9ffe6fb61ffffc00bffff0fde1fffffffff0fd56061fffffffff0e76dc61fffffffff0c9724e1ffffc008d0cd7802fffe0000";

        String expectedValue = "{\"messageId\":\"BasicSafetyMessage\",\"value\":{\"coreData\":{\"msgCnt\":102,\"id\":\"BEA10000\",\"secMark\":36799,\"position\":{\"latitude\":41.1641851,\"longitude\":-104.8434230,\"elevation\":1896.9},\"accelSet\":{\"accelYaw\":0.00},\"accuracy\":{},\"speed\":0.00,\"heading\":321.0125,\"brakes\":{\"wheelBrakes\":{\"leftFront\":false,\"rightFront\":false,\"unavailable\":true,\"leftRear\":false,\"rightRear\":false},\"traction\":\"unavailable\",\"abs\":\"unavailable\",\"scs\":\"unavailable\",\"brakeBoost\":\"unavailable\",\"auxBrakes\":\"unavailable\"},\"size\":{}},\"partII\":[{\"id\":\"VehicleSafetyExtensions\",\"value\":{\"pathHistory\":{\"crumbData\":[{\"elevationOffset\":9.5,\"latOffset\":0.0000035,\"lonOffset\":0.0131071,\"timeOffset\":33.20},{\"elevationOffset\":4.6,\"latOffset\":0.0000740,\"lonOffset\":0.0131071,\"timeOffset\":44.60},{\"elevationOffset\":3.5,\"latOffset\":0.0000944,\"lonOffset\":0.0000051,\"timeOffset\":49.30},{\"elevationOffset\":204.7,\"latOffset\":0.0001826,\"lonOffset\":0.0000637,\"timeOffset\":71.70},{\"elevationOffset\":204.7,\"latOffset\":0.0001313,\"lonOffset\":0.0000606,\"timeOffset\":80.20},{\"elevationOffset\":204.7,\"latOffset\":0.0001535,\"lonOffset\":0.0000748,\"timeOffset\":92.90},{\"elevationOffset\":204.7,\"latOffset\":0.0001650,\"lonOffset\":0.0001171,\"timeOffset\":126.40},{\"elevationOffset\":204.7,\"latOffset\":0.0001363,\"lonOffset\":0.0001000,\"timeOffset\":136.10},{\"elevationOffset\":204.7,\"latOffset\":0.0001196,\"lonOffset\":0.0001028,\"timeOffset\":141.90},{\"elevationOffset\":204.7,\"latOffset\":0.0001111,\"lonOffset\":0.0000980,\"timeOffset\":143.00},{\"elevationOffset\":204.7,\"latOffset\":0.0131071,\"lonOffset\":0.0000095,\"timeOffset\":348.00},{\"elevationOffset\":12.6,\"latOffset\":0.0131071,\"lonOffset\":0.0131071,\"timeOffset\":437.80},{\"elevationOffset\":11.5,\"latOffset\":0.0131071,\"lonOffset\":0.0131071,\"timeOffset\":468.20},{\"elevationOffset\":10.0,\"latOffset\":0.0131071,\"lonOffset\":0.0131071,\"timeOffset\":474.00},{\"elevationOffset\":10.2,\"latOffset\":0.0131071,\"lonOffset\":0.0000070,\"timeOffset\":481.30}]},\"pathPrediction\":{\"confidence\":0.0,\"radiusOfCurve\":0.0}}}]}}";

        assertEquals(expectedValue, coder.decodeUPERMessageFrameHex(inputString).toJson(false));

    }

    @Test
    public void test_decodeUPERBsmStream_throwsDecodeFailedException_someBytes(
            @Mocked final PERUnalignedCoder mockPERUnalignedCoder, @Mocked final J2735 mockJ2735,
            @Injectable BufferedInputStream mockInputStream, @Mocked final OssBsm mockOssBsm) {

        try {
            new Expectations() {
                {
                    J2735.getPERUnalignedCoder();
                    result = mockPERUnalignedCoder;

                    mockPERUnalignedCoder.decode(mockInputStream, (AbstractData) any);

                    mockInputStream.available();
                    result = 2;

               }
            };
        } catch (Exception e) {
            fail("Unexpected exception in expectations block: " + e);
        }

        OssJ2735Coder testOssAsn1Coder = new OssJ2735Coder();

        testOssAsn1Coder.decodeUPERBsmStream(mockInputStream);
    }

    // Bsm tests
    @Test
    public void test_decodeUPERBsmStream_throwsDecodeFailedException_nullBytes(
            @Mocked final PERUnalignedCoder mockPERUnalignedCoder, @Mocked final J2735 mockJ2735,
            @Injectable BufferedInputStream mockInputStream, @Mocked final OssBsm mockOssBsm) {

        try {
            new Expectations() {
                {
                    J2735.getPERUnalignedCoder();
                    result = mockPERUnalignedCoder;

                    mockPERUnalignedCoder.decode(mockInputStream, (AbstractData) any);

                    mockInputStream.available();
                    result = 2;

                }
            };
        } catch (Exception e) {
            fail("Unexpected exception in expectations block: " + e);
        }

        OssJ2735Coder testOssAsn1Coder = new OssJ2735Coder();

        testOssAsn1Coder.decodeUPERBsmStream(mockInputStream);
    }

    @Test
    public void test_decodeUPERBsmStream_noException(@Mocked final PERUnalignedCoder mockPERUnalignedCoder,
            @Mocked final J2735 mockJ2735, @Injectable BufferedInputStream mockInputStream, @Mocked final OssBsm mockOssBsm) {

        try {
            new Expectations() {
                {
                    J2735.getPERUnalignedCoder();
                    result = mockPERUnalignedCoder;

                    mockPERUnalignedCoder.decode(mockInputStream, (AbstractData) any);

                    mockInputStream.available();
                    result = 2;

                    OssBsm.genericBsm((BasicSafetyMessage) any);
                    result = null;

                }
            };
        } catch (Exception e) {
            fail("Unexpected exception in expectations block: " + e);
        }

        OssJ2735Coder testOssAsn1Coder = new OssJ2735Coder();

        testOssAsn1Coder.decodeUPERBsmStream(mockInputStream);
    }

    @Test
    public void test_decodeUPERBsmStream_noAvailableBytes(@Mocked final PERUnalignedCoder mockPERUnalignedCoder,
            @Mocked final J2735 mockJ2735, @Injectable BufferedInputStream mockInputStream, @Mocked final OssBsm mockOssBsm) {

        try {
            new Expectations() {
                {
                    J2735.getPERUnalignedCoder();
                    result = mockPERUnalignedCoder;

                    // mockPERUnalignedCoder.decode(mockInputStream,
                    // (AbstractData) any);

                    mockInputStream.available();
                    result = 0;

                }
            };
        } catch (Exception e) {
            fail("Unexpected exception in expectations block: " + e);
        }

        OssJ2735Coder testOssAsn1Coder = new OssJ2735Coder();

        testOssAsn1Coder.decodeUPERBsmStream(mockInputStream);
    }

    // Message Frame tests
    @Test
    public void test_decodeUPERMessageFrameStream_throwsDecodeFailedException_nullBytes(
            @Mocked final PERUnalignedCoder mockPERUnalignedCoder, @Mocked final J2735 mockJ2735,
            @Injectable BufferedInputStream mockInputStream, @Mocked final OssMessageFrame mockOssMessageFrame) {

        try {
            new Expectations() {
                {
                    J2735.getPERUnalignedCoder();
                    result = mockPERUnalignedCoder;

                    mockPERUnalignedCoder.decode(mockInputStream, (AbstractData) any);

                    mockInputStream.available();
                    result = 2;
                }
            };
        } catch (Exception e) {
            fail("Unexpected exception in expectations block: " + e);
        }

        OssJ2735Coder testOssAsn1Coder = new OssJ2735Coder();

        testOssAsn1Coder.decodeUPERMessageFrameStream(mockInputStream);
    }

    @Test
    public void test_decodeUPERMessageFrameStream_noException(@Mocked final PERUnalignedCoder mockPERUnalignedCoder,
            @Mocked final J2735 mockJ2735, @Injectable BufferedInputStream mockInputStream,
            @Mocked final OssMessageFrame mockOssMessageFrame) {

        try {
            new Expectations() {
                {
                    J2735.getPERUnalignedCoder();
                    result = mockPERUnalignedCoder;

                    mockPERUnalignedCoder.decode(mockInputStream, (AbstractData) any);

                    mockInputStream.available();
                    result = 2;

                    OssMessageFrame.genericMessageFrame((MessageFrame) any);
                    result = null;

                }
            };
        } catch (Exception e) {
            fail("Unexpected exception in expectations block: " + e);
        }

        OssJ2735Coder testOssAsn1Coder = new OssJ2735Coder();

        testOssAsn1Coder.decodeUPERMessageFrameStream(mockInputStream);
    }

    @Test
    public void test_decodeUPERMessageFrameStream_noAvailableBytes(
            @Mocked final PERUnalignedCoder mockPERUnalignedCoder, @Mocked final J2735 mockJ2735,
            @Injectable BufferedInputStream mockInputStream, @Mocked final OssMessageFrame mockOssMessageFrame) {

        try {
            new Expectations() {
                {
                    J2735.getPERUnalignedCoder();
                    result = mockPERUnalignedCoder;

                    mockInputStream.available();
                    result = 0;

                }
            };
        } catch (Exception e) {
            fail("Unexpected exception in expectations block: " + e);
        }

        OssJ2735Coder testOssAsn1Coder = new OssJ2735Coder();

        testOssAsn1Coder.decodeUPERMessageFrameStream(mockInputStream);
    }

    // handleDecodeException tests
    @Test
    public void test_handleDecodeException_DecodeNotSupportedException() {
        coder.handleDecodeException(new Exception());
    }

    @Test
    public void test_handleDecodeException_IOException() {
        coder.handleDecodeException(new IOException("testException123"));
    }

    @Test
    public void test_handleDecodeException_OssBsmPart2Exception() {
        coder.handleDecodeException(new Exception());
    }

    @Test
    public void test_handleDecodeException_OssMessageFrameException() {
        coder.handleDecodeException(new Exception());
    }

    @Test
    public void test_handleDecodeException_GenericException() {
        coder.handleDecodeException(new Exception("testException123"));
    }

    // Other tests
    @Test
    public void test_encodeUPERBase64(@Mocked final DatatypeConverter mockDatatypeConverter) {

        new Expectations() {
            {
                DatatypeConverter.printBase64Binary((byte[]) any);
            }
        };

        coder.encodeUPERBase64(null);
    }

    @Test
    public void test_encodeUPERHex(@Mocked final DatatypeConverter mockDatatypeConverter) {

        new Expectations() {
            {
                DatatypeConverter.printHexBinary((byte[]) any);
            }
        };

        coder.encodeUPERHex(null);
    }

    @Test
    public void test_encodeUPERBytes_nullInput() {
        coder.encodeUPERBytes(null);
    }

    @Test
    public void test_encodeUPERBytes_mockJ2735Bsm(@Mocked final J2735 mockJ2735,
            @Mocked PERUnalignedCoder mockPERUnalignedCoder, @Injectable J2735Bsm mockJ2735Bsm,
            @Mocked final OssBsm mockOssBsm) {

        try {
            new Expectations() {
                {
                    J2735.getPERUnalignedCoder();
                    result = mockPERUnalignedCoder;

                    OssBsm.basicSafetyMessage((J2735Bsm) any);

                    mockPERUnalignedCoder.encode((BasicSafetyMessage) any);
                }
            };
        } catch (EncodeFailedException | EncodeNotSupportedException e) {
            fail("Unexpected exception in expectations block: " + e);
        }
        OssJ2735Coder testOssAsn1Coder = new OssJ2735Coder();

        testOssAsn1Coder.encodeUPERBytes(mockJ2735Bsm);
    }

    @Test
    public void test_encodeUPERBytes_shouldCatchException(@Mocked final J2735 mockJ2735,
            @Mocked PERUnalignedCoder mockPERUnalignedCoder, @Injectable J2735Bsm mockJ2735Bsm,
            @Mocked final OssBsm mockOssBsm) {

        try {
            new Expectations() {
                {
                    J2735.getPERUnalignedCoder();
                    result = mockPERUnalignedCoder;

                    OssBsm.basicSafetyMessage((J2735Bsm) any);

                }
            };
        } catch (Exception e) {
            fail("Unexpected exception in expectations block: " + e);
        }
        OssJ2735Coder testOssAsn1Coder = new OssJ2735Coder();

        testOssAsn1Coder.encodeUPERBytes(mockJ2735Bsm);
    }

}
