package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import mockit.Mocked;

public class OssAsn1CoderTest {
    
    @Mocked(stubOutClassInitialization = true)
    final LoggerFactory unused = null;

    OssAsn1Coder coder;

    @Before
    public void setUp() throws Exception {
        coder = new OssAsn1Coder();
    }

    @Ignore
    @Test
    public void testOssAsn1Coder() {
        fail("Not yet implemented");
    }

    @Ignore
    @Test
    public void testUPER_DecodeBase64() {
        fail("Not yet implemented");
    }

    @Test
    public void testUPER_DecodeBsmHex() {
        String inputString = "401480CA4000000000000000000000000000000000000000000000000000000000000000F800D9EFFFB7FFF00000000000000000000000000000000000000000000000000000001FE07000000000000000000000000000000000001FF0";

        String expectedValue = "{\"coreData\":{\"msgCnt\":0,\"id\":\"52032900\",\"secMark\":0,\"position\":{\"latitude\":-90.0000000,\"longitude\":-179.9999999},\"accelSet\":{\"accelLat\":-20.00,\"accelLong\":-20.00,\"accelYaw\":-327.67},\"accuracy\":{\"semiMajor\":0.00,\"semiMinor\":0.00,\"orientation\":0E-10},\"transmission\":\"NEUTRAL\",\"speed\":0.00,\"heading\":0.0000,\"angle\":-189.0,\"brakes\":{\"wheelBrakes\":{\"leftFront\":false,\"rightFront\":false,\"unavailable\":false,\"leftRear\":false,\"rightRear\":false},\"traction\":\"unavailable\",\"abs\":\"unavailable\",\"scs\":\"unavailable\",\"brakeBoost\":\"unavailable\",\"auxBrakes\":\"unavailable\"},\"size\":{\"length\":31}},\"partII\":[{\"id\":\"VEHICLESAFETYEXT\",\"value\":{\"events\":{\"eventReserved1\":true,\"eventLightsChanged\":true,\"eventStabilityControlactivated\":true,\"eventHazardousMaterials\":true,\"eventWipersChanged\":true,\"eventHazardLights\":true,\"eventTractionControlLoss\":true,\"eventABSactivated\":true,\"eventAirBagDeployment\":true,\"eventFlatTire\":true,\"eventDisabledVehicle\":true,\"eventHardBraking\":true,\"eventStopLineViolation\":true},\"pathHistory\":{\"initialPosition\":{\"position\":{\"latitude\":-90.0000000,\"longitude\":-179.9999999},\"heading\":0.0000,\"posAccuracy\":{\"semiMajor\":0.00,\"semiMinor\":0.00,\"orientation\":0E-10},\"posConfidence\":{\"pos\":\"UNAVAILABLE\",\"elevation\":\"UNAVAILABLE\"},\"speed\":{\"speed\":0.00,\"transmisson\":\"NEUTRAL\"},\"speedConfidence\":{\"heading\":\"UNAVAILABLE\",\"speed\":\"UNAVAILABLE\",\"throttle\":\"UNAVAILABLE\"},\"timeConfidence\":\"UNAVAILABLE\",\"utcTime\":{\"hour\":0,\"minute\":0,\"offset\":-840,\"second\":0}},\"currGNSSstatus\":{\"localCorrectionsPresent\":true,\"baseStationType\":true,\"inViewOfUnder5\":true,\"unavailable\":true,\"aPDOPofUnder5\":true,\"isMonitored\":true,\"isHealthy\":true,\"networkCorrectionsPresent\":true},\"crumbData\":[{\"heading\":0.0,\"posAccuracy\":{\"semiMajor\":0.00,\"semiMinor\":0.00,\"orientation\":0E-10},\"speed\":0.00,\"timeOffset\":0.01}]},\"pathPrediction\":{\"confidence\":0.0,\"radiusOfCurve\":-3276.7},\"lights\":{\"daytimeRunningLightsOn\":true,\"automaticLightControlOn\":true,\"rightTurnSignalOn\":true,\"parkingLightsOn\":true,\"lowBeamHeadlightsOn\":true,\"hazardSignalOn\":true,\"highBeamHeadlightsOn\":true,\"leftTurnSignalOn\":true,\"fogLightOn\":true}}}]}";
        assertEquals(expectedValue, coder.decodeUPERBsmHex(inputString).toJson(false));
    }

    @Test
    public void testUPER_DecodeMessageFrameHex() {

        String inputString = "001480ad59afa8400023efe717087d9665fde4ad0cfffffffff0006451fdfa1fa1007fff8000000000020214c1c10011fffff0be19ee101727ffff05c22d6101d840067046268210391404fbffe380210290c04bdffe3ea6102ffc05d9ffe48921033940927ffe62be102a9c07d1ffe6a521025640809ffe6eda1022bc07a9ffe6fb61ffffc00bffff0fde1fffffffff0fd56061fffffffff0e76dc61fffffffff0c9724e1ffffc008d0cd7802fffe0000";

        String expectedValue = "{\"coreData\":{\"msgCnt\":102,\"id\":\"BEA10000\",\"secMark\":36799,\"position\":{\"latitude\":41.1641851,\"longitude\":-104.8434230,\"elevation\":1896.9},\"accelSet\":{\"accelYaw\":0.00},\"accuracy\":{},\"speed\":0.00,\"heading\":321.0125,\"brakes\":{\"wheelBrakes\":{\"leftFront\":false,\"rightFront\":false,\"unavailable\":true,\"leftRear\":false,\"rightRear\":false},\"traction\":\"unavailable\",\"abs\":\"unavailable\",\"scs\":\"unavailable\",\"brakeBoost\":\"unavailable\",\"auxBrakes\":\"unavailable\"},\"size\":{}},\"partII\":[{\"id\":\"VEHICLESAFETYEXT\",\"value\":{\"pathHistory\":{\"crumbData\":[{\"elevationOffset\":9.5,\"latOffset\":0.0000035,\"lonOffset\":0.0131071,\"timeOffset\":33.20},{\"elevationOffset\":4.6,\"latOffset\":0.0000740,\"lonOffset\":0.0131071,\"timeOffset\":44.60},{\"elevationOffset\":3.5,\"latOffset\":0.0000944,\"lonOffset\":0.0000051,\"timeOffset\":49.30},{\"elevationOffset\":204.7,\"latOffset\":0.0001826,\"lonOffset\":0.0000637,\"timeOffset\":71.70},{\"elevationOffset\":204.7,\"latOffset\":0.0001313,\"lonOffset\":0.0000606,\"timeOffset\":80.20},{\"elevationOffset\":204.7,\"latOffset\":0.0001535,\"lonOffset\":0.0000748,\"timeOffset\":92.90},{\"elevationOffset\":204.7,\"latOffset\":0.0001650,\"lonOffset\":0.0001171,\"timeOffset\":126.40},{\"elevationOffset\":204.7,\"latOffset\":0.0001363,\"lonOffset\":0.0001000,\"timeOffset\":136.10},{\"elevationOffset\":204.7,\"latOffset\":0.0001196,\"lonOffset\":0.0001028,\"timeOffset\":141.90},{\"elevationOffset\":204.7,\"latOffset\":0.0001111,\"lonOffset\":0.0000980,\"timeOffset\":143.00},{\"elevationOffset\":204.7,\"latOffset\":0.0131071,\"lonOffset\":0.0000095,\"timeOffset\":348.00},{\"elevationOffset\":12.6,\"latOffset\":0.0131071,\"lonOffset\":0.0131071,\"timeOffset\":437.80},{\"elevationOffset\":11.5,\"latOffset\":0.0131071,\"lonOffset\":0.0131071,\"timeOffset\":468.20},{\"elevationOffset\":10.0,\"latOffset\":0.0131071,\"lonOffset\":0.0131071,\"timeOffset\":474.00},{\"elevationOffset\":10.2,\"latOffset\":0.0131071,\"lonOffset\":0.0000070,\"timeOffset\":481.30}]},\"pathPrediction\":{\"confidence\":0.0,\"radiusOfCurve\":0.0}}}]}";

        assertEquals(expectedValue, coder.decodeUPERMessageFrameHex(inputString).toJson(false));

    }

    @Ignore
    @Test
    public void testUPER_DecodeBytes() {
        fail("Not yet implemented");
    }

    @Ignore
    @Test
    public void testUPER_DecodeBase64ToJson() {
        fail("Not yet implemented");
    }

    @Ignore
    @Test
    public void testUPER_DecodeHexToJson() {
        fail("Not yet implemented");
    }

    @Ignore
    @Test
    public void testUPER_DecodeBytesToJson() {
        fail("Not yet implemented");
    }

    @Ignore
    @Test
    public void testUPER_EncodeBase64() {
        fail("Not yet implemented");
    }

    @Ignore
    @Test
    public void testUPER_EncodeHex() {
        fail("Not yet implemented");
    }

    @Ignore
    @Test
    public void testUPER_EncodeBytes() {
        fail("Not yet implemented");
    }

    @Ignore
    @Test
    public void testUPER_EncodeBase64FromJson() {
        fail("Not yet implemented");
    }

    @Ignore
    @Test
    public void testUPER_EncodeHexfromJson() {
        fail("Not yet implemented");
    }

    @Ignore
    @Test
    public void testUPER_EncodeBytesFromJson() {
        fail("Not yet implemented");
    }

}
