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
package us.dot.its.jpo.ode.udp.isd;

//TODO open-ode
//import java.io.IOException;
//import java.net.DatagramSocket;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.oss.asn1.AbstractData;
//import com.oss.asn1.DecodeFailedException;
//import com.oss.asn1.DecodeNotSupportedException;
//
//import us.dot.its.jpo.ode.OdeProperties;
//import us.dot.its.jpo.ode.udp.AbstractConcurrentUdpReceiver;
//
//public class ServiceResponseReceiver extends AbstractConcurrentUdpReceiver {
//
//   private Logger logger = LoggerFactory.getLogger(this.getClass());
//
//   public ServiceResponseReceiver(OdeProperties odeProps, DatagramSocket sock) {
//      super(sock, odeProps.getServiceResponseBufferSize());
//      logger.debug("ServiceResponseReceiver spawned.");
//   }
//
//   @Override
//   protected AbstractData processPacket(byte[] data)
//         throws DecodeFailedException, DecodeNotSupportedException, IOException {
//      ServiceResponse returnMsg = null;
//
//      AbstractData response = J2735Util.decode(J2735.getPERUnalignedCoder(), data);
//
//      if (response instanceof ServiceResponse) {
//         returnMsg = (ServiceResponse) response;
//         if (J2735Util.isExpired(returnMsg.getExpiration())) {
//            throw new IOException("Received expired ServiceResponse.");
//         }
//
//         String hex = HexUtils.toHexString(data);
//         logger.debug("Received ServiceResponse (hex): {}", hex);
//         logger.debug("Received ServiceResponse (json): {}", returnMsg);
//      }
//      return returnMsg;
//   }
//
//}
