package us.dot.its.jpo.ode.udp.isd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;

import org.apache.tomcat.util.buf.HexUtils;

import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;
import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;
import com.oss.asn1.INTEGER;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.AbstractSubscriberDepositor;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.semi.GroupID;
import us.dot.its.jpo.ode.j2735.semi.IntersectionSituationData;
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
			logger.debug("Depositor received ISD: {}",
					((IntersectionSituationData) J2735.getPERUnalignedCoder()
							.decode(new ByteArrayInputStream((byte[]) record.value()), new IntersectionSituationData()))
									.toString());
			logger.debug("Sending Isd to SDC IP: {}:{} from port: {}", odeProperties.getSdcIp(),
					odeProperties.getSdcPort(), socket.getLocalPort());
			socket.send(new DatagramPacket(encodedIsd, encodedIsd.length,
					new InetSocketAddress(odeProperties.getSdcIp(), odeProperties.getSdcPort())));
		} catch (IOException | DecodeFailedException | DecodeNotSupportedException e) {
			logger.error("Error Sending Isd to SDC", e);
			return new byte[0];
		}

		/*
		 * Send an ISDAcceptance message to confirm deposit
		 */

//		IntersectionSituationDataAcceptance acceptance = new IntersectionSituationDataAcceptance();
//		acceptance.dialogID = dialogId;
//		acceptance.groupID = groupId;
//		acceptance.seqID = SemiSequenceID.accept;
//		acceptance.recordsSent = new INTEGER(1);
//		try {
//			// must reuse the requestID from the ISD
//			acceptance.requestID = ((IntersectionSituationData) J2735.getPERUnalignedCoder()
//					.decode(new ByteArrayInputStream(encodedIsd), new IntersectionSituationData())).requestID;
//
//			logger.info("Extracted requestID from ISD for ISD acceptance message {}",
//					HexUtils.toHexString(acceptance.requestID.byteArrayValue()));
//		} catch (DecodeFailedException | DecodeNotSupportedException e) {
//			logger.error("Failed to extract requestID from ISD ", e);
//			return new byte[0];
//		}
//
//		ByteArrayOutputStream sink = new ByteArrayOutputStream();
//		try {
//			J2735.getPERUnalignedCoder().encode(acceptance, sink);
//		} catch (EncodeFailedException | EncodeNotSupportedException e) {
//			logger.error("Error encoding ISD Acceptance message", e);
//		}
//		byte[] encodedAccept = sink.toByteArray();
//
//		try {
//			logger.debug("Sending ISD Acceptance message to SDC.");
//			socket.send(new DatagramPacket(encodedAccept, encodedAccept.length,
//					new InetSocketAddress(odeProperties.getSdcIp(), odeProperties.getSdcPort())));
//		} catch (IOException e) {
//			logger.error("Error sending ISD Acceptance message to SDC", e);
//		}
//
//		try {
//			byte[] buffer = new byte[odeProperties.getServiceResponseBufferSize()];
//			logger.debug("Waiting for ISD Data Receipt from SDC...");
//			DatagramPacket responseDp = new DatagramPacket(buffer, buffer.length);
//			socket.receive(responseDp);
//		} catch (IOException e) {
//			logger.error("Error receiving ISD Receipt message to SDC", e);
//		}

		return encodedIsd;
	}

}
