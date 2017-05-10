package us.dot.its.jpo.ode.asn1.j2735;

import us.dot.its.jpo.ode.asn1.j2735.CVSampleMessageBuilder;
import us.dot.its.jpo.ode.asn1.j2735.UnitTestHelper;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.semi.ServiceRequest;
import us.dot.its.jpo.ode.j2735.semi.ServiceResponse;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.oss.asn1.Coder;
import com.oss.asn1.ControlTableNotFoundException;
import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;
import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;
import com.oss.asn1.InitializationException;

public class CVMessageTest {

	private static Coder coder;
	private static final String MESSAGE_DIR = "./src/test/resources/CVMessages/";
	private static CoderHelper helper;
	
	static final private boolean isDebugOutput = false;

	@BeforeClass
	public static void init() throws ControlTableNotFoundException,
			InitializationException {
		UnitTestHelper.initLog4j(isDebugOutput);
		
		J2735.initialize();
		coder = J2735.getPERUnalignedCoder();
		coder.enableEncoderDebugging();
		coder.enableDecoderDebugging();
		helper = new CoderHelper(coder, MESSAGE_DIR);
	}
	
	@Test
	public void testVehicleSituationDataServiceRequest() throws EncodeFailedException, DecodeFailedException, 
		FileNotFoundException, EncodeNotSupportedException, DecodeNotSupportedException {
		ServiceRequest vsr = CVSampleMessageBuilder.buildVehicleSituationDataServiceRequest();
		helper.encodeDecodeMessage(vsr, "VehicleSituationDataServiceRequest.uper");
	}
	
	@Test
	public void testVehicleSituationDataServiceResponse() throws EncodeFailedException, DecodeFailedException, 
		FileNotFoundException, EncodeNotSupportedException, DecodeNotSupportedException {
		ServiceResponse vsr = CVSampleMessageBuilder.buildVehicleSituationDataServiceResponse();
		helper.encodeDecodeMessage(vsr, "VehicleSituationDataServiceResponse.uper");
	}

	@Test
	public void testVehSitDataMessage() throws EncodeFailedException, EncodeNotSupportedException, 
		DecodeFailedException, DecodeNotSupportedException, IOException {
		VehSitDataMessage vsdm = CVSampleMessageBuilder.buildVehSitDataMessage(43.394444, -107.595);
		helper.encodeDecodeMessage(vsdm, "VehSitDataMessage.uper");
	}
	
	@AfterClass
	public static void deinit() throws ControlTableNotFoundException,
			InitializationException {
		J2735.deinitialize();
	}

}
