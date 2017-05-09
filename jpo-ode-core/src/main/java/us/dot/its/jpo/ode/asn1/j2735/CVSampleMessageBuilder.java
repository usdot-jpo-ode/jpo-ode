package us.dot.its.jpo.ode.asn1.j2735;

import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.dsrc.Acceleration;
import us.dot.its.jpo.ode.j2735.dsrc.AccelerationSet4Way;
import us.dot.its.jpo.ode.j2735.dsrc.AntiLockBrakeStatus;
import us.dot.its.jpo.ode.j2735.dsrc.AuxiliaryBrakeStatus;
import us.dot.its.jpo.ode.j2735.dsrc.BrakeAppliedStatus;
import us.dot.its.jpo.ode.j2735.dsrc.BrakeBoostApplied;
import us.dot.its.jpo.ode.j2735.dsrc.BrakeSystemStatus;
import us.dot.its.jpo.ode.j2735.dsrc.DDateTime;
import us.dot.its.jpo.ode.j2735.dsrc.DDay;
import us.dot.its.jpo.ode.j2735.dsrc.DHour;
import us.dot.its.jpo.ode.j2735.dsrc.DMinute;
import us.dot.its.jpo.ode.j2735.dsrc.DMonth;
import us.dot.its.jpo.ode.j2735.dsrc.DOffset;
import us.dot.its.jpo.ode.j2735.dsrc.DSecond;
import us.dot.its.jpo.ode.j2735.dsrc.DYear;
import us.dot.its.jpo.ode.j2735.dsrc.Elevation;
import us.dot.its.jpo.ode.j2735.dsrc.Heading;
import us.dot.its.jpo.ode.j2735.dsrc.Latitude;
import us.dot.its.jpo.ode.j2735.dsrc.Longitude;
import us.dot.its.jpo.ode.j2735.dsrc.MsgCRC;
import us.dot.its.jpo.ode.j2735.dsrc.Position3D;
import us.dot.its.jpo.ode.j2735.dsrc.StabilityControlStatus;
import us.dot.its.jpo.ode.j2735.dsrc.SteeringWheelAngle;
import us.dot.its.jpo.ode.j2735.dsrc.TemporaryID;
import us.dot.its.jpo.ode.j2735.dsrc.TractionControlStatus;
import us.dot.its.jpo.ode.j2735.dsrc.TransmissionAndSpeed;
import us.dot.its.jpo.ode.j2735.dsrc.TransmissionState;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleLength;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleSize;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleWidth;
import us.dot.its.jpo.ode.j2735.dsrc.Velocity;
import us.dot.its.jpo.ode.j2735.dsrc.VerticalAcceleration;
import us.dot.its.jpo.ode.j2735.dsrc.YawRate;
import us.dot.its.jpo.ode.j2735.semi.ConnectionPoint;
import us.dot.its.jpo.ode.j2735.semi.FundamentalSituationalStatus;
import us.dot.its.jpo.ode.j2735.semi.GeoRegion;
import us.dot.its.jpo.ode.j2735.semi.GroupID;
import us.dot.its.jpo.ode.j2735.semi.IPv4Address;
import us.dot.its.jpo.ode.j2735.semi.IpAddress;
import us.dot.its.jpo.ode.j2735.semi.PortNumber;
import us.dot.its.jpo.ode.j2735.semi.SemiDialogID;
import us.dot.its.jpo.ode.j2735.semi.SemiSequenceID;
import us.dot.its.jpo.ode.j2735.semi.ServiceRequest;
import us.dot.its.jpo.ode.j2735.semi.ServiceResponse;
import us.dot.its.jpo.ode.j2735.semi.Sha256Hash;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage.Bundle;
import us.dot.its.jpo.ode.j2735.semi.VehSitRecord;
import us.dot.its.jpo.ode.j2735.semi.VsmType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.oss.asn1.AbstractData;
import com.oss.asn1.Coder;
import com.oss.asn1.ControlTableNotFoundException;
import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;
import com.oss.asn1.InitializationException;

public class CVSampleMessageBuilder {

	static {
		try {
			J2735.initialize();
		} catch (ControlTableNotFoundException e) {
			e.printStackTrace();
		} catch (InitializationException e) {
			e.printStackTrace();
		}
	}

	private static final int expTime = 1;

	private static final byte[] reqID = new byte[] { (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01 };
	private static final TemporaryID tmpReq = new TemporaryID(reqID);

	private static final GroupID groupID = new GroupID(
			new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 });

	public static ServiceRequest buildVehicleSituationDataServiceRequest() {
		ServiceRequest vsr = new ServiceRequest();
		vsr.setDialogID(SemiDialogID.vehSitData);
		vsr.setSeqID(SemiSequenceID.svcReq);
		vsr.setGroupID(groupID);
		vsr.setRequestID(tmpReq);

		IpAddress ipAddr = new IpAddress();
		ipAddr.setIpv4Address(new IPv4Address(J2735Util.ipToBytes("54.210.159.61")));
		ConnectionPoint cp = new ConnectionPoint(ipAddr, new PortNumber(6666));
		vsr.setDestination(cp);

		return vsr;
	}

	public static ServiceResponse buildVehicleSituationDataServiceResponse() {
		ServiceResponse vsr = new ServiceResponse();

		vsr.setDialogID(SemiDialogID.vehSitData);
		vsr.setSeqID(SemiSequenceID.svcResp);
		vsr.setGroupID(groupID);
		vsr.setRequestID(tmpReq);

		vsr.setExpiration(J2735Util.expireInMin(expTime));

		Position3D nwCnr = new Position3D(new Latitude(J2735Util.convertGeoCoordinateToInt(42.517663)),
				new Longitude(J2735Util.convertGeoCoordinateToInt(-83.548386)));
		Position3D seCnr = new Position3D(new Latitude(J2735Util.convertGeoCoordinateToInt(42.402164)),
				new Longitude(J2735Util.convertGeoCoordinateToInt(-83.317673)));
		GeoRegion svcRegion = new GeoRegion(nwCnr, seCnr);
		vsr.setServiceRegion(svcRegion);

		vsr.setHash(new Sha256Hash(ByteBuffer.allocate(32).putInt(1).array()));
		return vsr;
	}

	public static ServiceRequest buildServiceRequest(TemporaryID requestID, SemiDialogID dialogID) {
		return new ServiceRequest(dialogID, SemiSequenceID.svcReq, groupID, requestID);
	}

	public static ServiceRequest buildServiceRequest(TemporaryID requestID, SemiDialogID dialogID, GroupID groupID) {
		return new ServiceRequest(dialogID, SemiSequenceID.svcReq, groupID, requestID);
	}

	public static ServiceRequest buildServiceRequest(TemporaryID requestID, GroupID groupID, SemiDialogID dialogID) {
		return new ServiceRequest(dialogID, SemiSequenceID.svcReq, groupID, requestID);
	}

	public static ServiceRequest buildServiceRequest(TemporaryID requestID, SemiDialogID dialogID,
			ConnectionPoint destination) {
		return new ServiceRequest(dialogID, SemiSequenceID.svcReq, groupID, requestID, destination);
	}

	public static ServiceRequest buildServiceRequest(TemporaryID requestID, SemiDialogID dialogID,
			ConnectionPoint destination, GroupID groupID) {
		return new ServiceRequest(dialogID, SemiSequenceID.svcReq, groupID, requestID, destination);
	}

	public static VehSitDataMessage buildVehSitDataMessage(double lat, double lon) throws IOException {

		SemiDialogID dialID = (SemiDialogID.vehSitData);
		SemiSequenceID semiID = (SemiSequenceID.data);
		TemporaryID requestID = tmpReq;
		MsgCRC crc = new MsgCRC("12".getBytes());

		VsmType type = new VsmType(CVTypeHelper.VsmType.WEATHER.arrayValue());

		// 1/////////////////////////////////////////////////////////////////////////////
		TemporaryID tempID = new TemporaryID(J2735Util.mergeBytes(new byte[] { 0x20, 0x01, 0x3E, 0x16 }));
		DDateTime dt1 = new DDateTime(new DYear(2013), new DMonth(12), new DDay(9), new DHour(9), new DMinute(30),
				new DSecond(30), new DOffset(-300));
		// 43°23'08.4"N 107°29'42.4"W
		Position3D pos = new Position3D(new Latitude(J2735Util.convertGeoCoordinateToInt(lat)),
				new Longitude(J2735Util.convertGeoCoordinateToInt(lon)));
		pos.setElevation(new Elevation((short) 840));
		final TransmissionAndSpeed speed = new TransmissionAndSpeed(TransmissionState.forwardGears, new Velocity(8191));
		final Heading heading = new Heading(104);
		final SteeringWheelAngle stwhlangle = new SteeringWheelAngle(0);

		final Acceleration lonAccel = new Acceleration(1);
		final Acceleration latAccel = new Acceleration(1);
		final VerticalAcceleration vertAccel = new VerticalAcceleration(43);
		final YawRate yaw = new YawRate(0);
		final AccelerationSet4Way acc4way = new AccelerationSet4Way(lonAccel, latAccel, vertAccel, yaw);

		final BrakeSystemStatus brakes = new BrakeSystemStatus(new BrakeAppliedStatus(new byte[] { (byte) 0xf8 }),
				TractionControlStatus.unavailable, AntiLockBrakeStatus.unavailable, StabilityControlStatus.unavailable,
				BrakeBoostApplied.unavailable, AuxiliaryBrakeStatus.unavailable);

		final VehicleSize vs = new VehicleSize(new VehicleWidth(260), new VehicleLength(236));

		FundamentalSituationalStatus fundamental = new FundamentalSituationalStatus(speed, heading, stwhlangle, acc4way,
				brakes, vs);

		VehSitRecord VehSitRecord1 = new VehSitRecord(tempID, dt1, pos, fundamental);

		// 2/////////////////////////////////////////////////////////////////////////////
		tempID = new TemporaryID(J2735Util.mergeBytes(new byte[] { 0x20, 0x01, 0x3E, 0x16 }));
		dt1 = new DDateTime(new DYear(2013), new DMonth(12), new DDay(9), new DHour(9), new DMinute(30),
				new DSecond(30), new DOffset(-300));
		pos = new Position3D(new Latitude(J2735Util.convertGeoCoordinateToInt(42.4478019351393)),
				new Longitude(J2735Util.convertGeoCoordinateToInt(-83.4309105986756)));
		pos.setElevation(new Elevation((short) 840));
		VehSitRecord VehSitRecord2 = new VehSitRecord(tempID, dt1, pos, fundamental);

		// 3/////////////////////////////////////////////////////////////////////////////
		tempID = new TemporaryID(J2735Util.mergeBytes(new byte[] { 0x20, 0x01, 0x3E, 0x16 }));
		dt1 = new DDateTime(new DYear(2013), new DMonth(12), new DDay(9), new DHour(9), new DMinute(30),
				new DSecond(30), new DOffset(-300));
		pos = new Position3D(new Latitude(J2735Util.convertGeoCoordinateToInt(42.44783187)),
				new Longitude(J2735Util.convertGeoCoordinateToInt(-83.4309083816587)));
		pos.setElevation(new Elevation((short) 841));
		VehSitRecord VehSitRecord3 = new VehSitRecord(tempID, dt1, pos, fundamental);

		// 4/////////////////////////////////////////////////////////////////////////////
		tempID = new TemporaryID(J2735Util.mergeBytes(new byte[] { 0x20, 0x01, 0x3E, 0x16 }));
		dt1 = new DDateTime(new DYear(2013), new DMonth(12), new DDay(9), new DHour(9), new DMinute(30),
				new DSecond(30), new DOffset(-300));
		pos = new Position3D(new Latitude(J2735Util.convertGeoCoordinateToInt(42.4477416099675)),
				new Longitude(J2735Util.convertGeoCoordinateToInt(-83.4309124464879)));
		pos.setElevation(new Elevation((short) 842));
		VehSitRecord VehSitRecord4 = new VehSitRecord(tempID, dt1, pos, fundamental);

		// 5/////////////////////////////////////////////////////////////////////////////
		tempID = new TemporaryID(J2735Util.mergeBytes(new byte[] { 0x20, 0x01, 0x3E, 0x16 }));
		dt1 = new DDateTime(new DYear(2013), new DMonth(12), new DDay(9), new DHour(9), new DMinute(30),
				new DSecond(30), new DOffset(-300));
		pos = new Position3D(new Latitude(J2735Util.convertGeoCoordinateToInt(42.4477115433093)),
				new Longitude(J2735Util.convertGeoCoordinateToInt(-83.4309124895822)));
		pos.setElevation(new Elevation((short) 842));
		VehSitRecord VehSitRecord5 = new VehSitRecord(tempID, dt1, pos, fundamental);

		// 6/////////////////////////////////////////////////////////////////////////////
		tempID = new TemporaryID(J2735Util.mergeBytes(new byte[] { 0x20, 0x01, 0x3E, 0x16 }));
		dt1 = new DDateTime(new DYear(2013), new DMonth(12), new DDay(9), new DHour(9), new DMinute(30),
				new DSecond(30), new DOffset(-300));
		pos = new Position3D(new Latitude(J2735Util.convertGeoCoordinateToInt(42.4476805277902)),
				new Longitude(J2735Util.convertGeoCoordinateToInt(-83.4309131567509)));
		pos.setElevation(new Elevation((short) 841));
		VehSitRecord VehSitRecord6 = new VehSitRecord(tempID, dt1, pos, fundamental);

		// 7/////////////////////////////////////////////////////////////////////////////
		tempID = new TemporaryID(J2735Util.mergeBytes(new byte[] { 0x20, 0x01, 0x3E, 0x16 }));
		dt1 = new DDateTime(new DYear(2013), new DMonth(12), new DDay(9), new DHour(9), new DMinute(30),
				new DSecond(29), new DOffset(-300));
		pos = new Position3D(new Latitude(J2735Util.convertGeoCoordinateToInt(42.4476503815348)),
				new Longitude(J2735Util.convertGeoCoordinateToInt(-83.4309133488356)));
		pos.setElevation(new Elevation((short) 841));
		VehSitRecord VehSitRecord7 = new VehSitRecord(tempID, dt1, pos, fundamental);

		// 8/////////////////////////////////////////////////////////////////////////////
		tempID = new TemporaryID(J2735Util.mergeBytes(new byte[] { 0x20, 0x01, 0x3E, 0x16 }));
		dt1 = new DDateTime(new DYear(2013), new DMonth(12), new DDay(9), new DHour(9), new DMinute(30),
				new DSecond(29), new DOffset(-300));
		pos = new Position3D(new Latitude(J2735Util.convertGeoCoordinateToInt(42.4476182535585)),
				new Longitude(J2735Util.convertGeoCoordinateToInt(-83.4309139519957)));
		pos.setElevation(new Elevation((short) 841));
		VehSitRecord VehSitRecord8 = new VehSitRecord(tempID, dt1, pos, fundamental);

		// 9/////////////////////////////////////////////////////////////////////////////
		tempID = new TemporaryID(J2735Util.mergeBytes(new byte[] { 0x20, 0x01, 0x3E, 0x16 }));
		dt1 = new DDateTime(new DYear(2013), new DMonth(12), new DDay(9), new DHour(9), new DMinute(30),
				new DSecond(29), new DOffset(-300));
		pos = new Position3D(new Latitude(J2735Util.convertGeoCoordinateToInt(42.4475892080338)),
				new Longitude(J2735Util.convertGeoCoordinateToInt(-83.4309153351609)));
		pos.setElevation(new Elevation((short) 841));
		VehSitRecord VehSitRecord9 = new VehSitRecord(tempID, dt1, pos, fundamental);

		VehSitDataMessage vsdm = new VehSitDataMessage(dialID, semiID, groupID,
				requestID, type, new Bundle(new VehSitRecord[] { VehSitRecord1, VehSitRecord2, VehSitRecord3,
						VehSitRecord4, VehSitRecord5, VehSitRecord6, VehSitRecord7, VehSitRecord8, VehSitRecord9 }),
				crc);

		return vsdm;

	}

	// Note: Creates a new Coder for each call for thread safety, intended for
	// testing only
	public static byte[] messageToEncodedBytes(AbstractData message)
			throws EncodeFailedException, EncodeNotSupportedException {
		ByteArrayOutputStream sink = new ByteArrayOutputStream();
		Coder coder = J2735.getPERUnalignedCoder();
		coder.encode(message, sink);
		return sink.toByteArray();
	}
}
