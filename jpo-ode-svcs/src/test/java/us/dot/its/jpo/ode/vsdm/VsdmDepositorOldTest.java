package us.dot.its.jpo.ode.vsdm;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
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
public class VsdmDepositorOldTest {
	private String sdcIp = "104.130.170.234";
	private int sdcPort = 46753;
	private int serviceRequestSenderPort = 5555;
	private int vsdmSenderPort = 6666;

	// The ip and port where the SDC will send the ServiceResponse back
	private String returnIp = "104.239.174.231"; // Rackspace test instance ip
	private int returnPort = 12321;

	@Injectable
	OdeProperties mockOdeProperties;

	@Test
	public void depositVsdToSdcOverIpv6() throws Exception {
		final double DEFAULT_LAT = 43.394444; // Wyoming lat/lon
		final double DEFAULT_LON = -107.595;
		final int port = 12321;

		DatagramSocket socket = new DatagramSocket(port);

		VehSitDataMessage vsdm = CVSampleMessageBuilder.buildVehSitDataMessage(DEFAULT_LAT, DEFAULT_LON);
		VsmType vsmType = new VsmType(CVTypeHelper.VsmType.VEHSTAT.arrayValue());
		vsdm.setType(vsmType);
		byte[] payload = CVSampleMessageBuilder.messageToEncodedBytes(vsdm);

		String dst = "2001:4802:7803:104:be76:4eff:fe20:bfb2"; // SDC IPv6
		int dstPort = 46753;

		System.out.println("Sending to SDC, IP: " + dst + " Port: " + dstPort);
		DatagramPacket packet = new DatagramPacket(payload, payload.length, new InetSocketAddress(dst, dstPort));
		socket.send(packet);
	}

	// Runs end to end testing
	@Test
	public void testVsdmDepositor() throws InterruptedException {
		VsdmDepositorOld vsdmDepositorThreaded = new VsdmDepositorOld(sdcIp, sdcPort, returnIp, returnPort,
				serviceRequestSenderPort, vsdmSenderPort);
		vsdmDepositorThreaded.run();
	}

	@Test
	public void testConstructor1() {
		VsdmDepositorOld vsdmDepositor = new VsdmDepositorOld(sdcIp, sdcPort, returnIp, returnPort,
				serviceRequestSenderPort, vsdmSenderPort);
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
				mockOdeProperties.getForwarderPort();
				result = serviceRequestSenderPort;
				mockOdeProperties.getVsdmSenderPort();
				result = vsdmSenderPort;
				mockOdeProperties.getReturnIp();
				result = returnIp;
				mockOdeProperties.getReturnPort();
				result = returnPort;
			}
		};

		VsdmDepositorOld vsdmDepositor = new VsdmDepositorOld(mockOdeProperties);
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
