package us.dot.its.jpo.ode.udp.isd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.tomcat.util.buf.HexUtils;

import com.oss.asn1.AbstractData;
import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;
import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;
import com.oss.asn1.INTEGER;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.asn1.j2735.J2735Util;
import us.dot.its.jpo.ode.dds.AbstractSubscriberDepositor;
import us.dot.its.jpo.ode.dds.TrustManager.TrustManagerException;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.semi.DataReceipt;
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
			messagesDeposited++;
		} catch (IOException | DecodeFailedException | DecodeNotSupportedException e) {
			logger.error("Error Sending Isd to SDC", e);
			return new byte[0];
		}
		
		// If we've sent at least 5 messages, get a data receipt
		if (messagesDeposited < 5) {
			trustMgr.setTrustEstablished(false);
			return encodedIsd;
		}

		/*
		 * Send an ISDAcceptance message to confirm deposit
		 */

		IntersectionSituationDataAcceptance acceptance = new IntersectionSituationDataAcceptance();
		acceptance.dialogID = dialogId;
		acceptance.groupID = groupId;
		acceptance.seqID = SemiSequenceID.accept;
		acceptance.recordsSent = new INTEGER(messagesDeposited);

		try {
			// must reuse the requestID from the ISD
			acceptance.requestID = ((IntersectionSituationData) J2735.getPERUnalignedCoder()
					.decode(new ByteArrayInputStream(encodedIsd), new IntersectionSituationData())).requestID;

			logger.info("Extracted requestID from ISD for ISD acceptance message {}",
					HexUtils.toHexString(acceptance.requestID.byteArrayValue()));

			logger.info("Sending Data Acceptance message to SDC: {} ", acceptance.toString());
		} catch (DecodeFailedException | DecodeNotSupportedException e) {
			logger.error("Failed to extract requestID from ISD ", e);
			return new byte[0];
		}

		ByteArrayOutputStream sink = new ByteArrayOutputStream();
		try {
			J2735.getPERUnalignedCoder().encode(acceptance, sink);
		} catch (EncodeFailedException | EncodeNotSupportedException e) {
			logger.error("Error encoding ISD Acceptance message", e);
		}
		byte[] encodedAccept = sink.toByteArray();

		try {
			logger.debug("Sending ISD Acceptance message to SDC.");

			ExecutorService executorService = Executors.newCachedThreadPool(Executors.defaultThreadFactory());

			Future<Object> f = executorService.submit(new DataReceiptReceiver(odeProperties, socket));

			socket.send(new DatagramPacket(encodedAccept, encodedAccept.length,
					new InetSocketAddress(odeProperties.getSdcIp(), odeProperties.getSdcPort())));

			f.get(odeProperties.getServiceRespExpirationSeconds(), TimeUnit.SECONDS);

		} catch (IOException | InterruptedException | ExecutionException e) {
			logger.error("Error sending ISD Acceptance message to SDC", e);
		} catch (TimeoutException e) {
			logger.error("Did not receive ISD data receipt within alotted "
					+ +odeProperties.getServiceRespExpirationSeconds() + " seconds " + e);
		}

		return encodedIsd;
	}
}
