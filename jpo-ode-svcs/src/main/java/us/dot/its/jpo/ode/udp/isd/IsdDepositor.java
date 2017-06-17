package us.dot.its.jpo.ode.udp.isd;

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
import java.util.concurrent.ThreadFactory;

import com.oss.asn1.AbstractData;
import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;
import com.oss.asn1.INTEGER;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.AbstractSubscriberDepositor;
import us.dot.its.jpo.ode.j2735.semi.DataReceipt;
import us.dot.its.jpo.ode.j2735.semi.IntersectionSituationDataAcceptance;
import us.dot.its.jpo.ode.j2735.semi.SemiDialogID;
import us.dot.its.jpo.ode.j2735.semi.SemiSequenceID;

public class IsdDepositor extends AbstractSubscriberDepositor<String, byte[]> {

   private ExecutorService pool;

   public IsdDepositor(OdeProperties odeProps) {
      super(odeProps, odeProps.getIsdDepositorPort(), SemiDialogID.intersectionSitDataDep);

      // execService =
      // Executors.newCachedThreadPool(Executors.defaultThreadFactory());
      pool = Executors.newSingleThreadExecutor();
   }

   @Override
   protected byte[] deposit() {
      /*
       * The record.value() will return an encoded ISD
       */
      byte[] encodedIsd = record.value();

      try {
         logger.debug("Depositor received ISD: {}", HexUtils.toHexString(encodedIsd));

         logger.debug("Sending ISD to SDC IP: {}:{} from port: {}", odeProperties.getSdcIp(),
               odeProperties.getSdcPort(), socket.getLocalPort());
         socket.send(new DatagramPacket(encodedIsd, encodedIsd.length,
               new InetSocketAddress(odeProperties.getSdcIp(), odeProperties.getSdcPort())));
         messagesSent++;
      } catch (IOException e) {
         logger.error("Error Sending Isd to SDC", e);
         return new byte[0];
      }

      // TODO - determine more dynamic method of re-establishing trust
      // If we've sent at least 5 messages, get a data receipt and then end
      // trust session
      logger.info("ISDs sent since session start: {}/{}", messagesSent,
            odeProperties.getMessagesUntilTrustReestablished());
      if (messagesSent >= odeProperties.getMessagesUntilTrustReestablished()) {
         trustMgr.setTrustEstablished(false);
         sendDataReceipt(encodedIsd);
      }

      return encodedIsd;
   }

   public void sendDataReceipt(byte[] encodedIsd) {

      /*
       * Send an ISDAcceptance message to confirm deposit
       */

      IntersectionSituationDataAcceptance acceptance = new IntersectionSituationDataAcceptance();
      acceptance.dialogID = dialogId;
      acceptance.groupID = groupId;
      acceptance.requestID = requestId;
      acceptance.seqID = SemiSequenceID.accept;
      acceptance.recordsSent = new INTEGER(messagesSent);

      ByteArrayOutputStream sink = new ByteArrayOutputStream();
      try {
         coder.encode(acceptance, sink);
      } catch (EncodeFailedException | EncodeNotSupportedException e) {
         logger.error("Error encoding ISD non-repudiation message", e);
      }

      byte[] encodedAccept = sink.toByteArray();

      // Switching from socket.send to socket.receive in one thread is
      // slower than non-repud round trip time so we must lead this by
      // creating a socket.receive thread

      try {
         Future<AbstractData> f = pool.submit(new DataReceiptReceiver(odeProperties, socket));
         logger.debug("Submitted DataReceiptReceiver to listen on port {}", socket.getPort());

         logger.debug("Sending ISD non-repudiation message to SDC {} ", HexUtils.toHexString(encodedAccept));

         socket.send(new DatagramPacket(encodedAccept, encodedAccept.length,
               new InetSocketAddress(odeProperties.getSdcIp(), odeProperties.getSdcPort())));

         DataReceipt receipt = (DataReceipt) f.get(odeProperties.getDataReceiptExpirationSeconds(), TimeUnit.SECONDS);

         if (null != receipt) {
            logger.debug("Successfully received data receipt from SDC {}", receipt);
         } else {
            throw new IOException("Received invalid packet.");
         }

      } catch (IOException | InterruptedException | ExecutionException e) {
         logger.error("Error sending ISD Acceptance message to SDC", e);
      } catch (TimeoutException e) {
         logger.error("Did not receive ISD data receipt within alotted "
               + +odeProperties.getDataReceiptExpirationSeconds() + " seconds " + e);
      }

   }
}
