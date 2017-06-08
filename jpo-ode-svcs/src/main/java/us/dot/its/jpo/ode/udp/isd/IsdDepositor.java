package us.dot.its.jpo.ode.udp.isd;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Random;

import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;
import com.oss.asn1.INTEGER;
import com.oss.asn1.PERUnalignedCoder;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.asn1.j2735.J2735Util;
import us.dot.its.jpo.ode.dds.AbstractSubscriberDepositor;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.dsrc.TemporaryID;
import us.dot.its.jpo.ode.j2735.semi.DataReceipt;
import us.dot.its.jpo.ode.j2735.semi.GroupID;
import us.dot.its.jpo.ode.j2735.semi.IntersectionSituationDataAcceptance;
import us.dot.its.jpo.ode.j2735.semi.SemiDialogID;
import us.dot.its.jpo.ode.j2735.semi.SemiSequenceID;

public class IsdDepositor extends AbstractSubscriberDepositor<String, byte[]> {

	public IsdDepositor(OdeProperties odeProps) {
		super(odeProps, odeProps.getIsdDepositorPort(), SemiDialogID.intersectionSitDataDep);
	}

	@Override
	protected byte[] deposit() {
		/*
		 * The record.value() will return an encoded ISD
		 */
		byte[] encodedIsd = record.value();

		try {
			logger.debug("Sending Isd to SDC IP: {}:{} from port: {}", odeProperties.getSdcIp(),
					odeProperties.getSdcPort(), socket.getLocalPort());
			socket.send(new DatagramPacket(encodedIsd, encodedIsd.length,
					new InetSocketAddress(odeProperties.getSdcIp(), odeProperties.getSdcPort())));
		} catch (IOException e) {
			logger.error("Error Sending Isd to SDC", e);
		}
		
		/*
		 * Send an ISDAcceptance message to confirm deposit
		 */
		IntersectionSituationDataAcceptance acceptance = new IntersectionSituationDataAcceptance();
		acceptance.dialogID = dialogId;
		acceptance.groupID = new GroupID(OdeProperties.JPO_ODE_GROUP_ID);
		acceptance.requestID = new TemporaryID(ByteBuffer.allocate(4).putInt(new Random().nextInt(256)).array());
		acceptance.seqID = SemiSequenceID.accept;
		acceptance.recordsSent = new INTEGER(1);
		
		
		ByteArrayOutputStream sink = new ByteArrayOutputStream();
		try {
			J2735.getPERUnalignedCoder().encode(acceptance, sink);
		} catch (EncodeFailedException | EncodeNotSupportedException e) {
			logger.error("Error encoding ISD Acceptance message", e);
		}
		byte[] encodedAccept = sink.toByteArray();
		
		try {
			logger.debug("Sending ISD Acceptance message to SDC.");
			socket.send(new DatagramPacket(encodedAccept, encodedAccept.length,
					new InetSocketAddress(odeProperties.getSdcIp(), odeProperties.getSdcPort())));
		} catch (IOException e) {
			logger.error("Error sending ISD Acceptance message to SDC", e);
		}
		
		try {
			byte[] buffer = new byte[odeProperties.getServiceResponseBufferSize()];
			logger.debug("Waiting for ISD Data Receipt from SDC...");
			DatagramPacket responseDp = new DatagramPacket(buffer, buffer.length);
			socket.receive(responseDp);
		} catch (IOException e) {
			logger.error("Error receiving ISD Receipt message to SDC", e);
		}

		return encodedIsd;
	}

}
