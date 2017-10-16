package us.dot.its.jpo.ode.udp.isd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oss.asn1.AbstractData;
import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;
import com.oss.asn1.INTEGER;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.AbstractSubscriberDepositor;
import us.dot.its.jpo.ode.j2735.dsrc.TemporaryID;
import us.dot.its.jpo.ode.j2735.semi.DataReceipt;
import us.dot.its.jpo.ode.j2735.semi.GroupID;
import us.dot.its.jpo.ode.j2735.semi.IntersectionSituationData;
import us.dot.its.jpo.ode.j2735.semi.IntersectionSituationDataAcceptance;
import us.dot.its.jpo.ode.j2735.semi.SemiDialogID;
import us.dot.its.jpo.ode.j2735.semi.SemiSequenceID;
import us.dot.its.jpo.ode.wrapper.serdes.IntersectionSituationDataDeserializer;

public class IsdDepositor extends AbstractSubscriberDepositor {

   private IntersectionSituationDataDeserializer deserializer;

   public IsdDepositor(OdeProperties odeProps) {
      super(odeProps, odeProps.getIsdDepositorPort());
      this.deserializer = new IntersectionSituationDataDeserializer();
   }

   /**
    * TODO - utilize this method
    * 
    * @param encodedIsd
    */
   public void sendDataReceipt(byte[] encodedIsd) {

      /*
       * Send an ISDAcceptance message to confirm deposit
       */
      IntersectionSituationDataAcceptance acceptance = new IntersectionSituationDataAcceptance();
      acceptance.dialogID = SemiDialogID.intersectionSitDataQuery;
      acceptance.groupID = new GroupID(OdeProperties.getJpoOdeGroupId());
      acceptance.requestID = getRequestId(encodedIsd);
      acceptance.seqID = SemiSequenceID.accept;
      acceptance.recordsSent = new INTEGER(trustManager.getSessionMessageCount(getRequestId(encodedIsd)));

      byte[] encodedAccept = null;
      try {
         encodedAccept = coder.encode(acceptance).array();
      } catch (EncodeFailedException | EncodeNotSupportedException e) {
         logger.error("Error encoding ISD non-repudiation message", e);
         return;
      }

      // Switching from socket.send to socket.receive in one thread is
      // slower than non-repud round trip time so we must lead this by
      // creating a socket.receive thread

      try {
         Future<AbstractData> f = Executors.newSingleThreadExecutor().submit(new DataReceiptReceiver(odeProperties, socket));
         logger.debug("Submitted DataReceiptReceiver to listen on port {}", socket.getLocalPort());

         String hexMsg = HexUtils.toHexString(encodedAccept);
         logger.debug("Sending ISD non-repudiation message to SDC {} ", hexMsg);

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

   @Override
   public Logger getLogger() {
      return LoggerFactory.getLogger(this.getClass());
   }

   @Override
   public SemiDialogID getDialogId() {
      return SemiDialogID.intersectionSitDataDep;
   }

   @Override
   public TemporaryID getRequestId(byte[] serializedMsg) {
      IntersectionSituationData msg = deserializer.deserialize(null, serializedMsg);
      return msg.getRequestID();
   }
   
   @Override
   public byte[] encodeMessage(byte[] serializedMsg) {
      IntersectionSituationData msg = deserializer.deserialize(null, serializedMsg);
      
      byte[] encodedMsg = null;
      try {
         encodedMsg = coder.encode(msg).array();
      } catch (EncodeFailedException | EncodeNotSupportedException e) {
         logger.error("Failed to encode serialized ISD for sending.", e);
      }
      
      return encodedMsg;
   }

}
