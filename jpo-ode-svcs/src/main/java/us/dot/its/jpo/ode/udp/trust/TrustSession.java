package us.dot.its.jpo.ode.udp.trust;

import java.net.DatagramSocket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oss.asn1.AbstractData;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.j2735.dsrc.TemporaryID;
import us.dot.its.jpo.ode.j2735.semi.GroupID;
import us.dot.its.jpo.ode.j2735.semi.SemiDialogID;
import us.dot.its.jpo.ode.j2735.semi.SemiSequenceID;
import us.dot.its.jpo.ode.j2735.semi.ServiceRequest;
import us.dot.its.jpo.ode.j2735.semi.ServiceResponse;
import us.dot.its.jpo.ode.udp.UdpUtil;
import us.dot.its.jpo.ode.udp.UdpUtil.UdpUtilException;
import us.dot.its.jpo.ode.udp.isd.ServiceResponseReceiver;

/*
 * Trust Session Manager
 */
public class TrustSession {

   private OdeProperties odeProperties;
   private Logger logger = LoggerFactory.getLogger(this.getClass());
   private DatagramSocket socket = null;

   private boolean trustEstablished = false;

   public TrustSession(OdeProperties odeProps, DatagramSocket socket) {
      this.odeProperties = odeProps;
      this.socket = socket;
   }

   /**
    * Creates and sends a service request with a threaded response listener.
    * Returns boolean success.
    * 
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
         try {

            Future<AbstractData> f = Executors.newSingleThreadExecutor()
                  .submit(new ServiceResponseReceiver(odeProperties, socket));

            ServiceRequest request = new ServiceRequest(dialogId, SemiSequenceID.svcReq,
                  new GroupID(OdeProperties.getJpoOdeGroupId()), requestId);
            UdpUtil.send(socket, request, odeProperties.getSdcIp(), odeProperties.getSdcPort());

            ServiceResponse response = (ServiceResponse) f.get(odeProperties.getServiceRespExpirationSeconds(),
                  TimeUnit.SECONDS);

            if (response.getRequestID().equals(request.getRequestID())) {
               // Matching IDs indicates a successful handshake
               trustEstablished = true;
               String reqid = HexUtils.toHexString(request.getRequestID().byteArrayValue());
               logger.info("Trust established, session requestID: {}", reqid);
            } else {
               trustEstablished = false;
               logger.error("Received ServiceResponse from SDC but the requestID does not match! {} != {}",
                     response.getRequestID(), request.getRequestID());
            }

         } catch (TimeoutException e) {
            trustEstablished = false;
            logger.error("Did not receive Service Response within alotted "
                  + +odeProperties.getServiceRespExpirationSeconds() + " seconds.", e);

         } catch (InterruptedException | ExecutionException | UdpUtilException e) {
            trustEstablished = false;
            logger.error("Trust establishment interrupted.", e);
         }
         --retriesLeft;
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
