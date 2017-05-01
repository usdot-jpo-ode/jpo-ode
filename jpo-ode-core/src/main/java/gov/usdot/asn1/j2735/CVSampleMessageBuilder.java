package gov.usdot.asn1.j2735;

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
import us.dot.its.jpo.ode.j2735.dsrc.DFullTime;
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
import us.dot.its.jpo.ode.j2735.semi.DataAcceptance;
import us.dot.its.jpo.ode.j2735.semi.DataRequest;
import us.dot.its.jpo.ode.j2735.semi.DataSubscriptionCancel;
import us.dot.its.jpo.ode.j2735.semi.DataSubscriptionRequest;
import us.dot.its.jpo.ode.j2735.semi.DataSubscriptionResponse;
import us.dot.its.jpo.ode.j2735.semi.DistributionType;
import us.dot.its.jpo.ode.j2735.semi.FundamentalSituationalStatus;
import us.dot.its.jpo.ode.j2735.semi.GeoRegion;
import us.dot.its.jpo.ode.j2735.semi.GroupID;
import us.dot.its.jpo.ode.j2735.semi.IPv4Address;
import us.dot.its.jpo.ode.j2735.semi.IPv6Address;
import us.dot.its.jpo.ode.j2735.semi.IpAddress;
import us.dot.its.jpo.ode.j2735.semi.ObjectRegistrationData;
import us.dot.its.jpo.ode.j2735.semi.PortNumber;
import us.dot.its.jpo.ode.j2735.semi.Psid;
import us.dot.its.jpo.ode.j2735.semi.SemiDialogID;
import us.dot.its.jpo.ode.j2735.semi.SemiSequenceID;
import us.dot.its.jpo.ode.j2735.semi.ServiceID;
import us.dot.its.jpo.ode.j2735.semi.ServiceProviderID;
import us.dot.its.jpo.ode.j2735.semi.ServiceRecord;
import us.dot.its.jpo.ode.j2735.semi.ServiceRecord.ConnectionPoints;
import us.dot.its.jpo.ode.j2735.semi.ServiceRecord.SvcPSIDs;
import us.dot.its.jpo.ode.j2735.semi.ServiceRequest;
import us.dot.its.jpo.ode.j2735.semi.ServiceResponse;
import us.dot.its.jpo.ode.j2735.semi.Sha256Hash;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage.Bundle;
import us.dot.its.jpo.ode.j2735.semi.VehSitRecord;
import us.dot.its.jpo.ode.j2735.semi.VsmType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.TimeZone;

import com.oss.asn1.AbstractData;
import com.oss.asn1.Coder;
import com.oss.asn1.ControlTableNotFoundException;
import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;
import com.oss.asn1.INTEGER;
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

	private static final double nwCrnLat = 42.517663;
	private static final double nwCrnLon = -83.548386;

	private static final double seCnrLat = 42.402164;
	private static final double seCnrLon = -83.317673;

	private static final int expTime = 1;	
	
	private static final byte[] reqID = new byte[] {(byte)0x01, (byte)0x01, (byte)0x01, (byte)0x01};
	private static final TemporaryID tmpReq = new TemporaryID(reqID);
	
	private static final GroupID groupID = new GroupID(new byte[] {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00});

	public static ServiceRequest buildVehicleSituationDataServiceRequest() {
		ServiceRequest vsr = new ServiceRequest();
		vsr.setDialogID(SemiDialogID.vehSitData);
		vsr.setSeqID(SemiSequenceID.svcReq);
		vsr.setGroupID(groupID);
		vsr.setRequestID(new TemporaryID(ByteBuffer.allocate(4).putInt(1001).array()));
		return vsr;
	}
	
	public static ServiceResponse buildVehicleSituationDataServiceResponse() {
		ServiceResponse vsr = new ServiceResponse();
		
		vsr.setDialogID(SemiDialogID.vehSitData);
		vsr.setSeqID(SemiSequenceID.svcResp);
		vsr.setGroupID(groupID);
		vsr.setRequestID(new TemporaryID(ByteBuffer.allocate(4).putInt(1001).array()));
		
		vsr.setExpiration(J2735Util.expireInMin(expTime));
		
		Position3D nwCnr = new Position3D(new Latitude(J2735Util.convertGeoCoordinateToInt(42.517663)), 
				new Longitude(J2735Util.convertGeoCoordinateToInt(-83.548386)));
		Position3D seCnr = new Position3D(new Latitude(J2735Util.convertGeoCoordinateToInt(42.402164)), 
				new Longitude(J2735Util.convertGeoCoordinateToInt(-83.317673)));
		GeoRegion svcRegion = new GeoRegion(nwCnr,seCnr);
		vsr.setServiceRegion(svcRegion);
		
		vsr.setHash(new Sha256Hash(ByteBuffer.allocate(32).putInt(1).array()));
		return vsr;
	}
	
 
	public static ServiceRequest buildDataSubscriptionServiceRequest() {
		return buildDataSubscriptionServiceRequest(tmpReq);
	}
	
	public static ServiceRequest buildDataSubscriptionServiceRequest(TemporaryID requestID) {
		return buildServiceRequest(requestID, SemiDialogID.dataSubscription);
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
	
	public static ServiceRequest buildServiceRequest(TemporaryID requestID, SemiDialogID dialogID, ConnectionPoint destination) {
		return new ServiceRequest(dialogID, SemiSequenceID.svcReq, groupID, requestID, destination);
	}
	
	public static ServiceRequest buildServiceRequest(TemporaryID requestID, SemiDialogID dialogID, ConnectionPoint destination, GroupID groupID) {
		return new ServiceRequest(dialogID, SemiSequenceID.svcReq, groupID, requestID, destination);
	}
	
	public static ServiceResponse buildDataSubscriptionServiceResponse() {
		ServiceResponse dsr = new ServiceResponse();
		
		dsr.setDialogID(SemiDialogID.dataSubscription);
		dsr.setSeqID(SemiSequenceID.svcResp);
		dsr.setGroupID(groupID);
		dsr.setRequestID(tmpReq);
		
		dsr.setExpiration(J2735Util.expireInMin(expTime));
		
		Position3D nwCnr = new Position3D(new Latitude(J2735Util.convertGeoCoordinateToInt(42.517663)), 
				new Longitude(J2735Util.convertGeoCoordinateToInt(-83.548386)));
		Position3D seCnr = new Position3D(new Latitude(J2735Util.convertGeoCoordinateToInt(42.402164)), 
				new Longitude(J2735Util.convertGeoCoordinateToInt(-83.317673)));
		GeoRegion svcRegion = new GeoRegion(nwCnr,seCnr);
		dsr.setServiceRegion(svcRegion);
		
		dsr.setHash(new Sha256Hash(ByteBuffer.allocate(32).putInt(1).array()));
		return dsr;
	}
	
	
	public static DataSubscriptionRequest buildDataSubscriptionRequest() throws UnknownHostException {
		DataSubscriptionRequest dsreq = new DataSubscriptionRequest();
		
		dsreq.setDialogID(SemiDialogID.dataSubscription);
		dsreq.setSeqID(SemiSequenceID.subscriptionReq);
		dsreq.setGroupID(groupID);
		dsreq.setRequestID(tmpReq);
		
		VsmType type = new VsmType(CVTypeHelper.VsmType.WEATHER.arrayValue());
		dsreq.setType(DataSubscriptionRequest.Type.createTypeWithVsmType(type));
		
		Calendar future = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		future.add(Calendar.HOUR, 2);
		
		DFullTime endTime = new DFullTime(
			new DYear(future.get(Calendar.YEAR)), 
			new DMonth(future.get(Calendar.MONTH) + 1), // ASN.1 is 1 based while Java is 0 based 
			new DDay(future.get(Calendar.DAY_OF_MONTH)), 
			new DHour(future.get(Calendar.HOUR_OF_DAY)),
			new DMinute(future.get(Calendar.MINUTE)));
		dsreq.setEndTime(endTime);

		Position3D nwCnr = getPosition3D(nwCrnLat, nwCrnLon);
		Position3D seCnr = getPosition3D(seCnrLat, seCnrLon);
		GeoRegion svcRegion = new GeoRegion(nwCnr,seCnr);
		dsreq.setServiceRegion(svcRegion);
		
		return dsreq;
	}
	
	
	public static DataSubscriptionResponse buildDataSubscriptionResponse() {
		DataSubscriptionResponse dsresp = new DataSubscriptionResponse();
		
		dsresp.setDialogID(SemiDialogID.dataSubscription);
		dsresp.setSeqID(SemiSequenceID.subscriptinoResp);
		dsresp.setGroupID(groupID);
		dsresp.setRequestID(tmpReq);
		
		TemporaryID subID = new TemporaryID("1234".getBytes());
		dsresp.setSubID(subID);
		
		INTEGER err = new INTEGER(0);
		dsresp.setErr(err);

		return dsresp;
	}
	
	public static DataSubscriptionCancel buildDataSubscriptionCancel() {
		DataSubscriptionCancel dsc = new DataSubscriptionCancel();
		
		dsc.setDialogID(SemiDialogID.dataSubscription);
		dsc.setSeqID(SemiSequenceID.subscriptionCancel);
		dsc.setGroupID(groupID);
		dsc.setRequestID(tmpReq);
		
		TemporaryID subID = new TemporaryID(ByteBuffer.allocate(4).putInt(10000004).array());
		dsc.setSubID(subID);

		return dsc;
	}
	
	public static DataRequest buildRSUAdvisorySitDataRequest() {
		DataRequest rsureq = new DataRequest();
		
		rsureq.setDialogID(SemiDialogID.advSitDatDist);
		rsureq.setSeqID(SemiSequenceID.dataReq);
		rsureq.setGroupID(groupID);
		rsureq.setRequestID(tmpReq);
		
		Position3D nwCnr = getPosition3D(nwCrnLat, nwCrnLon);
		Position3D seCnr = getPosition3D(seCnrLat, seCnrLon);
		GeoRegion svcRegion = new GeoRegion(nwCnr,seCnr);
		rsureq.setServiceRegion(svcRegion);
		
		DistributionType distType = new DistributionType(new byte[] { 0 } );
		rsureq.setDistType(distType);

		return rsureq;
	}
	
	public static DataAcceptance buildDataAcceptance(SemiDialogID dialogID) {
		return new DataAcceptance(dialogID, SemiSequenceID.accept, groupID, tmpReq);
	}
		
	public static VehSitDataMessage buildVehSitDataMessage() throws IOException {
		
		SemiDialogID dialID = (SemiDialogID.vehSitData);
		SemiSequenceID semiID = (SemiSequenceID.data);
		TemporaryID requestID = new TemporaryID(J2735Util.mergeBytes(new byte[] { 0x20, 0x01, 0x3E, 0x16 } ));
		MsgCRC crc = new MsgCRC("12".getBytes());
		
		VsmType type = new VsmType(CVTypeHelper.VsmType.WEATHER.arrayValue());

		//1/////////////////////////////////////////////////////////////////////////////
		TemporaryID tempID = new TemporaryID(J2735Util.mergeBytes(new byte[] { 0x20, 0x01, 0x3E, 0x16 } ));
		DDateTime dt1 = new DDateTime(new DYear(2013), new DMonth(12), new DDay(9), new DHour(9), new DMinute(30),
				new DSecond(30), new DOffset(-300));
		
		Position3D pos = new Position3D(new Latitude(J2735Util.convertGeoCoordinateToInt(42.44783187)), new Longitude(
				J2735Util.convertGeoCoordinateToInt(-83.4309083816587)));
		pos.setElevation(new Elevation((short) 840));
		final TransmissionAndSpeed speed = new TransmissionAndSpeed(TransmissionState.forwardGears, new Velocity(8191));
		final Heading heading = new Heading(104);
		final SteeringWheelAngle stwhlangle = new SteeringWheelAngle(0);

		final Acceleration lonAccel = new Acceleration(1);
		final Acceleration latAccel = new Acceleration(1);
		final VerticalAcceleration vertAccel = new VerticalAcceleration(43);
		final YawRate yaw = new YawRate(0);
		final AccelerationSet4Way acc4way = new AccelerationSet4Way(lonAccel, latAccel, vertAccel, yaw);
	    
		final BrakeSystemStatus brakes = new BrakeSystemStatus(
					new BrakeAppliedStatus(new byte[] { (byte)0xf8 } ), 
					TractionControlStatus.unavailable, 
					AntiLockBrakeStatus.unavailable, 
					StabilityControlStatus.unavailable,
					BrakeBoostApplied.unavailable,
					AuxiliaryBrakeStatus.unavailable
				);

		final VehicleSize vs = new VehicleSize(new VehicleWidth(260), new VehicleLength(236));
		
		FundamentalSituationalStatus fundamental = new FundamentalSituationalStatus(speed, heading, stwhlangle, acc4way, brakes, vs);
		
		VehSitRecord VehSitRecord1 = new VehSitRecord(tempID, dt1, pos, fundamental);
		
		//2/////////////////////////////////////////////////////////////////////////////
		tempID = new TemporaryID(J2735Util.mergeBytes(new byte[] { 0x20, 0x01, 0x3E, 0x16 } ));
		dt1 = new DDateTime(new DYear(2013), new DMonth(12), new DDay(9), new DHour(9), new DMinute(30),
				new DSecond(30), new DOffset(-300));
		pos = new Position3D(new Latitude(J2735Util.convertGeoCoordinateToInt(42.4478019351393)), new Longitude(
				J2735Util.convertGeoCoordinateToInt(-83.4309105986756)));
		pos.setElevation(new Elevation((short) 840));
		VehSitRecord VehSitRecord2 = new VehSitRecord(tempID, dt1, pos, fundamental);

		//3/////////////////////////////////////////////////////////////////////////////
		tempID = new TemporaryID(J2735Util.mergeBytes(new byte[] { 0x20, 0x01, 0x3E, 0x16 } ));
		dt1 = new DDateTime(new DYear(2013), new DMonth(12), new DDay(9), new DHour(9), new DMinute(30),
				new DSecond(30), new DOffset(-300));
		pos = new Position3D(new Latitude(J2735Util.convertGeoCoordinateToInt(42.44783187)), new Longitude(
				J2735Util.convertGeoCoordinateToInt(-83.4309083816587)));
		pos.setElevation(new Elevation((short) 841));
		VehSitRecord VehSitRecord3 = new VehSitRecord(tempID, dt1, pos, fundamental);
		
		//4/////////////////////////////////////////////////////////////////////////////
		tempID = new TemporaryID(J2735Util.mergeBytes(new byte[] { 0x20, 0x01, 0x3E, 0x16 } ));
		dt1 = new DDateTime(new DYear(2013), new DMonth(12), new DDay(9), new DHour(9), new DMinute(30),
				new DSecond(30), new DOffset(-300));
		pos = new Position3D(new Latitude(J2735Util.convertGeoCoordinateToInt(42.4477416099675)), new Longitude(
				J2735Util.convertGeoCoordinateToInt(-83.4309124464879)));
		pos.setElevation(new Elevation((short) 842));
		VehSitRecord VehSitRecord4 = new VehSitRecord(tempID, dt1, pos, fundamental);

		//5/////////////////////////////////////////////////////////////////////////////
		tempID = new TemporaryID(J2735Util.mergeBytes(new byte[] { 0x20, 0x01, 0x3E, 0x16 } ));
		dt1 = new DDateTime(new DYear(2013), new DMonth(12), new DDay(9), new DHour(9), new DMinute(30),
				new DSecond(30), new DOffset(-300));
		pos = new Position3D(new Latitude(J2735Util.convertGeoCoordinateToInt(42.4477115433093)), new Longitude(
				J2735Util.convertGeoCoordinateToInt(-83.4309124895822)));
		pos.setElevation(new Elevation((short) 842));		
		VehSitRecord VehSitRecord5 = new VehSitRecord(tempID, dt1, pos, fundamental);

		//6/////////////////////////////////////////////////////////////////////////////
		tempID = new TemporaryID(J2735Util.mergeBytes(new byte[] { 0x20, 0x01, 0x3E, 0x16 } ));
		dt1 = new DDateTime(new DYear(2013), new DMonth(12), new DDay(9), new DHour(9), new DMinute(30),
				new DSecond(30), new DOffset(-300));
		pos = new Position3D(new Latitude(J2735Util.convertGeoCoordinateToInt(42.4476805277902)), new Longitude(
				J2735Util.convertGeoCoordinateToInt(-83.4309131567509)));
		pos.setElevation(new Elevation((short) 841));		
		VehSitRecord VehSitRecord6 = new VehSitRecord(tempID, dt1, pos, fundamental);

		//7/////////////////////////////////////////////////////////////////////////////
		tempID = new TemporaryID(J2735Util.mergeBytes(new byte[] { 0x20, 0x01, 0x3E, 0x16 } ));
		dt1 = new DDateTime(new DYear(2013), new DMonth(12), new DDay(9), new DHour(9), new DMinute(30),
				new DSecond(29), new DOffset(-300));
		pos = new Position3D(new Latitude(J2735Util.convertGeoCoordinateToInt(42.4476503815348)), new Longitude(
				J2735Util.convertGeoCoordinateToInt(-83.4309133488356)));
		pos.setElevation(new Elevation((short) 841));
		VehSitRecord VehSitRecord7 = new VehSitRecord(tempID, dt1, pos, fundamental);

		//8/////////////////////////////////////////////////////////////////////////////
		tempID = new TemporaryID(J2735Util.mergeBytes(new byte[] { 0x20, 0x01, 0x3E, 0x16 } ));
		dt1 = new DDateTime(new DYear(2013), new DMonth(12), new DDay(9), new DHour(9), new DMinute(30),
				new DSecond(29), new DOffset(-300));
		pos = new Position3D(new Latitude(J2735Util.convertGeoCoordinateToInt(42.4476182535585)), new Longitude(
				J2735Util.convertGeoCoordinateToInt(-83.4309139519957)));
		pos.setElevation(new Elevation((short) 841));
		VehSitRecord VehSitRecord8 = new VehSitRecord(tempID, dt1, pos, fundamental);

		//9/////////////////////////////////////////////////////////////////////////////
		tempID = new TemporaryID(J2735Util.mergeBytes(new byte[] { 0x20, 0x01, 0x3E, 0x16 } ));
		dt1 = new DDateTime(new DYear(2013), new DMonth(12), new DDay(9), new DHour(9), new DMinute(30),
				new DSecond(29), new DOffset(-300));
		pos = new Position3D(new Latitude(J2735Util.convertGeoCoordinateToInt(42.4475892080338)), new Longitude(
				J2735Util.convertGeoCoordinateToInt(-83.4309153351609)));
		pos.setElevation(new Elevation((short) 841));		
		VehSitRecord VehSitRecord9 = new VehSitRecord(tempID, dt1, pos, fundamental);

		VehSitDataMessage vsdm = new VehSitDataMessage(
			dialID, 
			semiID,
			groupID,
			requestID,
			type, 
			new Bundle(new VehSitRecord[] { 
				VehSitRecord1,
				VehSitRecord2, 
				VehSitRecord3, 
				VehSitRecord4, 
				VehSitRecord5, 
				VehSitRecord6, 
				VehSitRecord7, 
				VehSitRecord8, 
				VehSitRecord9
			}),
			crc);

		return vsdm;

	}
	
	public static ObjectRegistrationData buildObjectRegistrationData(int groupId, int requestId, 
			int serviceId, int serviceProviderId, int psid, GeoRegion geoRegion, String ... ipPorts) {
		
		ObjectRegistrationData ord = new ObjectRegistrationData();
		ord.setDialogID(SemiDialogID.objReg);
		ord.setSeqID(SemiSequenceID.data);
		ord.setGroupID(new GroupID(ByteBuffer.allocate(4).putInt(groupId).array()));
		ord.setRequestID(new TemporaryID(ByteBuffer.allocate(4).putInt(requestId).array()));
		ord.setServiceID(ServiceID.valueOf(serviceId));
		
		ServiceRecord serviceRecord = new ServiceRecord();
		ConnectionPoints cps = new ConnectionPoints();
		for (String ipPort: ipPorts) {
			String[] split = ipPort.split(",");
			String ip = split[0];
			int port = Integer.parseInt(split[1]);
			
			IpAddress ipAddr = new IpAddress();
			if (ip.contains(":")) {
				ipAddr.setIpv6Address(new IPv6Address(J2735Util.ipToBytes(ip)));
			} else {
				ipAddr.setIpv4Address(new IPv4Address(J2735Util.ipToBytes(ip)));
			}
			ConnectionPoint cp = new ConnectionPoint(ipAddr, new PortNumber(port));
			cps.add(cp);
			// asn spec capped at 2 for now
			if (cps.getSize() == 2) {
				break;
			}
		}
		serviceRecord.setConnectionPoints(cps);
		
		serviceRecord.setSvcProvider(new ServiceProviderID(ByteBuffer.allocate(4).putInt(serviceProviderId).array()));
		Psid psidObj = new Psid(ByteBuffer.allocate(4).putInt(psid).array());
		SvcPSIDs psids = new SvcPSIDs();
		psids.add(psidObj);
		serviceRecord.setSvcPSIDs(psids);
		
		serviceRecord.setServiceRegion(geoRegion);
		ord.setServiceRecord(serviceRecord);
		return ord;
	}

	// Note: Creates a new Coder for each call for thread safety, intended for testing only
	public static byte[] messageToEncodedBytes(AbstractData message) throws EncodeFailedException,
			EncodeNotSupportedException {
		ByteArrayOutputStream sink = new ByteArrayOutputStream();
		Coder coder = J2735.getPERUnalignedCoder();
		coder.encode(message, sink);
		return sink.toByteArray();
	}

	public static Position3D getPosition3D(double lat, double lon) {
		Position3D pos = new Position3D(new Latitude(J2735Util.convertGeoCoordinateToInt(lat)), new Longitude(
				J2735Util.convertGeoCoordinateToInt(lon)));
		return pos;
	}

}
