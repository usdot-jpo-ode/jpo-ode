package us.dot.its.jpo.ode.udp.isd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oss.asn1.AbstractData;
import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;
import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;
import com.oss.asn1.INTEGER;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.AbstractSubscriberDepositor;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.dsrc.TemporaryID;
import us.dot.its.jpo.ode.j2735.semi.DataReceipt;
import us.dot.its.jpo.ode.j2735.semi.IntersectionSituationData;
import us.dot.its.jpo.ode.j2735.semi.IntersectionSituationDataAcceptance;
import us.dot.its.jpo.ode.j2735.semi.SemiDialogID;
import us.dot.its.jpo.ode.j2735.semi.SemiSequenceID;
import us.dot.its.jpo.ode.wrapper.MessageConsumer;

public class IsdDepositor extends AbstractSubscriberDepositor {

   public IsdDepositor(OdeProperties odeProps) {
      super(odeProps, odeProps.getIsdDepositorPort());
      consumer = MessageConsumer.defaultByteArrayMessageConsumer(odeProps.getKafkaBrokers(),
            odeProps.getHostId() + this.getClass().getSimpleName(), this);
      consumer.setName(this.getClass().getSimpleName());
   }

   /**
    * TODO - incomplete method
    * 
    * @param encodedIsd
    */
   public void sendDataReceipt(byte[] encodedIsd) {

      /*
       * Send an ISDAcceptance message to confirm deposit
       */

      IntersectionSituationDataAcceptance acceptance = new IntersectionSituationDataAcceptance();
      // acceptance.dialogID = dialogId;
      // acceptance.groupID = groupId;
      // acceptance.requestID = requestId;
      acceptance.seqID = SemiSequenceID.accept;
      acceptance.recordsSent = new INTEGER(trustManager.getSessionMessageCount(getRequestId(encodedIsd)));

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
         logger.debug("DataReceipt: f.isDone(): {}, f.isCancelled(): {}", f.isDone(), f.isCancelled());

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

   @Override
   public Logger getLogger() {
      return LoggerFactory.getLogger(this.getClass());
   }

   public SemiDialogID getDialogId() {
      return SemiDialogID.intersectionSitDataDep;
   }

   public TemporaryID getRequestId(byte[] encodedMsg) {
      TemporaryID reqID = null;
      try {
         reqID = ((IntersectionSituationData) J2735.getPERUnalignedCoder().decode(new ByteArrayInputStream(encodedMsg),
               new IntersectionSituationData())).requestID;

      } catch (DecodeFailedException | DecodeNotSupportedException e) {
         logger.error("Failed to decode ISD message: {}", e);
      }

      return reqID;
   }

}
