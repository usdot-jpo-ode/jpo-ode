package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.Test;

import com.oss.asn1.OpenType;

import us.dot.its.jpo.ode.j2735.dsrc.Acceleration;
import us.dot.its.jpo.ode.j2735.dsrc.AccelerationSet4Way;
import us.dot.its.jpo.ode.j2735.dsrc.AntiLockBrakeStatus;
import us.dot.its.jpo.ode.j2735.dsrc.AuxiliaryBrakeStatus;
import us.dot.its.jpo.ode.j2735.dsrc.BSMcoreData;
import us.dot.its.jpo.ode.j2735.dsrc.BasicSafetyMessage;
import us.dot.its.jpo.ode.j2735.dsrc.BrakeAppliedStatus;
import us.dot.its.jpo.ode.j2735.dsrc.BrakeBoostApplied;
import us.dot.its.jpo.ode.j2735.dsrc.BrakeSystemStatus;
import us.dot.its.jpo.ode.j2735.dsrc.DSRCmsgID;
import us.dot.its.jpo.ode.j2735.dsrc.DSecond;
import us.dot.its.jpo.ode.j2735.dsrc.Elevation;
import us.dot.its.jpo.ode.j2735.dsrc.Heading;
import us.dot.its.jpo.ode.j2735.dsrc.Latitude;
import us.dot.its.jpo.ode.j2735.dsrc.Longitude;
import us.dot.its.jpo.ode.j2735.dsrc.MessageFrame;
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

public class OssMessageFrameTest {

    @Test
    public void shouldCreateMessageFrame() {
        
        MessageFrame testMessageFrame = new MessageFrame();
        testMessageFrame.setMessageId(new DSRCmsgID(20));
        
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
        String expectedTransmissionState = "reserved3";
        
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
        testcd.id = new TemporaryID(new byte[]{testId.byteValue()});
        testcd.secMark = new DSecond(testSec);
        testcd.lat = new Latitude(testLat);
        testcd._long = new Longitude(testLong);
        testcd.elev = new Elevation(testElev);
        testcd.accuracy = new PositionalAccuracy(
                new SemiMajorAxisAccuracy(testSemiMajorAxisAccuracy),
                new SemiMinorAxisAccuracy(testSemiMinorAxisAccuracy),
                new SemiMajorAxisOrientation(testSemiMajorAxisOrientation));
        testcd.transmission = new TransmissionState(testTransmissionState);
        testcd.speed = new Speed(testSpeed);
        testcd.heading = new Heading(testHeading);
        testcd.angle = new SteeringWheelAngle(testSteeringWheelAngle);
        testcd.accelSet = new AccelerationSet4Way(
                new Acceleration(testAccelLong),
                new Acceleration(testAccelLat),
                new VerticalAcceleration(testAccelVert),
                new YawRate(testAccelYaw));
        testcd.brakes = new BrakeSystemStatus(
                new BrakeAppliedStatus(testWheelBrakesBytes),
                new TractionControlStatus(testTractionControlStatus),
                new AntiLockBrakeStatus(testAntiLockBrakeStatus),
                new StabilityControlStatus(testStabilityControlStatus),
                new BrakeBoostApplied(testBrakeBoostApplied),
                new AuxiliaryBrakeStatus(testAuxiliaryBrakeStatus));
        testcd.size = new VehicleSize(
                new VehicleWidth(testVehicleWidth),
                new VehicleLength(testVehicleLength));
        
        BasicSafetyMessage testbsm = new BasicSafetyMessage();
        testbsm.coreData = testcd;
        
        
        OpenType tot = new OpenType();
        tot.setDecodedValue(testbsm);

        testMessageFrame.setValue(tot);
        
        J2735Bsm actualbsm = null;
        try {
            actualbsm = OssMessageFrame.genericMessageFrame(testMessageFrame).getValue();
            
            if (actualbsm.coreData == null) {
                fail("Bsm core data failed to populate");
            }
            
        } catch (Exception e) {
            fail("Unexpected exception: " + e);
        }
        
        J2735BsmCoreData actualcd = actualbsm.coreData;
        
        assertEquals("Incorrect message count", expectedMsgCnt, actualcd.msgCnt);
        assertEquals("Incorrect message id", expectedId, actualcd.id);
        assertEquals("Incorrect second", expectedSec, actualcd.secMark);
        assertEquals("Incorrect lat position", expectedLat, actualcd.position.getLatitude());
        assertEquals("Incorrect long position", expectedLong, actualcd.position.getLongitude());
        assertEquals("Incorrect elev position", expectedElev, actualcd.position.getElevation());
        assertEquals("Incorrect semi major accuracy", expectedSemiMajorAxisAccuracy, actualcd.accuracy.semiMajor);
        assertEquals("Incorrect semi minor accuracy", expectedSemiMinorAxisAccuracy, actualcd.accuracy.semiMinor);
        assertEquals("Incorrect semi major orient", expectedSemiMajorAxisOrientation, actualcd.accuracy.orientation);
        assertEquals("Incorrect transmission state", expectedTransmissionState, actualcd.transmission.name());
        assertEquals("Incorrect speed", expectedSpeed, actualcd.speed);
        assertEquals("Incorrect heading", expectedHeading, actualcd.heading);
        assertEquals("Incorrect steering wheel angle", expectedSteeringWheelAngle, actualcd.angle);
        assertEquals("Incorrect accel long", expectedAccelLong, actualcd.accelSet.getAccelLong());
        assertEquals("Incorrect accel lat", expectedAccelLat, actualcd.accelSet.getAccelLat());
        assertEquals("Incorrect accel vert", expectedAccelVert, actualcd.accelSet.getAccelVert());
        assertEquals("Incorrect accel yaw", expectedAccelYaw, actualcd.accelSet.getAccelYaw());
        for (Map.Entry<String, Boolean> curVal: actualcd.brakes.getWheelBrakes().entrySet()) {
            if (curVal.getKey() == expectedBrakeAppliedStatus) {
                assertTrue("Incorrect brake applied status, expected " + curVal.getKey() + " to be true", curVal.getValue());
            } else {
                assertFalse("Incorrect brake applied status, expected " + curVal.getKey() + " to be false", curVal.getValue());
            }
        }
        assertEquals("Incorrect brake tcs status", expectedTractionControlStatus, actualcd.brakes.getTraction());
        assertEquals("Incorrect brake abs status", expectedAntiLockBrakeStatus, actualcd.brakes.getAbs());
        assertEquals("Incorrect brake scs status", expectedStabilityControlStatus, actualcd.brakes.getScs());
        assertEquals("Incorrect brake boost status", expectedBrakeBoostApplied, actualcd.brakes.getBrakeBoost());
        assertEquals("Incorrect brake aux status", expectedAuxiliaryBrakeStatus, actualcd.brakes.getAuxBrakes());
        assertEquals("Incorrect vehicle width", expectedVehicleWidth, actualcd.size.getWidth());
        assertEquals("Incorrect vehicle length", expectedVehicleLength, actualcd.size.getLength());
    }


}
