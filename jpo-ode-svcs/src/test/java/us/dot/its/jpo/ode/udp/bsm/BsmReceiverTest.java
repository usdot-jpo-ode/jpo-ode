package us.dot.its.jpo.ode.udp.bsm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.Ignore;
import org.junit.Test;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.SerializableMessageProducerPool;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735MessageFrame;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssAsn1Coder;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssBsmPart2Content.OssBsmPart2Exception;
import us.dot.its.jpo.ode.udp.AbstractUdpReceiverPublisher.UdpReceiverException;
import us.dot.its.jpo.ode.udp.bsm.BsmReceiver;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

public class BsmReceiverTest {

	@Injectable
	OdeProperties mockOdeProperties;

	@Mocked
	OssAsn1Coder mockedOssAsn1Coder;

	@Mocked
	DatagramSocket mockedDatagramSocket;

	@SuppressWarnings("rawtypes")
	@Mocked
	SerializableMessageProducerPool mockedSMPP;

	@SuppressWarnings("rawtypes")
	@Mocked
	MessageProducer mockedMessageProducer;

	@Test
	public void testConstructor() throws SocketException {

		new Expectations() {
			{
				new OssAsn1Coder();
				mockOdeProperties.getBsmReceiverPort();
				result = 1234;

				new DatagramSocket(1234);

				MessageProducer.defaultStringMessageProducer(anyString, anyString);
			}
		};
		new BsmReceiver(mockOdeProperties);
	}

	@SuppressWarnings({ "unchecked", "resource" })
	@Test
	public void testRun(@Mocked DatagramPacket mockedDatagramPacket, @Mocked J2735MessageFrame mockedJ2735MessageFrame)
			throws IOException, DecoderException {
		new Expectations() {
			{
				new OssAsn1Coder();
				result = mockedOssAsn1Coder;

				mockOdeProperties.getBsmReceiverPort();
				result = 1234;

				new DatagramSocket(1234);
				result = mockedDatagramSocket;

				MessageProducer.defaultByteArrayMessageProducer(anyString, anyString);
				MessageProducer.defaultStringMessageProducer(anyString, anyString);

				new DatagramPacket((byte[]) any, anyInt);
				result = mockedDatagramPacket;

				String sampleBsmPacketStr = "1234560014223344";
				byte[] sampleBsmPacketByte = Hex.decodeHex(sampleBsmPacketStr.toCharArray());
				byte[] bsmMsgFrameByte = BsmReceiver.extractBsmMessageFrame(sampleBsmPacketByte);

				mockedDatagramPacket.getLength();
				result = sampleBsmPacketByte.length;

				mockedDatagramPacket.getData();
				result = sampleBsmPacketByte;

				mockedDatagramPacket.getLength();
				result = sampleBsmPacketByte.length;

				mockedOssAsn1Coder.decodeUPERMessageFrameBytes(bsmMsgFrameByte);
				result = mockedJ2735MessageFrame;
				mockedMessageProducer.send(anyString, null, anyString);
				mockedMessageProducer.send(anyString, null, (Byte[]) any);
			}
		};
		BsmReceiver bsmReceiver = new BsmReceiver(mockOdeProperties);
		bsmReceiver.setStopped(true);
		bsmReceiver.run();
	}

	@Test
	public void testRunException(@Mocked DatagramPacket mockedDatagramPacket, @Mocked J2735Bsm mockedJ2735Bsm)
			throws IOException {
		new Expectations() {
			{
				new OssAsn1Coder();
				result = mockedOssAsn1Coder;

				mockOdeProperties.getBsmReceiverPort();
				result = 1234;

				new DatagramSocket(1234);
				result = mockedDatagramSocket;

				MessageProducer.defaultStringMessageProducer(anyString, anyString);
				new DatagramPacket((byte[]) any, anyInt);
				result = mockedDatagramPacket;

				MessageProducer.defaultByteArrayMessageProducer(anyString, anyString);
				new DatagramPacket((byte[]) any, anyInt);
				result = mockedDatagramPacket;

				mockedDatagramSocket.receive(mockedDatagramPacket);
				result = new SocketException();
			}
		};
		BsmReceiver bsmReceiver = new BsmReceiver(mockOdeProperties);
		bsmReceiver.setStopped(true);
		bsmReceiver.run();
	}

	@Test
	public void testExtractBsmMessageFrame() throws SocketException, DecoderException {
		String sampleBsmPacketStr = "1234560014223344";
		byte[] sampleBsmPacketByte = Hex.decodeHex(sampleBsmPacketStr.toCharArray());
		byte[] bsmMsgFrameByte = BsmReceiver.extractBsmMessageFrame(sampleBsmPacketByte);
		String bsmMsgFrameStr = Hex.encodeHexString(bsmMsgFrameByte);
		assertEquals("0014223344", bsmMsgFrameStr);
	}

	@Test
	public void testPublishBsmError(@Mocked J2735Bsm mockedJ2735Bsm) throws SocketException, UdpReceiverException {
		byte[] msg = { 0x01, 0x02 };
		new Expectations() {
			{
				new OssAsn1Coder();
				result = mockedOssAsn1Coder;

				mockedOssAsn1Coder.decodeUPERMessageFrameBytes(msg);
				result = new Asn1Object();
			}
		};
		BsmReceiver bsmReceiver = new BsmReceiver(mockOdeProperties);
		bsmReceiver.publishBsm(msg);
	}

	@Test
	public void testPublishBsmError2(@Mocked J2735Bsm mockedJ2735Bsm) throws SocketException, UdpReceiverException {
		byte[] msg = { 0x01, 0x02 };
		new Expectations() {
			{
				new OssAsn1Coder();
				result = mockedOssAsn1Coder;
				mockedOssAsn1Coder.decodeUPERMessageFrameBytes(msg);
				result = new OssBsmPart2Exception("");
			}
		};
		BsmReceiver bsmReceiver = new BsmReceiver(mockOdeProperties);
		bsmReceiver.publishBsm(msg);
	}
}
