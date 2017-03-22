package us.dot.its.jpo.ode.plugin.j2735.oss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.Acceleration;
import us.dot.its.jpo.ode.j2735.dsrc.AccelerationSet4Way;
import us.dot.its.jpo.ode.j2735.dsrc.AntiLockBrakeStatus;
import us.dot.its.jpo.ode.j2735.dsrc.AuxiliaryBrakeStatus;
import us.dot.its.jpo.ode.j2735.dsrc.BSMcoreData;
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
import us.dot.its.jpo.ode.plugin.j2735.J2735BsmCoreData;

/**
 * -- Summary --
 * JUnit test class for OssBsmCoreData
 * 
 * Verifies correct conversion from generic BSMcoreData to compliant-J2735BsmCoreData
 * 
 * -- Documentation --
 * Data Frame: DF_BSMcoreData
 * Use: The DF_BSMcoreData data frame contains the critical core data elements deemed to be needed with every BSM 
 * issued. This data frame’s contents are often referred to as the "BSM Part One", although it is reused in other 
 * places as well.
 * ASN.1 Representation:
 *    BSMcoreData ::= SEQUENCE {
 *       msgCnt MsgCount,
 *       id TemporaryID,
 *       secMark DSecond,
 *       lat Latitude,
 *       long Longitude,
 *       elev Elevation,
 *       accuracy PositionalAccuracy,
 *       transmission TransmissionState,
 *       speed Speed,
 *       heading Heading,
 *       angle SteeringWheelAngle,
 *       accelSet AccelerationSet4Way,
 *       brakes BrakeSystemStatus,
 *       size VehicleSize
 *       }
 *
 */
public class OssBsmCoreDataTest {

    /**
     * Test creating a BSM with values that flag undefined or unavailable data
     * (values without explicit undefined flags are set to minimum value)
     */
    @Test
    public void shouldCreateCoreDataUndefinedValues() {
        
        Integer testMsgCnt = 0;
        Integer expectedMsgCnt = 0;
        
        Integer testId = 0;
        String expectedId = "00";
        
        Integer testSec = 65535;
        Integer expectedSec = null;
        
        Integer testLat = 900000001;
        BigDecimal expectedLat = null;
        
        Integer testLong = 1800000001;
        BigDecimal expectedLong = null;
        
        Integer testElev = -4096;
        BigDecimal expectedElev = null;
        
        // Positional accuracy
        Integer testSemiMajorAxisAccuracy = 255;
        BigDecimal expectedSemiMajorAxisAccuracy = null;
        
        Integer testSemiMinorAxisAccuracy = 255;
        BigDecimal expectedSemiMinorAxisAccuracy = null;
        
        Integer testSemiMajorAxisOrientation = 65535;
        BigDecimal expectedSemiMajorAxisOrientation = null;
        
        
        Integer testTransmissionState = 7;
        String expectedTransmissionState = null;
        
        Integer testSpeed = 8191;
        BigDecimal expectedSpeed = null;
        
        Integer testHeading = 28800;
        BigDecimal expectedHeading = null;
        
        Integer testSteeringWheelAngle = 127;
        BigDecimal expectedSteeringWheelAngle = null;
        
        // Acceleration set 
        Integer testAccelLong = 2001;
        BigDecimal expectedAccelLong = null;
        
        Integer testAccelLat = 2001;
        BigDecimal expectedAccelLat = null;
        
        Integer testAccelVert = -127;
        BigDecimal expectedAccelVert = null;
        
        Integer testAccelYaw = -32767;
        BigDecimal expectedAccelYaw = BigDecimal.valueOf(-327.67);
        
        // Brake system status
        byte[] testBrakeAppliedStatusBytes = new byte[1]; 
        testBrakeAppliedStatusBytes[0] = (byte) 0b10000000;
        String expectedBrakeAppliedStatus = "unavailable";
        
        Integer testTractionControlStatus = 0;
        String expectedTractionControlStatus = "unavailable";
        
        Integer testAntiLockBrakeStatus = 0;
        String expectedAntiLockBrakeStatus = "unavailable";
        
        Integer testStabilityControlStatus = 0;
        String expectedStabilityControlStatus = "unavailable";
        
        Integer testBrakeBoostApplied = 0;
        String expectedBrakeBoostApplied = "unavailable";
        
        Integer testAuxiliaryBrakeStatus = 0;
        String expectedAuxiliaryBrakeStatus = "unavailable";
        
        // Vehicle size
        Integer testVehicleWidth = 0;
        Integer expectedVehicleWidth = null;
        
        Integer testVehicleLength = 0;
        Integer expectedVehicleLength = null;
        
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
                new BrakeAppliedStatus(testBrakeAppliedStatusBytes),
                new TractionControlStatus(testTractionControlStatus),
                new AntiLockBrakeStatus(testAntiLockBrakeStatus),
                new StabilityControlStatus(testStabilityControlStatus),
                new BrakeBoostApplied(testBrakeBoostApplied),
                new AuxiliaryBrakeStatus(testAuxiliaryBrakeStatus));
        testcd.size = new VehicleSize(
                new VehicleWidth(testVehicleWidth),
                new VehicleLength(testVehicleLength));
        
        J2735BsmCoreData actualcd = OssBsmCoreData.genericBsmCoreData(testcd);
        
        assertEquals("Incorrect message count", expectedMsgCnt, actualcd.getMsgCnt());
        assertEquals("Incorrect message id", expectedId, actualcd.getId());
        assertEquals("Incorrect second", expectedSec, actualcd.getSecMark());
        assertEquals("Incorrect lat position", expectedLat, actualcd.getPosition().getLatitude());
        assertEquals("Incorrect long position", expectedLong, actualcd.getPosition().getLongitude());
        assertEquals("Incorrect elev position", expectedElev, actualcd.getPosition().getElevation());
        assertEquals("Incorrect semi major accuracy", expectedSemiMajorAxisAccuracy, actualcd.getAccuracy().getSemiMajor());
        assertEquals("Incorrect semi minor accuracy", expectedSemiMinorAxisAccuracy, actualcd.getAccuracy().getSemiMinor());
        assertEquals("Incorrect semi major orient", expectedSemiMajorAxisOrientation, actualcd.getAccuracy().getOrientation());
        assertEquals("Incorrect transmission state", expectedTransmissionState, actualcd.getTransmission());
        assertEquals("Incorrect speed", expectedSpeed, actualcd.getSpeed());
        assertEquals("Incorrect heading", expectedHeading, actualcd.getHeading());
        assertEquals("Incorrect steering wheel angle", expectedSteeringWheelAngle, actualcd.getAngle());
        assertEquals("Incorrect accel long", expectedAccelLong, actualcd.getAccelSet().getAccelLong());
        assertEquals("Incorrect accel lat", expectedAccelLat, actualcd.getAccelSet().getAccelLat());
        assertEquals("Incorrect accel vert", expectedAccelVert, actualcd.getAccelSet().getAccelVert());
        assertEquals("Incorrect accel yaw", expectedAccelYaw, actualcd.getAccelSet().getAccelYaw());
        for (Map.Entry<String, Boolean> curVal: actualcd.getBrakes().getWheelBrakes().entrySet()) {
            if (curVal.getKey() == expectedBrakeAppliedStatus) {
                assertTrue("Incorrect brake applied status, expected " + curVal.getKey() + " to be true", curVal.getValue());
            } else {
                assertFalse("Incorrect brake applied status, expected " + curVal.getKey() + " to be false", curVal.getValue());
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

    
    /**
     * Test creating BSM with all values set to their minimum value
     * (values whose minimum value is an undefined flag are set to one unit greater)
     */
    @Test
    public void shouldCreateCoreDataMinimumValues() {
        
        Integer testMsgCnt = 1;
        Integer expectedMsgCnt = 1;
        
        Integer testId = 1;
        String expectedId = "01";
        
        Integer testSec = 0;
        Integer expectedSec = 0;
        
        Integer testLat = -900000000;
        BigDecimal expectedLat = BigDecimal.valueOf(-90.0000000).setScale(7);
        
        Integer testLong = -1799999999;
        BigDecimal expectedLong = BigDecimal.valueOf(-179.9999999);
        
        Integer testElev = -4095;
        BigDecimal expectedElev = BigDecimal.valueOf(-409.5);
        
        // Positional accuracy
        Integer testSemiMajorAxisAccuracy = 0;
        BigDecimal expectedSemiMajorAxisAccuracy = BigDecimal.ZERO.setScale(2);
        
        Integer testSemiMinorAxisAccuracy = 0;
        BigDecimal expectedSemiMinorAxisAccuracy = BigDecimal.ZERO.setScale(2);
        
        Integer testSemiMajorAxisOrientation = 0;
        BigDecimal expectedSemiMajorAxisOrientation = BigDecimal.ZERO.setScale(10);
        
        
        Integer testTransmissionState = 0;
        String expectedTransmissionState = "neutral";
        
        Integer testSpeed = 0;
        BigDecimal expectedSpeed = BigDecimal.ZERO.setScale(2);
        
        Integer testHeading = 0;
        BigDecimal expectedHeading = BigDecimal.ZERO.setScale(4);
        
        Integer testSteeringWheelAngle = -126;
        BigDecimal expectedSteeringWheelAngle = BigDecimal.valueOf(-189.0).setScale(1);
        
        // Acceleration set 
        Integer testAccelLong = -2000;
        BigDecimal expectedAccelLong = BigDecimal.valueOf(-20.00).setScale(2);
        
        Integer testAccelLat = -2000;
        BigDecimal expectedAccelLat = BigDecimal.valueOf(-20.00).setScale(2);
        
        Integer testAccelVert = -126;
        BigDecimal expectedAccelVert = BigDecimal.valueOf(-2.52);
        
        Integer testAccelYaw = -32767;
        BigDecimal expectedAccelYaw = BigDecimal.valueOf(-327.67);
        
        // Brake system status
        byte[] testBrakeAppliedStatusBytes = new byte[1]; 
        testBrakeAppliedStatusBytes[0] = (byte) 0b01000000;
        String expectedBrakeAppliedStatus = "leftFront";
        
        Integer testTractionControlStatus = 1;
        String expectedTractionControlStatus = "off";
        
        Integer testAntiLockBrakeStatus = 1;
        String expectedAntiLockBrakeStatus = "off";
        
        Integer testStabilityControlStatus = 1;
        String expectedStabilityControlStatus = "off";
        
        Integer testBrakeBoostApplied = 1;
        String expectedBrakeBoostApplied = "off";
        
        Integer testAuxiliaryBrakeStatus = 1;
        String expectedAuxiliaryBrakeStatus = "off";
        
        // Vehicle size
        Integer testVehicleWidth = 1;
        Integer expectedVehicleWidth = 1;
        
        Integer testVehicleLength = 1;
        Integer expectedVehicleLength = 1;
        
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
                new BrakeAppliedStatus(testBrakeAppliedStatusBytes),
                new TractionControlStatus(testTractionControlStatus),
                new AntiLockBrakeStatus(testAntiLockBrakeStatus),
                new StabilityControlStatus(testStabilityControlStatus),
                new BrakeBoostApplied(testBrakeBoostApplied),
                new AuxiliaryBrakeStatus(testAuxiliaryBrakeStatus));
        testcd.size = new VehicleSize(
                new VehicleWidth(testVehicleWidth),
                new VehicleLength(testVehicleLength));
        
        J2735BsmCoreData actualcd = OssBsmCoreData.genericBsmCoreData(testcd);
        
        assertEquals("Incorrect message count", expectedMsgCnt, actualcd.getMsgCnt());
        assertEquals("Incorrect message id", expectedId, actualcd.getId());
        assertEquals("Incorrect second", expectedSec, actualcd.getSecMark());
        assertEquals("Incorrect lat position", expectedLat, actualcd.getPosition().getLatitude());
        assertEquals("Incorrect long position", expectedLong, actualcd.getPosition().getLongitude());
        assertEquals("Incorrect elev position", expectedElev, actualcd.getPosition().getElevation());
        assertEquals("Incorrect semi major accuracy", expectedSemiMajorAxisAccuracy, actualcd.getAccuracy().getSemiMajor());
        assertEquals("Incorrect semi minor accuracy", expectedSemiMinorAxisAccuracy, actualcd.getAccuracy().getSemiMinor());
        assertEquals("Incorrect semi major orient", expectedSemiMajorAxisOrientation, actualcd.getAccuracy().getOrientation());
        assertEquals("Incorrect transmission state", expectedTransmissionState, actualcd.getTransmission().name());
        assertEquals("Incorrect speed", expectedSpeed, actualcd.getSpeed());
        assertEquals("Incorrect heading", expectedHeading, actualcd.getHeading());
        assertEquals("Incorrect steering wheel angle", expectedSteeringWheelAngle, actualcd.getAngle());
        assertEquals("Incorrect accel long", expectedAccelLong, actualcd.getAccelSet().getAccelLong());
        assertEquals("Incorrect accel lat", expectedAccelLat, actualcd.getAccelSet().getAccelLat());
        assertEquals("Incorrect accel vert", expectedAccelVert, actualcd.getAccelSet().getAccelVert());
        assertEquals("Incorrect accel yaw", expectedAccelYaw, actualcd.getAccelSet().getAccelYaw());
        for (Map.Entry<String, Boolean> curVal: actualcd.getBrakes().getWheelBrakes().entrySet()) {
            if (curVal.getKey() == expectedBrakeAppliedStatus) {
                assertTrue("Incorrect brake applied status, expected " + curVal.getKey() + " to be true", curVal.getValue());
            } else {
                assertFalse("Incorrect brake applied status, expected " + curVal.getKey() + " to be false", curVal.getValue());
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
    
    /**
     * Test creating BSM with all values set to their maximum value
     * (values whose maximum value is an undefined flag are set to one unit less)
     */
    @Test
    public void shouldCreateCoreDataMaximumValues() {
        
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
        
        byte[] testBrakeAppliedStatusBytes = new byte[1]; 
        testBrakeAppliedStatusBytes[0] = (byte) 0b00001000;
        String expectedBrakeAppliedStatus = "rightRear";
        
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
                new BrakeAppliedStatus(testBrakeAppliedStatusBytes),
                new TractionControlStatus(testTractionControlStatus),
                new AntiLockBrakeStatus(testAntiLockBrakeStatus),
                new StabilityControlStatus(testStabilityControlStatus),
                new BrakeBoostApplied(testBrakeBoostApplied),
                new AuxiliaryBrakeStatus(testAuxiliaryBrakeStatus));
        testcd.size = new VehicleSize(
                new VehicleWidth(testVehicleWidth),
                new VehicleLength(testVehicleLength));
        
        J2735BsmCoreData actualcd = OssBsmCoreData.genericBsmCoreData(testcd);
        
        assertEquals("Incorrect message count", expectedMsgCnt, actualcd.getMsgCnt());
        assertEquals("Incorrect message id", expectedId, actualcd.getId());
        assertEquals("Incorrect second", expectedSec, actualcd.getSecMark());
        assertEquals("Incorrect lat position", expectedLat, actualcd.getPosition().getLatitude());
        assertEquals("Incorrect long position", expectedLong, actualcd.getPosition().getLongitude());
        assertEquals("Incorrect elev position", expectedElev, actualcd.getPosition().getElevation());
        assertEquals("Incorrect semi major accuracy", expectedSemiMajorAxisAccuracy, actualcd.getAccuracy().getSemiMajor());
        assertEquals("Incorrect semi minor accuracy", expectedSemiMinorAxisAccuracy, actualcd.getAccuracy().getSemiMinor());
        assertEquals("Incorrect semi major orient", expectedSemiMajorAxisOrientation, actualcd.getAccuracy().getOrientation());
        assertEquals("Incorrect transmission state", expectedTransmissionState, actualcd.getTransmission().name());
        assertEquals("Incorrect speed", expectedSpeed, actualcd.getSpeed());
        assertEquals("Incorrect heading", expectedHeading, actualcd.getHeading());
        assertEquals("Incorrect steering wheel angle", expectedSteeringWheelAngle, actualcd.getAngle());
        assertEquals("Incorrect accel long", expectedAccelLong, actualcd.getAccelSet().getAccelLong());
        assertEquals("Incorrect accel lat", expectedAccelLat, actualcd.getAccelSet().getAccelLat());
        assertEquals("Incorrect accel vert", expectedAccelVert, actualcd.getAccelSet().getAccelVert());
        assertEquals("Incorrect accel yaw", expectedAccelYaw, actualcd.getAccelSet().getAccelYaw());
        for (Map.Entry<String, Boolean> curVal: actualcd.getBrakes().getWheelBrakes().entrySet()) {
            if (curVal.getKey() == expectedBrakeAppliedStatus) {
                assertTrue("Incorrect brake applied status, expected " + curVal.getKey() + " to be true", curVal.getValue());
            } else {
                assertFalse("Incorrect brake applied status, expected " + curVal.getKey() + " to be false", curVal.getValue());
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

}
