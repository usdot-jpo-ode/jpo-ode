package us.dot.its.jpo.ode.udp.vsd;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import us.dot.its.jpo.ode.j2735.dsrc.AccelerationSet4Way;
import us.dot.its.jpo.ode.j2735.dsrc.BasicSafetyMessage;
import us.dot.its.jpo.ode.j2735.dsrc.BrakeSystemStatus;
import us.dot.its.jpo.ode.j2735.dsrc.DDateTime;
import us.dot.its.jpo.ode.j2735.dsrc.Elevation;
import us.dot.its.jpo.ode.j2735.dsrc.Heading;
import us.dot.its.jpo.ode.j2735.dsrc.Latitude;
import us.dot.its.jpo.ode.j2735.dsrc.Longitude;
import us.dot.its.jpo.ode.j2735.dsrc.MsgCRC;
import us.dot.its.jpo.ode.j2735.dsrc.Position3D;
import us.dot.its.jpo.ode.j2735.dsrc.SteeringWheelAngle;
import us.dot.its.jpo.ode.j2735.dsrc.TemporaryID;
import us.dot.its.jpo.ode.j2735.dsrc.TransmissionAndSpeed;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleSize;
import us.dot.its.jpo.ode.j2735.dsrc.Velocity;
import us.dot.its.jpo.ode.j2735.semi.FundamentalSituationalStatus;
import us.dot.its.jpo.ode.j2735.semi.GroupID;
import us.dot.its.jpo.ode.j2735.semi.SemiDialogID;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage.Bundle;
import us.dot.its.jpo.ode.udp.vsd.VsdToBsmConverter;
import us.dot.its.jpo.ode.j2735.semi.VehSitRecord;

public class VsdToBsmConverterTest {

	VehSitDataMessage validVsd;

	@Before
	public void createTestVsd() {

		validVsd = new VehSitDataMessage();
		validVsd.bundle = new Bundle();
		validVsd.bundle.elements = new ArrayList<>();
		validVsd.crc = new MsgCRC(new byte[] { 1 });
		validVsd.dialogID = new SemiDialogID(2);
		validVsd.groupID = new GroupID(new byte[] { 3 });

		VehSitRecord vsr = new VehSitRecord();
		vsr.tempID = new TemporaryID();
		vsr.time = new DDateTime();
		vsr.pos = new Position3D();
		vsr.pos._long = new Longitude(90);
		vsr.pos.lat = new Latitude(90);
		vsr.pos.elevation = new Elevation();
		vsr.fundamental = new FundamentalSituationalStatus();
		vsr.fundamental.accelSet = new AccelerationSet4Way();
		vsr.fundamental.steeringAngle = new SteeringWheelAngle();
		vsr.fundamental.brakes = new BrakeSystemStatus();
		vsr.fundamental.heading = new Heading();
		vsr.fundamental.vehSize = new VehicleSize();
		vsr.fundamental.speed = new TransmissionAndSpeed();
		vsr.fundamental.speed.speed = new Velocity(8191);
		
		validVsd.bundle.add(vsr);
	}

	@Test
	public void shouldThrowExceptionBundleNull() {
		VehSitDataMessage vsdm = new VehSitDataMessage();
		try {
			VsdToBsmConverter.convert(vsdm);
			fail("Expected IllegalArgumentException.");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
	}

	@Test
	public void shouldReturnEmptyListFromEmptyBundle() {
		VehSitDataMessage vsdm = new VehSitDataMessage();
		vsdm.bundle = new Bundle();
		assertTrue(VsdToBsmConverter.convert(vsdm).size() == 0);
	}

	@Test
	public void shouldConvertCorrectly() {
		List<BasicSafetyMessage> bsmlist = VsdToBsmConverter.convert(validVsd);
		assertTrue(bsmlist.size() == 1);
		
		BasicSafetyMessage bsm = bsmlist.get(0);
		assertTrue(bsm.coreData._long.intValue() == 90);
	}

}
