package us.dot.its.jpo.ode.vsdm;

import java.util.ArrayList;
import java.util.List;

import us.dot.its.jpo.ode.j2735.dsrc.BSMcoreData;
import us.dot.its.jpo.ode.j2735.dsrc.BasicSafetyMessage;
import us.dot.its.jpo.ode.j2735.dsrc.MsgCount;
import us.dot.its.jpo.ode.j2735.dsrc.PositionalAccuracy;
import us.dot.its.jpo.ode.j2735.dsrc.SemiMajorAxisAccuracy;
import us.dot.its.jpo.ode.j2735.dsrc.SemiMajorAxisOrientation;
import us.dot.its.jpo.ode.j2735.dsrc.SemiMinorAxisAccuracy;
import us.dot.its.jpo.ode.j2735.dsrc.Speed;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage;
import us.dot.its.jpo.ode.j2735.semi.VehSitRecord;

public class VsdToBsmConverter {

	private VsdToBsmConverter() {
		// hidden
	}

	public static List<BasicSafetyMessage> convert(VehSitDataMessage vsdm) {

		List<BasicSafetyMessage> bsmList = new ArrayList<>();

		for (VehSitRecord vsr : vsdm.bundle.elements) {
			BasicSafetyMessage bsm = new BasicSafetyMessage();
			bsm.coreData = createCoreData(vsr);
			bsmList.add(bsm);
		}

		return bsmList;
	}

	private static BSMcoreData createCoreData(VehSitRecord vsr) {

		BSMcoreData cd = new BSMcoreData();

		cd.lat = vsr.pos.lat;
		cd._long = vsr.pos._long;
		cd.accelSet = vsr.fundamental.accelSet;
		cd.accuracy = new PositionalAccuracy(new SemiMajorAxisAccuracy(255), new SemiMinorAxisAccuracy(255),
				new SemiMajorAxisOrientation(65535));
		cd.angle = vsr.fundamental.steeringAngle;
		cd.brakes = vsr.fundamental.brakes;
		cd.elev = vsr.pos.elevation;
		cd.heading = vsr.fundamental.heading;
		cd.id = vsr.tempID;
		cd.msgCnt = new MsgCount(0);
		cd.secMark = vsr.time.second;
		cd.size = vsr.fundamental.vehSize;
		cd.speed = new Speed(vsr.fundamental.speed.speed.intValue());
		cd.transmission = vsr.fundamental.speed.transmisson;

		return cd;
	}

}
