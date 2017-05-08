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
import us.dot.its.jpo.ode.j2735.semi.ServiceResponse;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage;
import us.dot.its.jpo.ode.j2735.semi.VehSitRecord;
import us.dot.its.jpo.ode.j2735.semi.VsmType;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage.Bundle;
import gov.usdot.asn1.j2735.CVTypeHelper;
import gov.usdot.asn1.j2735.J2735Util;
import us.dot.its.jpo.ode.common.asn1.GroupIDHelper;
import us.dot.its.jpo.ode.common.asn1.TemporaryIDHelper;
import us.dot.its.jpo.ode.common.asn1.TransmissionAndSpeedHelper;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.codec.binary.Hex;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.oss.asn1.AbstractData;
import com.oss.asn1.Coder;
import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;

public class VsdmDepositorSDCTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {		
		J2735.initialize();
	}
	
	@Test 
	public void testVsdMessageDeposit() throws Exception {
		String targetHost = "104.130.170.234";
		int targetPort = 46753;
		int selfPort = 6666;
		VsdmDepositor vsdmDepositor = new VsdmDepositor(targetHost, targetPort, selfPort);
		vsdmDepositor.depositVsdm();
	}
}

