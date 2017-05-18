package us.dot.its.jpo.ode.vsdm;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.asn1.j2735.CVSampleMessageBuilder;
import us.dot.its.jpo.ode.asn1.j2735.CVTypeHelper;
import us.dot.its.jpo.ode.j2735.semi.VehSitDataMessage;
import us.dot.its.jpo.ode.j2735.semi.VsmType;

@RunWith(JMockit.class)
public class VsdmDepositorTest {
	private String sdcIp = "104.130.170.234";
	private int sdcPort = 46753;
	private int serviceRequestSenderPort = 5556;
	private int vsdmSenderPort = 6666;

	// The ip and port where the SDC will send the ServiceResponse back
	private String returnIp = "104.239.174.231"; // Rackspace test instance ip
	private int returnPort = 12321;

	@Injectable
	OdeProperties mockOdeProperties;
	
    @BeforeClass
    public static void runOnceBeforeClass() {
    	System.out.println("Running this before class");
    	System.out.println("Before: " + System.getProperty("java.net.preferIPv4Stack"));
    	System.out.println("Before: " + System.getProperty("java.net.preferIPv6Addresses"));
    	System.setProperty("java.net.preferIPv4Stack" , "true");
    	System.setProperty("java.net.preferIPv6Addresses" , "true");
    	System.out.println("After: " + System.getProperty("java.net.preferIPv4Stack"));
    	System.out.println("After: " + System.getProperty("java.net.preferIPv6Addresses"));
    }

	@Test
	public void tempTest() throws Exception {
		final double DEFAULT_LAT = 43.394444; // Wyoming lat/lon
		final double DEFAULT_LON = -107.595;
		final int port = 12321;
		DatagramSocket s = new DatagramSocket(port); // on a dual-stack host,
														// this dual-binds to
														// IPv6+IPv4
		VehSitDataMessage vsdm = CVSampleMessageBuilder.buildVehSitDataMessage(DEFAULT_LAT, DEFAULT_LON);
		VsmType vsmType = new VsmType(CVTypeHelper.VsmType.VEHSTAT.arrayValue());
		vsdm.setType(vsmType);
		byte[] payload = CVSampleMessageBuilder.messageToEncodedBytes(vsdm);

		// send to IPv4 and IPv6 addresses
		// 2001:4802:7803:104:be76:4eff:fe20:bfb2 >> Didnt work
		// 104.130.170.234	>> Worked
		// IPv4 FQDN: sdc2.connectedvcs.com  >> Worked
		// IPv6 FQDN: sdc6-2.connectedvcs.com

		//String dst = "2001:4802:7803:104:be76:4eff:fe20:bfb2";
		//String dst = "162.242.218.130";  	// ode instance
		String dst = "2001:4802:7801:102:be76:4eff:fe20:eb5";  	// ode instance
		int dstPort = 46753;
		System.out.println("Sending to: " + dst);
		DatagramPacket p = new DatagramPacket(payload, payload.length, new InetSocketAddress(dst, dstPort));
        //DatagramPacket p = new DatagramPacket(payload, payload.length, InetAddress.getByName(dst), dstPort);
		s.send(p);
	}

	// Runs end to end testing
	@Test
	public void testVsdmDepositor() throws InterruptedException {
		VsdmDepositor vsdmDepositorThreaded = new VsdmDepositor(sdcIp, sdcPort, returnIp, returnPort,
				serviceRequestSenderPort, vsdmSenderPort);
		vsdmDepositorThreaded.run();
	}

	@Test
	public void testConstructor1() {
		VsdmDepositor vsdmDepositor = new VsdmDepositor(sdcIp, sdcPort, returnIp, returnPort, serviceRequestSenderPort,
				vsdmSenderPort);
		assertEquals(vsdmDepositor.getSdcIp(), sdcIp);
		assertEquals(vsdmDepositor.getSdcPort(), sdcPort);
		assertEquals(vsdmDepositor.getReturnIp(), returnIp);
		assertEquals(vsdmDepositor.getReturnPort(), returnPort);
		assertEquals(vsdmDepositor.getServiceRequestSenderPort(), serviceRequestSenderPort);
		assertEquals(vsdmDepositor.getVsdmSenderPort(), vsdmSenderPort);
	}

	@Test
	public void testConstructor2() {
		new Expectations() {
			{
				mockOdeProperties.getSdcIp();
				result = sdcIp;
				mockOdeProperties.getSdcPort();
				result = sdcPort;
				mockOdeProperties.getServiceRequestSenderPort();
				result = serviceRequestSenderPort;
				mockOdeProperties.getVsdmSenderPort();
				result = vsdmSenderPort;
				mockOdeProperties.getReturnIp();
				result = returnIp;
				mockOdeProperties.getReturnPort();
				result = returnPort;
			}
		};

		VsdmDepositor vsdmDepositor = new VsdmDepositor(mockOdeProperties);
		assertEquals(vsdmDepositor.getSdcIp(), sdcIp);
		assertEquals(vsdmDepositor.getSdcPort(), sdcPort);
		assertEquals(vsdmDepositor.getReturnIp(), returnIp);
		assertEquals(vsdmDepositor.getReturnPort(), returnPort);
		assertEquals(vsdmDepositor.getServiceRequestSenderPort(), serviceRequestSenderPort);
		assertEquals(vsdmDepositor.getVsdmSenderPort(), vsdmSenderPort);
	}

	@Test
	public void testReceiveServiceResponse(@Mocked DatagramSocket mockSocket) throws IOException {
		new Expectations() {
			{
				mockSocket.receive((DatagramPacket) any);

			}
		};

		VsdmSender vsdmSender = new VsdmSender(sdcIp, sdcPort, vsdmSenderPort);
		vsdmSender.receiveVsdServiceResponse();
	}

	@Test
	public void testSendVsdMessage(@Mocked DatagramSocket mockSocket) throws IOException {
		new Expectations() {
			{
				mockSocket.send((DatagramPacket) any);

			}
		};

		VsdmSender vsdmSender = new VsdmSender(sdcIp, sdcPort, vsdmSenderPort);
		vsdmSender.sendVsdMessage();
	}
}
