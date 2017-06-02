package us.dot.its.jpo.ode.bsm;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
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
import us.dot.its.jpo.ode.plugin.j2735.oss.OssAsn1Coder;
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
				new SerializableMessageProducerPool<>(mockOdeProperties);
				MessageProducer.defaultStringMessageProducer(anyString, anyString);
			}
		};
		new BsmReceiver(mockOdeProperties);
	}

	@Test
	public void testConstructorException() throws SocketException {

		new Expectations() {
			{
				new OssAsn1Coder();
				result = mockedOssAsn1Coder;

				mockOdeProperties.getBsmReceiverPort();
				result = 1234;

				new DatagramSocket(1234);
				result = new SocketException();
			}
		};
		new BsmReceiver(mockOdeProperties);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRun(@Mocked DatagramPacket mockedDatagramPacket, @Mocked J2735Bsm mockedJ2735Bsm)
			throws IOException {
		byte[] msg = { 0x00, 0x00 };
		new Expectations() {
			{
				new OssAsn1Coder();
				result = mockedOssAsn1Coder;

				mockOdeProperties.getBsmReceiverPort();
				result = 1234;

				new DatagramSocket(1234);

				new SerializableMessageProducerPool<>(mockOdeProperties);

				MessageProducer.defaultStringMessageProducer(anyString, anyString);
				new DatagramPacket((byte[]) any, anyInt);
				result = mockedDatagramPacket;

				mockedDatagramPacket.getLength();
				result = 2;

				mockedOssAsn1Coder.decodeUPERBsmBytes(msg);
				result = mockedJ2735Bsm;
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

				new SerializableMessageProducerPool<>(mockOdeProperties);

				MessageProducer.defaultStringMessageProducer(anyString, anyString);
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

	@SuppressWarnings("unchecked")
	@Test
	public void testDecodeData(@Mocked J2735Bsm mockedJ2735Bsm) throws SocketException {
		byte[] msg = { 0x01, 0x02 };
		new Expectations() {
			{
				mockedOssAsn1Coder.decodeUPERBsmBytes(msg);
				result = mockedJ2735Bsm;
				mockedMessageProducer.send(anyString, null, anyString);
				mockedMessageProducer.send(anyString, null, (Byte[]) any);
			}
		};
		BsmReceiver bsmReceiver = new BsmReceiver(mockOdeProperties);
		bsmReceiver.decodeData(msg);
	}

	@Test
	public void testDecodeDataError(@Mocked J2735Bsm mockedJ2735Bsm) throws SocketException {
		byte[] msg = { 0x01, 0x02 };
		new Expectations() {
			{
				mockedOssAsn1Coder.decodeUPERBsmBytes(msg);
				result = new Asn1Object();
			}
		};
		BsmReceiver bsmReceiver = new BsmReceiver(mockOdeProperties);
		bsmReceiver.decodeData(msg);
	}
}
