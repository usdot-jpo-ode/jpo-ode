/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.ode.udp.trust;

//TODO open-ode
//import java.net.DatagramSocket;
//import java.util.HashMap;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.TimeoutException;
//
//import org.apache.tomcat.util.buf.HexUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.oss.asn1.AbstractData;
//
//import us.dot.its.jpo.ode.OdeProperties;
//import us.dot.its.jpo.ode.udp.UdpUtil;
//import us.dot.its.jpo.ode.udp.UdpUtil.UdpUtilException;
//
///*
// * Trust Session Manager
// * <p>
// * Maintains a hashmap of TemporaryID (session ID) and messages sent in session.
// * Trust session is considered active as long as the hashmap contains TempID.
// * </p>
// */
//public class TrustManager {
//
//   private OdeProperties odeProperties;
//
//   private HashMap<TemporaryID, Integer> sessionList;
//   private Logger logger = LoggerFactory.getLogger(this.getClass());
//   private DatagramSocket socket = null;
//   
//
//   public TrustManager(OdeProperties odeProps, DatagramSocket socket) {
//      this.odeProperties = odeProps;
//      this.socket = socket;
//
//      this.sessionList = new HashMap<>();
//   }
//
//
//   /**
//    * Creates and sends a service request with a threaded response listener.
//    * Returns boolean success.
//    * 
//    * @param destIp
//    * @param destPort
//    * @param requestId
//    * @param dialogId
//    * @return
//    */
//   public boolean establishTrust(TemporaryID requestId, SemiDialogID dialogId) {
//
//      if (this.isTrustEstablished(requestId)) {
//         return true;
//      }
//
//      logger.info("Starting trust establishment...");
//
//      int retriesLeft = odeProperties.getTrustRetries();
//
//      while (retriesLeft > 0 && !this.isTrustEstablished(requestId)) {
//         if (retriesLeft < odeProperties.getTrustRetries()) {
//            logger.debug("Failed to establish trust, retrying {} more time(s).", retriesLeft);
//         }
//         
//         try {
//            Future<AbstractData> f = Executors.newSingleThreadExecutor()
//                  .submit(new ServiceResponseReceiver(odeProperties, socket));
//
//            ServiceRequest request = new ServiceRequest(dialogId, SemiSequenceID.svcReq,
//                  new GroupID(OdeProperties.getJpoOdeGroupId()), requestId);
//            UdpUtil.send(socket, request, odeProperties.getSdcIp(), odeProperties.getSdcPort());
//
//            ServiceResponse response = (ServiceResponse) f.get(odeProperties.getServiceRespExpirationSeconds(),
//                  TimeUnit.SECONDS);
//
//            if (response.getRequestID().equals(request.getRequestID())) {
//               // Matching IDs indicates a successful handshake
//               createSession(requestId);
//               String reqid = HexUtils.toHexString(request.getRequestID().byteArrayValue());
//               logger.info("Trust established, session requestID: {}", reqid);
//            } else {
//               endTrustSession(requestId);
//               logger.error("Received ServiceResponse from SDC but the requestID does not match! {} != {}",
//                     response.getRequestID(), request.getRequestID());
//            }
//
//         } catch (TimeoutException e) {
//            endTrustSession(requestId);
//            logger.error("Did not receive Service Response within alotted "
//                  + +odeProperties.getServiceRespExpirationSeconds() + " seconds.", e);
//
//         } catch (InterruptedException | ExecutionException | UdpUtilException e) {
//            endTrustSession(requestId);
//            logger.error("Trust establishment interrupted.", e);
//         }
//         --retriesLeft;
//      }
//
//      return isTrustEstablished(requestId);
//
//   }
//
//   public boolean isTrustEstablished(TemporaryID requestId) {
//      return (sessionList.containsKey(requestId) && sessionList.get(requestId) < odeProperties.getMessagesUntilTrustReestablished());
//   }
//   
//   public void endTrustSession(TemporaryID requestID) {
//      logger.info("Ending trust session ID {}", requestID);
//      sessionList.remove(requestID);
//   }
//   
//   public void createSession(TemporaryID requestID) {
//      sessionList.put(requestID, 0);
//   }
//   
//   public void incrementSessionTracker(TemporaryID requestID) {
//      sessionList.put(requestID, sessionList.get(requestID)+1);
//   }
//   
//   public Integer getSessionMessageCount(TemporaryID requestID) {
//      return sessionList.get(requestID);
//   }
//}
