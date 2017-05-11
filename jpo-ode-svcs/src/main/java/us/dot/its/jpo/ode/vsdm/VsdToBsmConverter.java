package us.dot.its.jpo.ode.vsdm;

import java.util.ArrayList;
import java.util.List;

import us.dot.its.jpo.ode.j2735.dsrc.BSMcoreData;
import us.dot.its.jpo.ode.j2735.dsrc.BasicSafetyMessage;
import us.dot.its.jpo.ode.j2735.dsrc.Speed;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage;
import us.dot.its.jpo.ode.j2735.semi.VehSitRecord;

public class VsdToBsmConverter {
	
	private VsdToBsmConverter () {
		// hidden
	}
	
	public static List<BasicSafetyMessage> convert(VehSitDataMessage vsdm) {
		
		List<BasicSafetyMessage> bsmList = new ArrayList<>();
		
		for (VehSitRecord vsr : vsdm.bundle.elements) {
			BasicSafetyMessage bsm = new BasicSafetyMessage();
			bsm.coreData = createCoreData(vsr);
		}
		
		return bsmList;	
	}
	
	private static BSMcoreData createCoreData(VehSitRecord vsr) {
		
		BSMcoreData cd = new BSMcoreData();
		
		cd.lat = vsr.pos.lat;
		cd._long = vsr.pos._long;
		cd.accelSet = vsr.fundamental.accelSet;
		// cd.accuracy = ?
		cd.angle = vsr.fundamental.steeringAngle;
		cd.brakes = vsr.fundamental.brakes;
		cd.elev = vsr.pos.elevation;
		cd.heading = vsr.fundamental.heading;
		cd.id = vsr.tempID;
		// cd.msgCnt = ?
		cd.secMark = vsr.time.second;
		cd.size = vsr.fundamental.vehSize;
		cd.speed = new Speed(vsr.fundamental.speed.speed.intValue());
		cd.transmission = vsr.fundamental.speed.transmisson;
		
		return cd;
	}

}
