package us.dot.its.jpo.ode.plugins.oss.j2735;

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

public class OssBsm {
	public static BasicSafetyMessage basicSafetyMessage(J2735Bsm genericBsm) {
		BasicSafetyMessage basicSafetyMessage = new BasicSafetyMessage();
		
		J2735BsmCoreData coreData = genericBsm.coreData;
		
		basicSafetyMessage.coreData = new BSMcoreData();
		basicSafetyMessage.coreData.msgCnt = new MsgCount(1);
		basicSafetyMessage.coreData.id = new TemporaryID("1234".getBytes());
		basicSafetyMessage.coreData.secMark = new DSecond(1);
		basicSafetyMessage.coreData.setLat(new Latitude(coreData.position.getLatitude().longValue()));
		basicSafetyMessage.coreData.set_long(new Longitude(coreData.position.getLongitude().longValue()));
		basicSafetyMessage.coreData.setElev(new Elevation(coreData.position.getElevation().longValue()));
		
		basicSafetyMessage.coreData.accuracy = new PositionalAccuracy(new SemiMajorAxisAccuracy(0),
				new SemiMinorAxisAccuracy(0), 
				new SemiMajorAxisOrientation(0));

		basicSafetyMessage.coreData.transmission = new TransmissionState(0);

		basicSafetyMessage.coreData.speed = new Speed(10);
		
		basicSafetyMessage.coreData.heading = new Heading(0);
		
		basicSafetyMessage.coreData.angle = new SteeringWheelAngle(0);
		
		basicSafetyMessage.coreData.accelSet = new AccelerationSet4Way(new Acceleration(0), 
				new Acceleration(0), 
				new VerticalAcceleration(0), 
				new YawRate(0));
		
		basicSafetyMessage.coreData.brakes = new BrakeSystemStatus();
		basicSafetyMessage.coreData.brakes.wheelBrakes = new BrakeAppliedStatus();
		basicSafetyMessage.coreData.brakes.traction = new TractionControlStatus(0);
		basicSafetyMessage.coreData.brakes.abs = new AntiLockBrakeStatus(0);
		basicSafetyMessage.coreData.brakes.scs = new StabilityControlStatus(0);
		basicSafetyMessage.coreData.brakes.brakeBoost = new BrakeBoostApplied(0);
		basicSafetyMessage.coreData.brakes.auxBrakes = new AuxiliaryBrakeStatus(0);
		
		basicSafetyMessage.coreData.size = new VehicleSize(new VehicleWidth(10), new VehicleLength(10));
		
//		bsm.partII = new PartII();
//		bsm.partII.add(element);

		return basicSafetyMessage;
	}

	public static J2735Bsm genericBsm(BasicSafetyMessage basicSafetyMessage) {
		J2735Bsm genericBsm = new J2735Bsm();
		if (basicSafetyMessage.coreData != null) {
			genericBsm.coreData = 
				OssBsmCoreData.genericBsmCoreData(basicSafetyMessage.coreData);
		}
		
		if (basicSafetyMessage.partII != null) {
			OssBsmPart2Content.buildGenericPart2(
					basicSafetyMessage.partII.elements,
					genericBsm.partII);
		}
		
		return genericBsm;
	}

}
