package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.j2735.dsrc.Acceleration;
import us.dot.its.jpo.ode.j2735.dsrc.AccelerationSet4Way;
import us.dot.its.jpo.ode.j2735.dsrc.AntiLockBrakeStatus;
import us.dot.its.jpo.ode.j2735.dsrc.AuxiliaryBrakeStatus;
import us.dot.its.jpo.ode.j2735.dsrc.BSMcoreData;
import us.dot.its.jpo.ode.j2735.dsrc.BasicSafetyMessage;
import us.dot.its.jpo.ode.j2735.dsrc.BrakeAppliedStatus;
import us.dot.its.jpo.ode.j2735.dsrc.BrakeBoostApplied;
import us.dot.its.jpo.ode.j2735.dsrc.BrakeSystemStatus;
import us.dot.its.jpo.ode.j2735.dsrc.DSecond;
import us.dot.its.jpo.ode.j2735.dsrc.Elevation;
import us.dot.its.jpo.ode.j2735.dsrc.Heading;
import us.dot.its.jpo.ode.j2735.dsrc.Latitude;
import us.dot.its.jpo.ode.j2735.dsrc.Longitude;
import us.dot.its.jpo.ode.j2735.dsrc.MsgCount;
import us.dot.its.jpo.ode.j2735.dsrc.PositionalAccuracy;
import us.dot.its.jpo.ode.j2735.dsrc.SemiMajorAxisAccuracy;
import us.dot.its.jpo.ode.j2735.dsrc.SemiMajorAxisOrientation;
import us.dot.its.jpo.ode.j2735.dsrc.SemiMinorAxisAccuracy;
import us.dot.its.jpo.ode.j2735.dsrc.Speed;
import us.dot.its.jpo.ode.j2735.dsrc.StabilityControlStatus;
import us.dot.its.jpo.ode.j2735.dsrc.SteeringWheelAngle;
import us.dot.its.jpo.ode.j2735.dsrc.TemporaryID;
import us.dot.its.jpo.ode.j2735.dsrc.TractionControlStatus;
import us.dot.its.jpo.ode.j2735.dsrc.TransmissionState;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleLength;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleSize;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleWidth;
import us.dot.its.jpo.ode.j2735.dsrc.VerticalAcceleration;
import us.dot.its.jpo.ode.j2735.dsrc.YawRate;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735BsmCoreData;
import us.dot.its.jpo.ode.plugin.j2735.J2735Position3D;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssBsmPart2Content.OssBsmPart2Exception;

/**
 * -- Summary -- JUnit test class for OssBsm
 * 
 * Verifies correct conversion from generic BSM object to compliant-J2735Bsm
 * 
 * This tests successful population of a Bsm including core data and part II
 * content.
 * 
 * -- Documentation -- Message: MSG_BasicSafetyMessage (BSM) Use: The basic
 * safety message (BSM) is used in a variety of applications to exchange safety
 * data regarding vehicle state. This message is broadcast frequently to
 * surrounding vehicles with data content as required by safety and other
 * applications. Transmission rates are beyond the scope of this standard, but a
 * rate 10 times per second is typical when congestion control algorithms do not
 * prescribe a reduced rate. Part I data shall be included in every BSM. Part II
 * data items are optional for a given BSM and are included as needed according
 * to policies that are beyond the scope of this standard. A BSM without Part II
 * optional content is a valid message.
 *
 */
@RunWith(JMockit.class)
public class OssBsmTest {

    /**
     * Test that a BSM can be populated with known core data
     */
    @Test
    public void shouldCreateKnownBsmCoreData() {

        Integer testMsgCnt = 127;
        Integer expectedMsgCnt = 127;

        Integer testId = 0b11111111111111111111111111111111;
        String expectedId = "FF";

        Integer testSec = 65534;
        Integer expectedSec = 65534;

        Integer testLat = 900000000;
        BigDecimal expectedLat = BigDecimal.valueOf(90.0000000).setScale(7);

        Integer testLong = 1800000000;
        BigDecimal expectedLong = BigDecimal.valueOf(180.0000000).setScale(7);

        Integer testElev = 61439;
        BigDecimal expectedElev = BigDecimal.valueOf(6143.9);

        // Positional accuracy
        Integer testSemiMajorAxisAccuracy = 254;
        BigDecimal expectedSemiMajorAxisAccuracy = BigDecimal.valueOf(12.70).setScale(2);

        Integer testSemiMinorAxisAccuracy = 254;
        BigDecimal expectedSemiMinorAxisAccuracy = BigDecimal.valueOf(12.70).setScale(2);

        Integer testSemiMajorAxisOrientation = 65534;
        BigDecimal expectedSemiMajorAxisOrientation = BigDecimal.valueOf(359.9945078786);

        Integer testTransmissionState = 6;
        String expectedTransmissionState = "RESERVED3";

        Integer testSpeed = 8190;
        BigDecimal expectedSpeed = BigDecimal.valueOf(163.80).setScale(2);

        Integer testHeading = 28799;
        BigDecimal expectedHeading = BigDecimal.valueOf(359.9875);

        Integer testSteeringWheelAngle = 126;
        BigDecimal expectedSteeringWheelAngle = BigDecimal.valueOf(189.0).setScale(1);

        // Acceleration set
        Integer testAccelLong = 2000;
        BigDecimal expectedAccelLong = BigDecimal.valueOf(20.00).setScale(2);

        Integer testAccelLat = 2000;
        BigDecimal expectedAccelLat = BigDecimal.valueOf(20.00).setScale(2);

        Integer testAccelVert = 127;
        BigDecimal expectedAccelVert = BigDecimal.valueOf(2.54);

        Integer testAccelYaw = 32767;
        BigDecimal expectedAccelYaw = BigDecimal.valueOf(327.67);

        // Brake system status
        byte[] testWheelBrakesBytes = new byte[1];
        testWheelBrakesBytes[0] = (byte) 0b10000000;
        String expectedBrakeAppliedStatus = "unavailable";

        Integer testTractionControlStatus = 3;
        String expectedTractionControlStatus = "engaged";

        Integer testAntiLockBrakeStatus = 3;
        String expectedAntiLockBrakeStatus = "engaged";

        Integer testStabilityControlStatus = 3;
        String expectedStabilityControlStatus = "engaged";

        Integer testBrakeBoostApplied = 2;
        String expectedBrakeBoostApplied = "on";

        Integer testAuxiliaryBrakeStatus = 3;
        String expectedAuxiliaryBrakeStatus = "reserved";

        // Vehicle size
        Integer testVehicleWidth = 1023;
        Integer expectedVehicleWidth = 1023;

        Integer testVehicleLength = 4095;
        Integer expectedVehicleLength = 4095;

        BSMcoreData testcd = new BSMcoreData();
        testcd.msgCnt = new MsgCount(testMsgCnt);
        testcd.id = new TemporaryID(new byte[] { testId.byteValue() });
        testcd.secMark = new DSecond(testSec);
        testcd.lat = new Latitude(testLat);
        testcd._long = new Longitude(testLong);
        testcd.elev = new Elevation(testElev);
        testcd.accuracy = new PositionalAccuracy(new SemiMajorAxisAccuracy(testSemiMajorAxisAccuracy),
                new SemiMinorAxisAccuracy(testSemiMinorAxisAccuracy),
                new SemiMajorAxisOrientation(testSemiMajorAxisOrientation));
        testcd.transmission = new TransmissionState(testTransmissionState);
        testcd.speed = new Speed(testSpeed);
        testcd.heading = new Heading(testHeading);
        testcd.angle = new SteeringWheelAngle(testSteeringWheelAngle);
        testcd.accelSet = new AccelerationSet4Way(new Acceleration(testAccelLong), new Acceleration(testAccelLat),
                new VerticalAcceleration(testAccelVert), new YawRate(testAccelYaw));
        testcd.brakes = new BrakeSystemStatus(new BrakeAppliedStatus(testWheelBrakesBytes),
                new TractionControlStatus(testTractionControlStatus), new AntiLockBrakeStatus(testAntiLockBrakeStatus),
                new StabilityControlStatus(testStabilityControlStatus), new BrakeBoostApplied(testBrakeBoostApplied),
                new AuxiliaryBrakeStatus(testAuxiliaryBrakeStatus));
        testcd.size = new VehicleSize(new VehicleWidth(testVehicleWidth), new VehicleLength(testVehicleLength));

        BasicSafetyMessage testbsm = new BasicSafetyMessage();
        testbsm.coreData = testcd;

        J2735Bsm actualbsm = null;
        try {
            actualbsm = OssBsm.genericBsm(testbsm);

            if (actualbsm.getCoreData() == null) {
                fail("Bsm core data failed to populate");
            }

        } catch (OssBsmPart2Exception e) {
            fail("Unexpected exception: " + e.getClass());
        }

        J2735BsmCoreData actualcd = actualbsm.getCoreData();

        assertEquals("Incorrect message count", expectedMsgCnt, actualcd.getMsgCnt());
        assertEquals("Incorrect message id", expectedId, actualcd.getId());
        assertEquals("Incorrect second", expectedSec, actualcd.getSecMark());
        assertEquals("Incorrect lat position", expectedLat, actualcd.getPosition().getLatitude());
        assertEquals("Incorrect long position", expectedLong, actualcd.getPosition().getLongitude());
        assertEquals("Incorrect elev position", expectedElev, actualcd.getPosition().getElevation());
        assertEquals("Incorrect semi major accuracy", expectedSemiMajorAxisAccuracy,
                actualcd.getAccuracy().getSemiMajor());
        assertEquals("Incorrect semi minor accuracy", expectedSemiMinorAxisAccuracy,
                actualcd.getAccuracy().getSemiMinor());
        assertEquals("Incorrect semi major orient", expectedSemiMajorAxisOrientation,
                actualcd.getAccuracy().getOrientation());
        assertEquals("Incorrect transmission state", expectedTransmissionState, actualcd.getTransmission().name());
        assertEquals("Incorrect speed", expectedSpeed, actualcd.getSpeed());
        assertEquals("Incorrect heading", expectedHeading, actualcd.getHeading());
        assertEquals("Incorrect steering wheel angle", expectedSteeringWheelAngle, actualcd.getAngle());
        assertEquals("Incorrect accel long", expectedAccelLong, actualcd.getAccelSet().getAccelLong());
        assertEquals("Incorrect accel lat", expectedAccelLat, actualcd.getAccelSet().getAccelLat());
        assertEquals("Incorrect accel vert", expectedAccelVert, actualcd.getAccelSet().getAccelVert());
        assertEquals("Incorrect accel yaw", expectedAccelYaw, actualcd.getAccelSet().getAccelYaw());
        for (Map.Entry<String, Boolean> curVal : actualcd.getBrakes().getWheelBrakes().entrySet()) {
            if (curVal.getKey() == expectedBrakeAppliedStatus) {
                assertTrue("Incorrect brake applied status, expected " + curVal.getKey() + " to be true",
                        curVal.getValue());
            } else {
                assertFalse("Incorrect brake applied status, expected " + curVal.getKey() + " to be false",
                        curVal.getValue());
            }
        }
        assertEquals("Incorrect brake tcs status", expectedTractionControlStatus, actualcd.getBrakes().getTraction());
        assertEquals("Incorrect brake abs status", expectedAntiLockBrakeStatus, actualcd.getBrakes().getAbs());
        assertEquals("Incorrect brake scs status", expectedStabilityControlStatus, actualcd.getBrakes().getScs());
        assertEquals("Incorrect brake boost status", expectedBrakeBoostApplied, actualcd.getBrakes().getBrakeBoost());
        assertEquals("Incorrect brake aux status", expectedAuxiliaryBrakeStatus, actualcd.getBrakes().getAuxBrakes());
        assertEquals("Incorrect vehicle width", expectedVehicleWidth, actualcd.getSize().getWidth());
        assertEquals("Incorrect vehicle length", expectedVehicleLength, actualcd.getSize().getLength());
    }

    @Test
    public void test_basicSafetyMessage(@Injectable J2735Bsm mockJ2735Bsm,
            @Mocked J2735BsmCoreData mockJ2735BsmCoreData, @Mocked J2735Position3D mockJ2735Position3D,
            @Mocked BigDecimal mockBigDecimal) {

        new Expectations() {
            {
                mockJ2735Bsm.getCoreData();
                result = mockJ2735BsmCoreData;

                mockJ2735BsmCoreData.getPosition();
                result = mockJ2735Position3D;
                mockJ2735Position3D.getLatitude();
                result = mockBigDecimal;
                mockJ2735Position3D.getLongitude();
                result = mockBigDecimal;
                mockJ2735Position3D.getElevation();
                result = mockBigDecimal;
                mockBigDecimal.longValue();
                result = anyLong;
            }
        };

        OssBsm.basicSafetyMessage(mockJ2735Bsm);
    }

    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, SecurityException  {
        Constructor<OssBsm> constructor = OssBsm.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        try {
           constructor.newInstance();
        } catch (Exception e) {
           assertTrue(e.getCause() instanceof UnsupportedOperationException);
        }
    }
}
