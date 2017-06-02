package us.dot.its.jpo.ode.bsm;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

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
import us.dot.its.jpo.ode.j2735.semi.ServiceRequest;

public class BsmToVsdConverterTest {

	BasicSafetyMessage testbsm;

	@Before
	public void setUp() {
		// Create a sample bsm

		Integer testMsgCnt = 127;

		Integer testId = 0b11111111111111111111111111111111;

		Integer testSec = 65534;

		Integer testLat = 900000000;

		Integer testLong = 1800000000;

		Integer testElev = 61439;

		// Positional accuracy
		Integer testSemiMajorAxisAccuracy = 254;

		Integer testSemiMinorAxisAccuracy = 254;

		Integer testSemiMajorAxisOrientation = 65534;

		Integer testTransmissionState = 6;

		Integer testSpeed = 8190;

		Integer testHeading = 28799;

		Integer testSteeringWheelAngle = 126;

		// Acceleration set
		Integer testAccelLong = 2000;

		Integer testAccelLat = 2000;

		Integer testAccelVert = 127;

		Integer testAccelYaw = 32767;

		// Brake system status
		byte[] testWheelBrakesBytes = new byte[1];
		testWheelBrakesBytes[0] = (byte) 0b10000000;

		Integer testTractionControlStatus = 3;

		Integer testAntiLockBrakeStatus = 3;

		Integer testStabilityControlStatus = 3;

		Integer testBrakeBoostApplied = 2;

		Integer testAuxiliaryBrakeStatus = 3;

		// Vehicle size
		Integer testVehicleWidth = 1023;

		Integer testVehicleLength = 4095;

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

		testbsm = new BasicSafetyMessage();
		testbsm.coreData = testcd;
	}

	@Test
	public void testConverter() {

		ServiceRequest testsr = new ServiceRequest();

		ArrayList<BasicSafetyMessage> testBsmArray = new ArrayList<>();
		testBsmArray.add(testbsm);

		BsmToVsdConverter.convertBsmToVsd(testsr, testBsmArray);
	}

	@Test
	public void testConverterTooManyItems() {
		// Test what happens when too many items are added to the list
		ServiceRequest testsr = new ServiceRequest();

		ArrayList<BasicSafetyMessage> testBsmArray = new ArrayList<>();

		for (int i = 0; i < 11; i++) {
			testBsmArray.add(testbsm);
		}

		try {
			BsmToVsdConverter.convertBsmToVsd(testsr, testBsmArray);
			fail("Expected IllegalArgumentException");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}

	}

}
