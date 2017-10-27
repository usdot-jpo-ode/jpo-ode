package us.dot.its.jpo.ode.udp.vsd;

//TODO open-ode
//import us.dot.its.jpo.ode.udp.vsd.VsdToBsmConverter;

public class VsdToBsmConverterTest {

   //TODO open-ode
//	VehSitDataMessage validVsd;
//
//	@Before
//	public void createTestVsd() {
//
//		validVsd = new VehSitDataMessage();
//		validVsd.bundle = new Bundle();
//		validVsd.bundle.elements = new ArrayList<>();
//		validVsd.crc = new MsgCRC(new byte[] { 1 });
//		validVsd.dialogID = new SemiDialogID(2);
//		validVsd.groupID = new GroupID(new byte[] { 3 });
//
//		VehSitRecord vsr = new VehSitRecord();
//		vsr.tempID = new TemporaryID();
//		vsr.time = new DDateTime();
//		vsr.pos = new Position3D();
//		vsr.pos._long = new Longitude(90);
//		vsr.pos.lat = new Latitude(90);
//		vsr.pos.elevation = new Elevation();
//		vsr.fundamental = new FundamentalSituationalStatus();
//		vsr.fundamental.accelSet = new AccelerationSet4Way();
//		vsr.fundamental.steeringAngle = new SteeringWheelAngle();
//		vsr.fundamental.brakes = new BrakeSystemStatus();
//		vsr.fundamental.heading = new Heading();
//		vsr.fundamental.vehSize = new VehicleSize();
//		vsr.fundamental.speed = new TransmissionAndSpeed();
//		vsr.fundamental.speed.speed = new Velocity(8191);
//		
//		validVsd.bundle.add(vsr);
//	}
//
//	@Test
//	public void shouldThrowExceptionBundleNull() {
//		VehSitDataMessage vsdm = new VehSitDataMessage();
//		try {
//			VsdToBsmConverter.convert(vsdm);
//			fail("Expected IllegalArgumentException.");
//		} catch (Exception e) {
//			assertTrue(e instanceof IllegalArgumentException);
//		}
//	}
//
//	@Test
//	public void shouldReturnEmptyListFromEmptyBundle() {
//		VehSitDataMessage vsdm = new VehSitDataMessage();
//		vsdm.bundle = new Bundle();
//		assertTrue(VsdToBsmConverter.convert(vsdm).size() == 0);
//	}
//
//	@Test
//	public void shouldConvertCorrectly() {
//		List<BasicSafetyMessage> bsmlist = VsdToBsmConverter.convert(validVsd);
//		assertTrue(bsmlist.size() == 1);
//		
//		BasicSafetyMessage bsm = bsmlist.get(0);
//		assertTrue(bsm.coreData._long.intValue() == 90);
//	}

}
