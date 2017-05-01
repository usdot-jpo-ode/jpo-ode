package us.dot.its.jpo.ode.vsdm;

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
import us.dot.its.jpo.ode.j2735.dsrc.VehicleLength;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleWidth;
import us.dot.its.jpo.ode.j2735.dsrc.VerticalAcceleration;
import us.dot.its.jpo.ode.j2735.dsrc.YawRate;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleSize;
import us.dot.its.jpo.ode.j2735.semi.FundamentalSituationalStatus;
import us.dot.its.jpo.ode.j2735.semi.SemiDialogID;
import us.dot.its.jpo.ode.j2735.semi.SemiSequenceID;
import us.dot.its.jpo.ode.j2735.semi.ServiceRequest;
import us.dot.its.jpo.ode.j2735.semi.ServiceResponse;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage;
import us.dot.its.jpo.ode.j2735.semi.VehSitRecord;
import us.dot.its.jpo.ode.j2735.semi.VsmType;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage.Bundle;
import gov.usdot.asn1.j2735.CVSampleMessageBuilder;
import gov.usdot.asn1.j2735.CVTypeHelper;
import gov.usdot.asn1.j2735.J2735Util;
import us.dot.its.jpo.ode.common.asn1.GroupIDHelper;
import us.dot.its.jpo.ode.common.asn1.TemporaryIDHelper;
import us.dot.its.jpo.ode.common.asn1.TransmissionAndSpeedHelper;

import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.oss.asn1.AbstractData;
import com.oss.asn1.Coder;
import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;

/**
* This is not a unit test that runs during build time. This is more of
* a application that sends data the LCSDW for integration testing purpose.
*/
public class VsdmDepositorTest {
	
	private static Coder coder;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {		
		J2735.initialize();
		coder = J2735.getPERUnalignedCoder();
	}
	
	@Test @Ignore
	public void testFeedData2LCSDW() throws Exception {
		System.out.println("Initializing data feed to LCSDW ...");
		
		String targetHost = "54.242.96.40";
		int targetPort = 46751;
		
		DatagramSocket socket = new DatagramSocket(targetPort);
		socket.setSoTimeout(3000);
		
		ServiceRequest sr = new ServiceRequest();
		sr.setDialogID(SemiDialogID.vehSitData);
		sr.setSeqID(SemiSequenceID.svcReq);
		sr.setRequestID(new TemporaryID(ByteBuffer.allocate(4).putInt(1001).array()));
		
		ByteArrayOutputStream sink = new ByteArrayOutputStream();
		coder.encode(sr, sink);
		
		byte [] payload = sink.toByteArray();
		int length = payload.length;
		
		// Perform the trust establishment
		
		System.out.println("Sending ServiceRequest ...");
		socket.send(new DatagramPacket(
			payload,
			length,
			new InetSocketAddress(targetHost, targetPort)
		));
		
		try {
			byte [] buffer = new byte[10000];
			DatagramPacket responeDp = new DatagramPacket(buffer, buffer.length);
			socket.receive(responeDp);
	
			System.out.println("Waiting for ServiceResponse ...");
			if (buffer != null && buffer.length > 0) {
				AbstractData response = J2735Util.decode(coder, buffer);
				if (response instanceof ServiceResponse) {
					System.out.println("Printing ServiceResponse ...");
					System.out.println(response);
				}
			}
		} catch (Exception ex) {
			// We didn't get a response back from the trust establishment, we are
			// going to ignore it and process with sending sitdata.
			System.out.println("Failed to receive service response. Message: " + ex.getMessage());
		}
		
		// Now we prepare and send situation data to the LCSDW
		
		System.out.println("Preparing vehicle situation data ...");
		VehSitDataMessage vsdm = CVSampleMessageBuilder.buildVehSitDataMessage();
		VsmType vsmType = new VsmType(CVTypeHelper.VsmType.VEHSTAT.arrayValue());
		vsdm.setType(vsmType);
		byte [] encodedMsg = CVSampleMessageBuilder.messageToEncodedBytes(vsdm);
		
		System.out.println("Flooding the LCSDW with vehicle situation data ...");
		boolean sendByNumRecords = true; // Toggle this flag to by # records or by messages/second
		if (sendByNumRecords) {
			sendByNumberRecords(socket, targetHost, targetPort, encodedMsg, 100, 0);
		} else {
			sendWithLocalhostSpeed(socket, targetHost, targetPort, encodedMsg, 5);
		}
	}
	
	private void sendByNumberRecords(
			DatagramSocket socket,
			String host, 
			int port, 
			byte [] encodedMsg,
			int numRecords,
			int waitTimePerRecordInMillis) {
		int count = 0;
		long start = System.currentTimeMillis();
		while (count < numRecords) try {
			socket.send(new DatagramPacket(
				encodedMsg,
				encodedMsg.length,
				new InetSocketAddress(host, port)
			));
			count++;
			if (waitTimePerRecordInMillis > 0) {
				Thread.sleep(waitTimePerRecordInMillis);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		long duration = System.currentTimeMillis() - start;
		System.out.println("Sent " + count + " messages and it took " + duration + " milliseconds.");
	}
	
	private void sendWithLocalhostSpeed(
			DatagramSocket socket,
			String host, 
			int port, 
			byte [] encodedMsg,
			int sleepIntervalInMillis) {
		int count = 0;
		long start = System.currentTimeMillis();
		while (true) try {
			socket.send(new DatagramPacket(
				encodedMsg,
				encodedMsg.length,
				new InetSocketAddress(host, port)
			));
			count++;
			long duration = System.currentTimeMillis() - start;
			if (duration >= 1000) {
				float avg = (float) count / ((float) duration / 1000f);
				System.out.println("Feeding in " + avg + " messages/second.");
				
				count = 0;
				start = System.currentTimeMillis();
			}
			// Tweak the sleep interval for control flow.
			// The avg message/second is heavily depended
			// on the hardware you are running on.
			Thread.sleep(sleepIntervalInMillis);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Test @Ignore
	public void testBuildVehSitDataMessage() throws Exception {
		createEncodedVehSitDataMessage(-83.4309083816587, 42.4478318657929);
	}
	
	private byte [] createEncodedVehSitDataMessage(double latitude, double longitude) throws Exception {
		try {
			VehSitDataMessage sitDataMsg = createVehSitDataMessage(latitude, longitude);
			ByteArrayOutputStream sink = new ByteArrayOutputStream();
			coder.encode(sitDataMsg, sink);
			byte[] responseBytes = sink.toByteArray();
			return responseBytes;
		} catch (EncodeFailedException ex) {
			throw new Exception("Couldn't encode VehicleServiceResponse message because encoding failed", ex);
		} catch (EncodeNotSupportedException ex) {
			throw new Exception("Couldn't encode VehicleServiceResponse message because encoding is not supported", ex);
		}
	}
	
	private VehSitDataMessage createVehSitDataMessage(double latitude, double longitude) {
		int lon_int = J2735Util.convertGeoCoordinateToInt(longitude);
		int lat_int = J2735Util.convertGeoCoordinateToInt(latitude);
		
		long cur_time = System.currentTimeMillis();
		Calendar now = GregorianCalendar.getInstance();
		DDateTime dt = new DDateTime(
				new DYear(now.get(Calendar.YEAR)), 
				new DMonth(now.get(Calendar.MONTH)+1), 
				new DDay(now.get(Calendar.DAY_OF_MONTH)), 
				new DHour(now.get(Calendar.HOUR_OF_DAY)), 
				new DMinute(now.get(Calendar.MINUTE)), 
				new DSecond(now.get(Calendar.SECOND)),
				new DOffset( -300 ));
		
		int last_lat_1 = 0, last_lat_2 = 0, last_lon_1 = 0, last_lon_2 = 0;
		long last_time_1 = 0, last_time_2 = 0;
		
		short[] pathOffsets = new short[8];
		pathOffsets[0] = (short)(lat_int - last_lat_1);			 // latitude offset
		pathOffsets[1] = (short)(lon_int - last_lon_1); 		 // longitude offset
		pathOffsets[2] = 0;										 // elevation offset
		pathOffsets[3] = (short)((cur_time - last_time_1)/10);	 // time delta (LSB units of 10 mSec)
		pathOffsets[4] = (short)(last_lat_1 - last_lat_2);		 // latitude offset
		pathOffsets[5] = (short)(last_lon_1 - last_lon_2); 		 // longitude offset
		pathOffsets[6] = 0;										 // elevation offset
		pathOffsets[7] = (short)((last_time_1 - last_time_2)/10);// time delta ((LSB units of 10 mSec)
		
		// update path history
		last_lat_2 = last_lat_1;
		last_lat_1 = lat_int;
		last_lon_2 = last_lon_1;
		last_lon_1 = lon_int;
		last_time_2 = last_time_1;
		last_time_1 = cur_time;
		
		TemporaryID tempID = new TemporaryID("1234".getBytes());
		Position3D pos = new Position3D(new Latitude(42517663), new Longitude(-83548386));
		TransmissionAndSpeed speed = TransmissionAndSpeedHelper.createTransmissionAndSpeed(55);
		Heading heading = new Heading(90);
		
		// see BrakeSystemStatus in DSRC.ASN
		final Acceleration lonAccel = new Acceleration(1);
		final Acceleration latAccel = new Acceleration(1);
		final VerticalAcceleration vertAccel = new VerticalAcceleration(43);
		final YawRate yaw = new YawRate(0);
		final AccelerationSet4Way accelSet = new AccelerationSet4Way(lonAccel, latAccel, vertAccel, yaw);
	    
		final BrakeSystemStatus brakes = new BrakeSystemStatus(
					new BrakeAppliedStatus(new byte[] { (byte)0xf8 } ), 
					TractionControlStatus.unavailable, 
					AntiLockBrakeStatus.unavailable, 
					StabilityControlStatus.unavailable,
					BrakeBoostApplied.unavailable,
					AuxiliaryBrakeStatus.unavailable
				);
		
		SteeringWheelAngle steeringAngle = new SteeringWheelAngle(0);

		VehicleWidth vehWidth   = new  VehicleWidth(185); 	// Honda Accord 2014 width:   72.8 in -> ~ 185 cm
		VehicleLength vehLength = new VehicleLength(486);	// Honda Accord 2014 length: 191.4 in -> ~ 486 cm
		VehicleSize vehSize = new VehicleSize(vehWidth, vehLength);
		
		FundamentalSituationalStatus fundamental = new FundamentalSituationalStatus(speed, heading, steeringAngle, accelSet,  brakes, vehSize);
		VehSitRecord vehSitRcd1 = new VehSitRecord(tempID, dt, pos, fundamental);
		VehSitRecord vehSitRcd2 = new VehSitRecord(tempID, dt, pos, fundamental);
		
		// create some random crc value
		MsgCRC crc = new MsgCRC();
		byte [] crc_b = new byte[2];
		crc_b[0] = (byte) 1 << 3;
		crc_b[1] = (byte) 1 << 5;
		crc.setValue(crc_b);
		
		VsmType type = new VsmType(new byte[] { CVTypeHelper.bitWiseOr(CVTypeHelper.VsmType.VEHSTAT, CVTypeHelper.VsmType.ELVEH) }) ;
		VehSitDataMessage vsdm = new VehSitDataMessage(SemiDialogID.vehSitData, SemiSequenceID.data,  GroupIDHelper.toGroupID(1), TemporaryIDHelper.toTemporaryID(1), type,  
			    new Bundle(new VehSitRecord[] { vehSitRcd1, vehSitRcd2} ), crc);
		
		System.out.println(vsdm);
		return vsdm;
	}
	
}

