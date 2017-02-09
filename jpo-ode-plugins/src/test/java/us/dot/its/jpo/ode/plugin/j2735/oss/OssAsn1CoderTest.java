package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class OssAsn1CoderTest {

    static OssAsn1Coder coder;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
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

        String expectedValue = "{\"coreData\":{\"msgCnt\":0,\"id\":\"52032900\",\"secMark\":0,\"position\":{\"latitude\":-90.0000000,\"longitude\":-179.9999999},\"accelSet\":{\"accelLat\":-20.00,\"accelLong\":-20.00,\"accelYaw\":-327.67},\"accuracy\":{\"semiMajor\":0.00,\"semiMinor\":0.00,\"orientation\":0E-10},\"transmission\":\"neutral\",\"speed\":0.00,\"heading\":0.0000,\"angle\":-189.0,\"brakes\":{\"wheelBrakes\":{\"leftFront\":false,\"rightFront\":false,\"unavailable\":false,\"leftRear\":false,\"rightRear\":false},\"traction\":\"unavailable\",\"abs\":\"unavailable\",\"scs\":\"unavailable\",\"brakeBoost\":\"unavailable\",\"auxBrakes\":\"unavailable\"},\"size\":{\"length\":31}},\"partII\":[{\"id\":\"vehicleSafetyExt\",\"value\":{\"events\":{\"eventReserved1\":true,\"eventLightsChanged\":true,\"eventStabilityControlactivated\":true,\"eventHazardousMaterials\":true,\"eventWipersChanged\":true,\"eventHazardLights\":true,\"eventTractionControlLoss\":true,\"eventABSactivated\":true,\"eventAirBagDeployment\":true,\"eventFlatTire\":true,\"eventDisabledVehicle\":true,\"eventHardBraking\":true,\"eventStopLineViolation\":true},\"pathHistory\":{\"initialPosition\":{\"position\":{\"latitude\":-90.0000000,\"longitude\":-179.9999999},\"heading\":0.0000,\"posAccuracy\":{\"semiMajor\":0.00,\"semiMinor\":0.00,\"orientation\":0E-10},\"posConfidence\":{\"pos\":\"unavailable\",\"elevation\":\"unavailable\"},\"speed\":{\"speed\":0.00,\"transmisson\":\"neutral\"},\"speedConfidence\":{\"heading\":\"unavailable\",\"speed\":\"unavailable\",\"throttle\":\"unavailable\"},\"timeConfidence\":\"unavailable\",\"utcTime\":{\"hour\":0,\"minute\":0,\"offset\":-840,\"second\":0}},\"currGNSSstatus\":{\"localCorrectionsPresent\":true,\"baseStationType\":true,\"inViewOfUnder5\":true,\"unavailable\":true,\"aPDOPofUnder5\":true,\"isMonitored\":true,\"isHealthy\":true,\"networkCorrectionsPresent\":true},\"crumbData\":[{\"heading\":0.0,\"posAccuracy\":{\"semiMajor\":0.00,\"semiMinor\":0.00,\"orientation\":0E-10},\"speed\":0.00,\"timeOffset\":0.01}]},\"pathPrediction\":{\"confidence\":0.0,\"radiusOfCurve\":-3276.7},\"lights\":{\"daytimeRunningLightsOn\":true,\"automaticLightControlOn\":true,\"rightTurnSignalOn\":true,\"parkingLightsOn\":true,\"lowBeamHeadlightsOn\":true,\"hazardSignalOn\":true,\"highBeamHeadlightsOn\":true,\"leftTurnSignalOn\":true,\"fogLightOn\":true}}}]}";
        assertEquals(expectedValue, coder.UPER_DecodeBsmHex(inputString).toJson());
    }

    @Ignore
    @Test
    public void testUPER_DecodeMessageFrameHex() {

        String inputString = "001480ad401480CA4000000000000000000000000000000000000000000000000000000000000000F800D9EFFFB7FFF00000000000000000000000000000000000000000000000000000001FE07000000000000000000000000000000000001FF0";

        String expectedValue = "{\"coreData\":{\"msgCnt\":0,\"id\":\"5202B500\",\"secMark\":20995,\"position\":{\"latitude\":-55.6067072,\"longitude\":-179.9999999},\"accelSet\":{\"accelLat\":-20.00,\"accelLong\":-20.00,\"accelYaw\":-327.67},\"accuracy\":{\"semiMajor\":0.00,\"semiMinor\":0.00,\"orientation\":0E-10},\"transmission\":\"neutral\",\"speed\":0.00,\"heading\":0.0000,\"angle\":-189.0,\"brakes\":{\"wheelBrakes\":{\"leftFront\":false,\"rightFront\":false,\"unavailable\":false,\"leftRear\":false,\"rightRear\":false},\"traction\":\"unavailable\",\"abs\":\"unavailable\",\"scs\":\"unavailable\",\"brakeBoost\":\"unavailable\",\"auxBrakes\":\"unavailable\"},\"size\":{}},\"partII\":[]}";

        assertEquals(expectedValue, coder.UPER_DecodeMessageFrameHex(inputString).toJson());

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
