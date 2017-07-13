package us.dot.its.jpo.ode.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.time.ZonedDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oss.asn1.AbstractData;
import com.oss.asn1.Coder;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.asn1.j2735.J2735Util;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.dsrc.DDateTime;
import us.dot.its.jpo.ode.j2735.dsrc.DDay;
import us.dot.its.jpo.ode.j2735.dsrc.DHour;
import us.dot.its.jpo.ode.j2735.dsrc.DMinute;
import us.dot.its.jpo.ode.j2735.dsrc.DMonth;
import us.dot.its.jpo.ode.j2735.dsrc.DOffset;
import us.dot.its.jpo.ode.j2735.dsrc.DSecond;
import us.dot.its.jpo.ode.j2735.dsrc.DYear;
import us.dot.its.jpo.ode.j2735.dsrc.TemporaryID;
import us.dot.its.jpo.ode.j2735.semi.GroupID;
import us.dot.its.jpo.ode.j2735.semi.SemiDialogID;
import us.dot.its.jpo.ode.j2735.semi.SemiSequenceID;
import us.dot.its.jpo.ode.j2735.semi.ServiceRequest;
import us.dot.its.jpo.ode.j2735.semi.ServiceResponse;
import us.dot.its.jpo.ode.j2735.semi.Sha256Hash;
import us.dot.its.jpo.ode.udp.isd.ServiceResponseReceiver;

/*
 * This class can be used to create a trust session or create a service response reply.
 */
public class TrustManager {

   private OdeProperties odeProperties;
   private static Coder coder = J2735.getPERUnalignedCoder();
   private Logger logger = LoggerFactory.getLogger(this.getClass());
   private DatagramSocket socket = null;

   private ExecutorService execService;
   private boolean trustEstablished = false;

   public TrustManager(OdeProperties odeProps, DatagramSocket socket) {
      this.odeProperties = odeProps;
      this.socket = socket;

       execService =
       Executors.newCachedThreadPool(Executors.defaultThreadFactory());
   }

   public ServiceResponse createServiceResponse(ServiceRequest request) {
      ServiceResponse response = new ServiceResponse();
      response.setDialogID(request.getDialogID());

      ZonedDateTime expiresAt = ZonedDateTime.now().plusSeconds(odeProperties.getServiceRespExpirationSeconds());
      response.setExpiration(new DDateTime(new DYear(expiresAt.getYear()), new DMonth(expiresAt.getMonthValue()),
            new DDay(expiresAt.getDayOfMonth()), new DHour(expiresAt.getHour()), new DMinute(expiresAt.getMinute()),
            new DSecond(expiresAt.getSecond()), new DOffset(0)));

      response.setGroupID(request.getGroupID());
      response.setRequestID(request.getRequestID());
      response.setSeqID(SemiSequenceID.svcResp);

      response.setHash(new Sha256Hash(ByteBuffer.allocate(32).putInt(1).array()));
      return response;
   }

   public void sendServiceResponse(ServiceResponse response, String ip, int port) {
      try {
         logger.debug("Sending ServiceResponse {} to {}:{}", response, ip, port);

         byte[] responseBytes = J2735Util.encode(coder, response);
         socket.send(new DatagramPacket(responseBytes, responseBytes.length, new InetSocketAddress(ip, port)));
      } catch (IOException e) {
         logger.error("Error Sending ServiceResponse", e);
      }
   }

   public void sendServiceRequest(ServiceRequest request, String ip, int port) {
      try {
         trustEstablished = false;

         byte[] requestBytes = J2735Util.encode(coder, request);
         logger.debug("Sending ServiceRequest to {}:{}", ip, port);
         socket.send(new DatagramPacket(requestBytes, requestBytes.length, new InetSocketAddress(ip, port)));
      } catch (IOException e) {
         logger.error("Error ServiceRequest", e);
      }
   }

   /**
    * Creates and sends a service request with a threaded response listener. Returns 
    * boolean success.
    * @param destIp
    * @param destPort
    * @param requestId
    * @param dialogId
    * @return
    */
   public boolean establishTrust(TemporaryID requestId, SemiDialogID dialogId) {
      
      if (this.isTrustEstablished()) {
         return true;
      }
      
      logger.info("Starting trust establishment...");

      int retriesLeft = odeProperties.getTrustRetries();

      while (retriesLeft > 0 && !this.isTrustEstablished()) {
         if (retriesLeft < odeProperties.getTrustRetries()) {
            logger.debug("Failed to establish trust, retrying {} more time(s).", retriesLeft);
         }
         performHandshake(requestId, dialogId);
         --retriesLeft;
      }
      
      return trustEstablished;
      
   }
   
   public boolean performHandshake(TemporaryID requestId, SemiDialogID dialogId) {
      logger.info("Establishing trust...");

      // Launch a trust manager thread to listen for the service response
      try {

         Future<AbstractData> f = execService.submit(new ServiceResponseReceiver(odeProperties, socket));

         ServiceRequest request = new ServiceRequest(dialogId, SemiSequenceID.svcReq, new GroupID(OdeProperties.getJpoOdeGroupId()), requestId);
         this.sendServiceRequest(request, odeProperties.getSdcIp(), odeProperties.getSdcPort());

         ServiceResponse response = (ServiceResponse) f.get(odeProperties.getServiceRespExpirationSeconds(),
               TimeUnit.SECONDS);

         if (response.getRequestID().equals(request.getRequestID())) {
            // Matching IDs indicates a successful handshake
            trustEstablished = true;
            String reqid = HexUtils.toHexString(request.getRequestID().byteArrayValue());
            logger.info("Trust established, session requestID: {}",
                  reqid);
         } else {
            trustEstablished = false;
            logger.error("Received ServiceResponse from SDC but the requestID does not match! {} != {}",
                  response.getRequestID(), request.getRequestID());
         }

      } catch (TimeoutException e) {
         trustEstablished = false;
         logger.error("Did not receive Service Response within alotted "
               + +odeProperties.getServiceRespExpirationSeconds() + " seconds.", e);

      } catch (InterruptedException | ExecutionException e) {
         trustEstablished = false;
         logger.error("Trust establishment interrupted.", e);
      }
      return trustEstablished;
   }

   public boolean isTrustEstablished() {
      return trustEstablished;
   }

   public void setTrustEstablished(boolean trustEstablished) {
      this.trustEstablished = trustEstablished;
   }
}
