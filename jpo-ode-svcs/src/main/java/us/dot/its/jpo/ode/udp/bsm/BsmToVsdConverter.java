package us.dot.its.jpo.ode.udp.bsm;

import java.util.List;

import us.dot.its.jpo.ode.j2735.dsrc.BSMcoreData;
import us.dot.its.jpo.ode.j2735.dsrc.BasicSafetyMessage;
import us.dot.its.jpo.ode.j2735.dsrc.DDateTime;
import us.dot.its.jpo.ode.j2735.dsrc.Position3D;
import us.dot.its.jpo.ode.j2735.dsrc.TransmissionAndSpeed;
import us.dot.its.jpo.ode.j2735.dsrc.Velocity;
import us.dot.its.jpo.ode.j2735.semi.Environmental;
import us.dot.its.jpo.ode.j2735.semi.FundamentalSituationalStatus;
import us.dot.its.jpo.ode.j2735.semi.ServiceRequest;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage.Bundle;
import us.dot.its.jpo.ode.j2735.semi.VehSitRecord;
import us.dot.its.jpo.ode.j2735.semi.VehicleSituationStatus;
import us.dot.its.jpo.ode.j2735.semi.Weather;

public class BsmToVsdConverter {

	private static final int MAX_LENGTH_BSM_LIST = 10;

	private BsmToVsdConverter() {
	}

	/**
	 * Convert an array of BasicSafetyMessage objects into a single VehSitDataMessage object.
	 * @param sr ServiceRequest containing DialogID, SequenceID, GroupID, and RequestID
	 * @param bsmList ArrayList&lt;BasicSafetyMessage&gt; containing 1-10 BasicSafetyMessage objects
	 * @return VehSitDataMessage
	 */
	public static VehSitDataMessage convertBsmToVsd(ServiceRequest sr, List<BasicSafetyMessage> bsmList) {

		VehSitDataMessage vsdm = new VehSitDataMessage();

		vsdm.bundle = new Bundle();
		vsdm.dialogID = sr.dialogID;
		vsdm.seqID = sr.seqID;
		vsdm.groupID = sr.groupID;
		vsdm.requestID = sr.requestID;

		if (bsmList.size() > MAX_LENGTH_BSM_LIST) {
			throw new IllegalArgumentException(
					String.format("BSM list too large: %d, max = %d", bsmList.size(), MAX_LENGTH_BSM_LIST));
		}

		for (BasicSafetyMessage bsm : bsmList) {
			vsdm.bundle.add(createVehSitRecord(bsm));
		}

		return vsdm;
	}

	private static VehSitRecord createVehSitRecord(BasicSafetyMessage bsm) {

		VehSitRecord vsr = new VehSitRecord();

		vsr.tempID = bsm.coreData.id;
		vsr.time = new DDateTime();
		vsr.time.second = bsm.coreData.secMark;
		vsr.pos = new Position3D(bsm.coreData.lat, bsm.coreData._long);
		vsr.pos.elevation = bsm.coreData.elev;

		vsr.fundamental = createFundamentalSituationalStatus(bsm.coreData);
		vsr.vehstat = createVehicleSituationStatus(bsm.partII);
		vsr.weather = createWeather(bsm.partII);
		vsr.env = createEnvironmental(bsm.partII);

		return vsr;

	}

	private static FundamentalSituationalStatus createFundamentalSituationalStatus(BSMcoreData bsmCoreData) {

		FundamentalSituationalStatus fss = new FundamentalSituationalStatus();
		
		fss.speed = new TransmissionAndSpeed();
		fss.speed.transmisson = bsmCoreData.transmission;
		fss.speed.speed = new Velocity(bsmCoreData.speed.intValue());
		fss.heading = bsmCoreData.heading;
		fss.steeringAngle = bsmCoreData.angle;
		fss.accelSet = bsmCoreData.accelSet;
		fss.brakes = bsmCoreData.brakes;
		fss.vehSize = bsmCoreData.size;
		// TODO fss.vsmEventFlag = ???

		return fss;
	}

	private static VehicleSituationStatus createVehicleSituationStatus(BasicSafetyMessage.PartII part2) {
		// TODO
		return null;
	}

	private static Weather createWeather(BasicSafetyMessage.PartII part2) {
		// TODO
		return null;
	}

	private static Environmental createEnvironmental(BasicSafetyMessage.PartII part2) {
		// TODO
		return null;
	}

}

